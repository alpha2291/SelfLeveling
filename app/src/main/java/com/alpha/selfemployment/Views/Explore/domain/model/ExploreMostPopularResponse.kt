package com.alpha.selfemployment.Views.Explore.domain.model

import com.alpha.selfemployment.Views.Home.Videos.domain.model.PostPropertyData
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExploreMostPopularResponse(
    @SerialName("result")
    val result: String = "",

    @SerialName("error")
    val error: String = "",

    @SerialName("message")
    val message: String = "",

    @SerialName("data")
    val data: List<PostPropertyData> = emptyList()
)