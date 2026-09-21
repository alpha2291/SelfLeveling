package com.alpha.selfemployment.Views.Home.Videos.domain.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HomeReelsResponseCommon(
    @SerialName("current_page")
    val current_page: Int,
    @SerialName("data")
    val `data`: List<PostPropertyData>,
    @SerialName("next_page")
    val next_page: Int,
    @SerialName("result")
    val result: String,
    @SerialName("total_records")
    val total_records: Int
)

@Serializable
data class PostPropertyData(
    @SerialName("is_liked")
    val is_liked: Int,
    @SerialName("is_saved")
    val is_saved: Int,
    @SerialName("is_reported") // ✅ NEW
    val is_reported: Int = 0,
    @SerialName("name")
    val name: String,
    @SerialName("post_property")
    val post_property: PostProperty,
    @SerialName("profile_image")
    val profile_image: String,
    @SerialName("total_comments")
    val total_comments: Int,
    @SerialName("total_likes")
    val total_likes: Int,
    @SerialName("user_id")
    val user_id: Int,
    @SerialName("user_post_id")
    val user_post_id: Int,
    @SerialName("username")
    val username: String
)

@Serializable
data class PostProperty(
    @SerialName("category")
    val category: String,
    @SerialName("images")
    val images: List<Image?>,
    @SerialName("language")
    val language: String,
    @SerialName("long_description")
    val long_description: String,
    @SerialName("post_type")
    val post_type: String,
    @SerialName("short_description")
    val short_description: String,
    @SerialName("title")
    val title: String,
    @SerialName("video")
    val video: String
)




@Serializable
data class Image(
    @SerialName("articles_photo")
    val articles_photo: String,
    @SerialName("created_at")
    val created_at: String,
    @SerialName("id")
    val id: Int,
    @SerialName("user_id")
    val user_id: Int,
    @SerialName("user_post_id")
    val user_post_id: Int
)