package com.alpha.selfemployment.Views.Message.FirebaseChat.domain.models

data class PresenceModel(
    val online: Boolean = false,
    val lastSeen: Long = 0L,
    val typingInChatId: String = ""
)