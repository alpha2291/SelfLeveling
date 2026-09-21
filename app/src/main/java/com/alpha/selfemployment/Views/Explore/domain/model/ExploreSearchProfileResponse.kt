package com.alpha.selfemployment.Views.Explore.domain.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExploreSearchProfileResponse(
    @SerialName("data")
    val `data`: List<ExploreSearchProfileResponseData>,
    @SerialName("nxtpage")
    val nxtpage: Int,
    @SerialName("recCnt")
    val recCnt: Int,
    @SerialName("result")
    val result: String,
    @SerialName("totalPages")
    val totalPages: Int
)

@Serializable
data class ExploreSearchProfileResponseData(
    @SerialName("followers")
    val followers: Int,
    @SerialName("following")
    val following: Int,
    @SerialName("im_followed")
    val im_followed: Int,
    @SerialName("isBlocked")
    val isBlocked: Int,
    @SerialName("is_followed")
    val is_followed: Int,
    @SerialName("is_report")
    val is_report: Int,
    @SerialName("name")
    val name: String,
    @SerialName("number")
    val number: String,
    @SerialName("others_page")
    val others_page: Int,
    @SerialName("posts")
    val posts: Int,
    @SerialName("profile_image")
    val profile_image: String,
    @SerialName("user_id")
    val user_id: Int,
    @SerialName("username")
    val username: String
)