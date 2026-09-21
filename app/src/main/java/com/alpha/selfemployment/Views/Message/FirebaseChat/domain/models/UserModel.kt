package com.alpha.selfemployment.Views.Message.FirebaseChat.domain.models


data class FirebaseUserModel(
    val userId: String = "",
    val name: String = "",
    val username: String = "",
    val phone: String = "",
    val avatarUrl: String = "",
    val email: String = "",
    val createdAt: Long = 0L,
    val lastActiveAt: Long = 0L
)