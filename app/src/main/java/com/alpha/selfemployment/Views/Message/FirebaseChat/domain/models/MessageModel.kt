package com.alpha.selfemployment.Views.Message.FirebaseChat.domain.models

data class MessageModel(
    val messageId: String = "",
    val chatId: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val text: String = "",
    val timestamp: Long = 0L,
    val edited: Boolean = false,
    val deletedForEveryone: Boolean = false,
    val deletedFor: Map<String, Boolean> = emptyMap()
)