package com.alpha.selfemployment.Views.Explore.domain.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchHistoryResponse(
    @SerialName("data")
    val `data`: List<SearchHistoryResponseData>,
    @SerialName("error")
    val error: String,
    @SerialName("message")
    val message: String,
    @SerialName("result")
    val result: String
)

@Serializable
data class SearchHistoryResponseData(
    @SerialName("created_at")
    val created_at: Long,
    @SerialName("search_id")
    val search_id: Int,
    @SerialName("search_text")
    val search_text: String,
    @SerialName("search_type")
    val search_type: String,
    @SerialName("user_id")
    val user_id: Int
)