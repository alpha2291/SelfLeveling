package com.alpha.selfemployment.Views.Message.FirebaseChat.domain.models

data class ChatThreadModel(
    val chatId: String = "",
    val postId: String = "",

    val ownerId: String = "",
    val ownerName: String = "",
    val ownerUsername: String = "",
    val ownerPhone: String = "",
    val ownerProfileImage: String = "",

    val recipientId: String = "",
    val recipientName: String = "",
    val recipientUsername: String = "",
    val recipientPhone: String = "",
    val recipientProfileImage: String = "",

    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
    val lastMessage: String = "",
    val lastMessageAt: Long = 0L,
    val lastMessageSenderId: String = "",

    val ownerUnreadCount: Int = 0,
    val recipientUnreadCount: Int = 0,

    val ownerDeleted: Boolean = false,
    val recipientDeleted: Boolean = false,

    val ownerBlockedRecipient: Boolean = false,
    val recipientBlockedOwner: Boolean = false
)