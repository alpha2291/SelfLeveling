package com.alpha.selfemployment.Views.Home.Videos.domain.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostLikeResponse(
    @SerialName("data")
    val `data`: List<Any>,
    @SerialName("error")
    val error: String,
    @SerialName("message")
    val message: String,
    @SerialName("result")
    val result: String
)