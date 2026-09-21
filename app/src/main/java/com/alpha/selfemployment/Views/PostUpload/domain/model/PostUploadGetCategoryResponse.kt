package com.alpha.selfemployment.Views.PostUpload.domain.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostUploadGetCategoryResponse(
    @SerialName("data")
    val `data`: List<PostUploadGetCategoryResponseData>,
    @SerialName("error")
    val error: String,
    @SerialName("message")
    val message: String,
    @SerialName("result")
    val result: String
)

@Serializable
data class PostUploadGetCategoryResponseData(
    @SerialName("category_name")
    val category_name: String?,
    @SerialName("language_name")
    val language_name: String?,
    @SerialName("created_at")
    val created_at: String,
    @SerialName("id")
    val id: Int
)