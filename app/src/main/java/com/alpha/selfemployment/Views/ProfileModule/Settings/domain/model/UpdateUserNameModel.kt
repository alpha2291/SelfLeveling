package com.alpha.selfemployment.Views.ProfileModule.Settings.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class UserNameUpdate(
    @SerialName("data")
    val `data`: List<UserNameUpdateData>,
    @SerialName("error")
    val error: String,
    @SerialName("message")
    val message: String,
    @SerialName("result")
    val result: String
)

@Serializable
data class UserNameUpdateData(
    @SerialName("user_id")
    val user_id : Int,
    @SerialName("username")
    val username : String,
)
