package com.alpha.selfemployment.Views.ProfileModule.Settings.domain.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MyInterestResponse(
    @SerialName("data")
    val `data`: List<MyInterestResponseData>,
    @SerialName("error")
    val error: String,
    @SerialName("message")
    val message: String,
    @SerialName("result")
    val result: String
)

@Serializable
data class MyInterestResponseData(
    @SerialName("created_at")
    val created_at: String,
    @SerialName("id")
    val id: Int,
    @SerialName("interest_id")
    val interest_id: String,
    @SerialName("user_id")
    val user_id: Int
)