package com.alpha.selfemployment.Views.ProfileModule.Settings.domain.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationSettings(
    @SerialName("data")
    val `data`: List<NotificationSettingsData>,
    @SerialName("error")
    val error: String,
    @SerialName("message")
    val message: String,
    @SerialName("result")
    val result: String
)

@Serializable
data class NotificationSettingsData(
    @SerialName("allow_notification")
    val allow_notification: String,
    @SerialName("notification_type")
    val notification_type: String,
    @SerialName("user_id")
    val user_id: Int
)