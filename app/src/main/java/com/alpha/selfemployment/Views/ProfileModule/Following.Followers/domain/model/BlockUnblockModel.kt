package com.alpha.selfemployment.Views.ProfileModule.Following.Followers.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserBlockUnblockResponse(
    @SerialName("data")
    val `data`: List<String>,
    @SerialName("error")
    val error: String,
    @SerialName("message")
    val message: String,
    @SerialName("result")
    val result: String
)