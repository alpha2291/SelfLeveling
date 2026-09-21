package com.alpha.selfemployment.Startup.domain.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OtpResponse(
    @SerialName("data")
    val `data`: List<OtpResponseData?>,
    @SerialName("error")
    val error: String,
    @SerialName("message")
    val message: String,
    @SerialName("result")
    val result: String
)

@Serializable
data class OtpResponseData(

    @SerialName("email_id")
    val email_id: String,
    @SerialName("location_page")
    val language_page: String,

    @SerialName("name")
    val name: String,
    @SerialName("phone_num")
    val phone_num: String,
    @SerialName("phone_num_cc")
    val phone_num_cc: String,

    @SerialName("token")
    val token: String,
    @SerialName("user_id")
    val user_id: Int,
    @SerialName("username")
    val username: String,
    @SerialName("whatsapp_num")
    val whatsapp_num: String,
    @SerialName("whatsapp_num_cc")
    val whatsapp_num_cc: String
)