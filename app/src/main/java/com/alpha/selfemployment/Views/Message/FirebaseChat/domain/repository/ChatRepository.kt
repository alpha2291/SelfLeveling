package com.alpha.selfemployment.Views.Message.FirebaseChat.domain.repository


import com.alpha.selfemployment.Views.Message.FirebaseChat.domain.models.ChatThreadModel
import com.alpha.selfemployment.Views.Message.FirebaseChat.domain.models.FirebaseUserModel
import com.alpha.selfemployment.Views.Message.FirebaseChat.domain.models.MessageModel
import com.alpha.selfemployment.Views.Message.FirebaseChat.domain.models.PostChatUserItem
import com.alpha.selfemployment.Views.Message.FirebaseChat.domain.models.PresenceModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ChatRepository(
    private val db: FirebaseDatabase
) {

    private val rootRef         get() = db.reference
    private val usersRef        get() = db.getReference("users")
    private val chatThreadsRef  get() = db.getReference("chatThreads")
    private val messagesRef     get() = db.getReference("messages")
    private val presenceRef     get() = db.getReference("presence")

    // ─────────────────────────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────────────────────────

    /**
     * chatId = "{postId}_{smallerUserId}_{largerUserId}"
     * The post owner is ALWAYS stored as ownerId in the thread,
     * regardless of their numeric userId.
     */
    fun buildChatId(postId: String, userA: String, userB: String): String {
        val sorted = listOf(userA, userB).sorted()
        return "${postId}_${sorted[0]}_${sorted[1]}"
    }

    // ─────────────────────────────────────────────────────────────
    // USERS NODE
    // ─────────────────────────────────────────────────────────────

    suspend fun createOrUpdateUserNode(
        userId: String,
        name: String,
        username: String,
        phone: String,
        avatarUrl: String = "",
        email: String = ""
    ): Result<Unit> = runCatching {
        val userRef  = usersRef.child(userId)
        val existing = userRef.get().await()

        if (!existing.exists()) {
            val userMap = mapOf(
                "userId"       to userId,
                "name"         to name,
                "username"     to username,
                "phone"        to phone,
                "avatarUrl"    to avatarUrl,
                "email"        to email,
                "createdAt"    to ServerValue.TIMESTAMP,
                "lastActiveAt" to ServerValue.TIMESTAMP
            )
            userRef.setValue(userMap).await()
        } else {
            val updates = mapOf<String, Any>(
                "name"         to name,
                "username"     to username,
                "phone"        to phone,
                "avatarUrl"    to avatarUrl,
                "email"        to email,
                "lastActiveAt" to ServerValue.TIMESTAMP
            )
            userRef.updateChildren(updates).await()
        }
    }

    suspend fun getUserOnce(userId: String): FirebaseUserModel? {
        val snap = usersRef.child(userId).get().await()
        return snap.getValue(FirebaseUserModel::class.java)
    }

    fun observeUser(userId: String): Flow<FirebaseUserModel?> = callbackFlow {
        val ref      = usersRef.child(userId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.getValue(FirebaseUserModel::class.java))
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    // ─────────────────────────────────────────────────────────────
    // CREATE CHAT THREAD + DEFAULT MESSAGE
    // Idempotent — safe to call multiple times for the same chat.
    // ─────────────────────────────────────────────────────────────

    suspend fun createChatAndSendDefaultMessage(
        postId: String,

        // The person who OWNS the post (receives enquiries)
        ownerId:          String,
        ownerName:        String,
        ownerUsername:    String,
        ownerPhone:       String,
        ownerProfileImage: String,

        // The person ENQUIRING about the post
        recipientId:          String,
        recipientName:        String,
        recipientUsername:    String,
        recipientPhone:       String,
        recipientProfileImage: String,

        defaultMessage: String
    ): String {
        val chatId       = buildChatId(postId, ownerId, recipientId)
        val chatThreadRef = chatThreadsRef.child(chatId)
        val existing      = chatThreadRef.get().await()

        if (!existing.exists()) {
            val messageRef = messagesRef.child(chatId).push()
            val messageId  = messageRef.key ?: throw IllegalStateException("Message key null")

            val threadMap = mapOf(
                "chatId"   to chatId,
                "postId"   to postId,

                "ownerId"          to ownerId,
                "ownerName"        to ownerName,
                "ownerUsername"    to ownerUsername,
                "ownerPhone"       to ownerPhone,
                "ownerProfileImage" to ownerProfileImage,

                "recipientId"          to recipientId,
                "recipientName"        to recipientName,
                "recipientUsername"    to recipientUsername,
                "recipientPhone"       to recipientPhone,
                "recipientProfileImage" to recipientProfileImage,

                "createdAt"           to ServerValue.TIMESTAMP,
                "updatedAt"           to ServerValue.TIMESTAMP,
                "lastMessage"         to defaultMessage,
                "lastMessageAt"       to ServerValue.TIMESTAMP,
                // The enquirer (recipientId) sends the first message
                "lastMessageSenderId" to recipientId,

                // Owner has 1 unread (the enquiry just arrived)
                "ownerUnreadCount"     to 1,
                "recipientUnreadCount" to 0,

                "ownerDeleted"         to false,
                "recipientDeleted"     to false,
                "ownerBlockedRecipient" to false,
                "recipientBlockedOwner" to false
            )

            val messageMap = mapOf(
                "messageId"          to messageId,
                "chatId"             to chatId,
                "senderId"           to recipientId,
                "receiverId"         to ownerId,
                "text"               to defaultMessage,
                "timestamp"          to ServerValue.TIMESTAMP,
                "edited"             to false,
                "deletedForEveryone" to false,
                "deletedFor"         to emptyMap<String, Boolean>()
            )

            val updates = hashMapOf<String, Any>(
                "/chatThreads/$chatId"          to threadMap,
                "/messages/$chatId/$messageId"  to messageMap
            )
            rootRef.updateChildren(updates).await()
        }

        return chatId
    }

    // ─────────────────────────────────────────────────────────────
    // SEND MESSAGE
    // ─────────────────────────────────────────────────────────────

    suspend fun sendMessage(
        chatId:     String,
        senderId:   String,
        receiverId: String,
        text:       String
    ): Result<Unit> = runCatching {
        val messageRef = messagesRef.child(chatId).push()
        val messageId  = messageRef.key ?: throw IllegalStateException("Message key null")

        val threadSnap = chatThreadsRef.child(chatId).get().await()
        val thread     = threadSnap.getValue(ChatThreadModel::class.java)
            ?: throw IllegalStateException("Chat thread not found for chatId=$chatId")

        // Increment the RECEIVER's unread count only
        val ownerUnread = when (receiverId) {
            thread.ownerId     -> thread.ownerUnreadCount + 1
            else               -> thread.ownerUnreadCount
        }
        val recipientUnread = when (receiverId) {
            thread.recipientId -> thread.recipientUnreadCount + 1
            else               -> thread.recipientUnreadCount
        }

        val messageMap = mapOf(
            "messageId"          to messageId,
            "chatId"             to chatId,
            "senderId"           to senderId,
            "receiverId"         to receiverId,
            "text"               to text,
            "timestamp"          to ServerValue.TIMESTAMP,
            "edited"             to false,
            "deletedForEveryone" to false,
            "deletedFor"         to emptyMap<String, Boolean>()
        )

        val threadUpdates = mapOf<String, Any>(
            "lastMessage"         to text,
            "lastMessageAt"       to ServerValue.TIMESTAMP,
            "lastMessageSenderId" to senderId,
            "updatedAt"           to ServerValue.TIMESTAMP,
            "ownerUnreadCount"    to ownerUnread,
            "recipientUnreadCount" to recipientUnread,
            // Sending a message un-deletes the thread for the sender
            if (senderId == thread.ownerId) "ownerDeleted" to false
            else "recipientDeleted" to false
        )

        // Atomic write: message + thread update together
        val updates = hashMapOf<String, Any>(
            "/messages/$chatId/$messageId"  to messageMap,
            "/chatThreads/$chatId/lastMessage"          to text,
            "/chatThreads/$chatId/lastMessageAt"        to ServerValue.TIMESTAMP,
            "/chatThreads/$chatId/lastMessageSenderId"  to senderId,
            "/chatThreads/$chatId/updatedAt"            to ServerValue.TIMESTAMP,
            "/chatThreads/$chatId/ownerUnreadCount"     to ownerUnread,
            "/chatThreads/$chatId/recipientUnreadCount" to recipientUnread
        )
        rootRef.updateChildren(updates).await()
    }

    // ─────────────────────────────────────────────────────────────
    // OBSERVE MESSAGES  (real-time)
    // ─────────────────────────────────────────────────────────────

    fun observeMessages(chatId: String): Flow<List<MessageModel>> = callbackFlow {
        val ref      = messagesRef.child(chatId).orderByChild("timestamp")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children
                    .mapNotNull { it.getValue(MessageModel::class.java) }
                    .filter { !it.deletedForEveryone }   // hide deleted-for-everyone
                    .sortedBy { it.timestamp }
                trySend(list)
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    // ─────────────────────────────────────────────────────────────
    // OBSERVE POST ENQUIRERS  (owner's "Interested Users" screen)
    //
    // FIX: We can't use orderByChild("postId") without a Firebase
    // index — it silently returns nothing.  Instead we listen to
    // ALL chatThreads and filter client-side.  This is fine because
    // a single post won't have thousands of threads.
    // ─────────────────────────────────────────────────────────────

    fun observePostChats(postId: String, ownerId: String): Flow<List<PostChatUserItem>> = callbackFlow {
        // Listen to ALL threads (no index needed)
        val ref      = chatThreadsRef
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val threadList = snapshot.children
                    .mapNotNull { it.getValue(ChatThreadModel::class.java) }
                    .filter { thread ->
                        thread.postId   == postId    // right post
                                && thread.ownerId == ownerId    // current user is the post owner
                                && !thread.ownerDeleted         // not deleted by owner
                    }

                android.util.Log.d("ChatRepo", "observePostChats postId=$postId ownerId=$ownerId → ${threadList.size} threads")

                if (threadList.isEmpty()) {
                    trySend(emptyList())
                    return
                }

                // Fetch all users in one shot
                usersRef.get()
                    .addOnSuccessListener { usersSnapshot ->
                        val result = threadList.map { thread ->
                            val userSnap = usersSnapshot.child(thread.recipientId)

                            PostChatUserItem(
                                chatId       = thread.chatId,
                                userId       = thread.recipientId,
                                name         = userSnap.child("name").getValue(String::class.java)
                                    .orEmpty().ifBlank { thread.recipientName },
                                username     = userSnap.child("username").getValue(String::class.java)
                                    .orEmpty().ifBlank { thread.recipientUsername },
                                phone        = userSnap.child("phone").getValue(String::class.java)
                                    .orEmpty().ifBlank { thread.recipientPhone },
                                profileImage = userSnap.child("avatarUrl").getValue(String::class.java)
                                    .orEmpty().ifBlank { thread.recipientProfileImage },
                                lastMessage   = thread.lastMessage,
                                lastMessageAt = thread.lastMessageAt,
                                // ownerUnreadCount = messages the owner hasn't read yet
                                unreadCount   = thread.ownerUnreadCount,
                                isOnline      = false,
                                lastSeen      = 0L
                            )
                        }.sortedByDescending { it.lastMessageAt }

                        trySend(result)
                    }
                    .addOnFailureListener { e ->
                        android.util.Log.e("ChatRepo", "Failed to fetch users", e)
                        // Fall back to thread-stored names so the list still shows
                        val fallback = threadList.map { thread ->
                            PostChatUserItem(
                                chatId        = thread.chatId,
                                userId        = thread.recipientId,
                                name          = thread.recipientName,
                                username      = thread.recipientUsername,
                                phone         = thread.recipientPhone,
                                profileImage  = thread.recipientProfileImage,
                                lastMessage   = thread.lastMessage,
                                lastMessageAt = thread.lastMessageAt,
                                unreadCount   = thread.ownerUnreadCount,
                                isOnline      = false,
                                lastSeen      = 0L
                            )
                        }.sortedByDescending { it.lastMessageAt }
                        trySend(fallback)
                    }
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    // ─────────────────────────────────────────────────────────────
    // OBSERVE CHAT LIST  (Messages screen — all threads for a user)
    // ─────────────────────────────────────────────────────────────

    fun observeUserChatThreads(currentUserId: String): Flow<List<ChatThreadModel>> = callbackFlow {
        val ref      = chatThreadsRef
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children
                    .mapNotNull { it.getValue(ChatThreadModel::class.java) }
                    .filter { thread ->
                        val asOwner     = thread.ownerId     == currentUserId && !thread.ownerDeleted
                        val asRecipient = thread.recipientId == currentUserId && !thread.recipientDeleted
                        asOwner || asRecipient
                    }
                    .sortedByDescending { it.lastMessageAt }
                trySend(list)
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    // ─────────────────────────────────────────────────────────────
    // MARK READ
    // Call ONLY after the user has actually seen the messages,
    // not immediately on screen entry.
    // ─────────────────────────────────────────────────────────────

    suspend fun markChatAsRead(chatId: String, currentUserId: String): Result<Unit> = runCatching {
        val snap   = chatThreadsRef.child(chatId).get().await()
        val thread = snap.getValue(ChatThreadModel::class.java) ?: return@runCatching

        val field = when (currentUserId) {
            thread.ownerId     -> "ownerUnreadCount"
            thread.recipientId -> "recipientUnreadCount"
            else               -> return@runCatching
        }
        chatThreadsRef.child(chatId).child(field).setValue(0).await()
    }

    // ─────────────────────────────────────────────────────────────
    // DELETE / BLOCK
    // ─────────────────────────────────────────────────────────────

    suspend fun deleteForMe(chatId: String, currentUserId: String): Result<Unit> = runCatching {
        val snap   = chatThreadsRef.child(chatId).get().await()
        val thread = snap.getValue(ChatThreadModel::class.java) ?: return@runCatching

        val field = when (currentUserId) {
            thread.ownerId     -> "ownerDeleted"
            thread.recipientId -> "recipientDeleted"
            else               -> return@runCatching
        }
        chatThreadsRef.child(chatId).child(field).setValue(true).await()
    }

    suspend fun deleteForEveryone(chatId: String, messageId: String): Result<Unit> = runCatching {
        messagesRef.child(chatId).child(messageId).child("deletedForEveryone").setValue(true).await()
    }

    suspend fun deleteMessageForMe(
        chatId: String,
        messageId: String,
        currentUserId: String
    ): Result<Unit> = runCatching {
        messagesRef.child(chatId).child(messageId).child("deletedFor").child(currentUserId).setValue(true).await()
    }

    suspend fun blockUser(chatId: String, blockerId: String): Result<Unit> = runCatching {
        val snap   = chatThreadsRef.child(chatId).get().await()
        val thread = snap.getValue(ChatThreadModel::class.java) ?: return@runCatching

        val field = when (blockerId) {
            thread.ownerId     -> "ownerBlockedRecipient"
            thread.recipientId -> "recipientBlockedOwner"
            else               -> return@runCatching
        }
        chatThreadsRef.child(chatId).child(field).setValue(true).await()
    }

    suspend fun unblockUser(chatId: String, blockerId: String): Result<Unit> = runCatching {
        val snap   = chatThreadsRef.child(chatId).get().await()
        val thread = snap.getValue(ChatThreadModel::class.java) ?: return@runCatching

        val field = when (blockerId) {
            thread.ownerId     -> "ownerBlockedRecipient"
            thread.recipientId -> "recipientBlockedOwner"
            else               -> return@runCatching
        }
        chatThreadsRef.child(chatId).child(field).setValue(false).await()
    }

    // ─────────────────────────────────────────────────────────────
    // PRESENCE
    // ─────────────────────────────────────────────────────────────

    suspend fun setUserOnline(userId: String) {
        val updates = mapOf<String, Any>(
            "online"   to true,
            "lastSeen" to ServerValue.TIMESTAMP
        )
        presenceRef.child(userId).updateChildren(updates).await()
    }

    suspend fun setUserOffline(userId: String) {
        val updates = mapOf<String, Any>(
            "online"          to false,
            "lastSeen"        to ServerValue.TIMESTAMP,
            "typingInChatId"  to ""
        )
        presenceRef.child(userId).updateChildren(updates).await()
    }

    suspend fun setTyping(userId: String, chatId: String, isTyping: Boolean) {
        presenceRef.child(userId).child("typingInChatId")
            .setValue(if (isTyping) chatId else "").await()
    }

    fun observePresence(userId: String): Flow<PresenceModel> = callbackFlow {
        val ref      = presenceRef.child(userId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.getValue(PresenceModel::class.java) ?: PresenceModel())
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }
}