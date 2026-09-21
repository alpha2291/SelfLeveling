package com.alpha.selfemployment.Views.ProfileModule.Settings.domain.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetBlockList(
    @SerialName("data")
    val `data`: List<GetBlockListData>,
    @SerialName("error")
    val error: String,
    @SerialName("message")
    val message: String,
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
data class GetBlockListData(
    @SerialName("followers")
    val followers: Int,
    @SerialName("following")
    val following: Int,
    @SerialName("im_followed")
    val im_followed: Int,
    @SerialName("is_blocked")
    val is_blocked: Int,
    @SerialName("is_followed")
    val is_followed: Int,
    @SerialName("name")
    val name: String,
    @SerialName("profile_image")
    val profile_image: String,
    @SerialName("user_id")
    val user_id: Int,
    @SerialName("username")
    val username: String
)