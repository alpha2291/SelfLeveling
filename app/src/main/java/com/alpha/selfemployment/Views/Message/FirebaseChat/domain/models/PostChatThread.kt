package com.alpha.selfemployment.Views.Message.FirebaseChat.domain.models


data class PostChatThread(
    val chatId: String = "",
    val postId: String = "",
    val ownerId: String = "",
    val participantId: String = "",
    val participantName: String = "",
    val participantPhone: String = "",
    val createdAt: Long = 0L,
    val lastMessage: String = "",
    val lastMessageAt: Long = 0L
)





data class PostChatUserItem(
    val chatId: String = "",
    val userId: String = "",

    val name: String = "",
    val username: String = "",
    val phone: String = "",
    val profileImage: String = "",

    val lastMessage: String = "",
    val lastMessageAt: Long = 0L,
    val unreadCount: Int = 0,
    val isOnline: Boolean = false,
    val lastSeen: Long = 0L
)