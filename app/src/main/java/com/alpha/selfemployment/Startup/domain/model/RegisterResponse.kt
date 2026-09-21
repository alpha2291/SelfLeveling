package com.alpha.selfemployment.Startup.domain.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponse(
    @SerialName("data")
    val `data`: List<RegisterResponseData>,
    @SerialName("error")
    val error: String,
    @SerialName("message")
    val message: String,
    @SerialName("result")
    val result: String
)

@Serializable
data class RegisterResponseData(
    @SerialName("name")
    val name: String?,
    @SerialName("otp")
    val otp: String?,
    @SerialName("phone_num")
    val phone_num: String?,
    @SerialName("phone_num_cc")
    val phone_num_cc: String?,
    @SerialName("user_id")
    val user_id: Int?,
    @SerialName("account_deactivated")
    val account_deactivated: Int?,
    @SerialName("account_banned")
    val account_banned: Int?,
    @SerialName("account_deactivate_days")
    val account_deactivate_days: Int?,
)