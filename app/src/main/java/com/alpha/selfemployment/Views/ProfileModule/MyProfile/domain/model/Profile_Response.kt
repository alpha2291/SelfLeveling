package com.alpha.selfemployment.Views.ProfileModule.MyProfile.domain.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Profile_Response(
    @SerialName("data")
    val `data`: List<Profile_ResponseData>,
    @SerialName("result")
    val result: String
)

@Serializable
data class Profile_ResponseData(
    @SerialName("bio")
    val bio: String,
    @SerialName("followers")
    val followers: Int,
    @SerialName("following")
    val following: Int,
    @SerialName("his_blocked")
    val his_blocked: Int,
    @SerialName("im_followed")
    val im_followed: Int,
    @SerialName("is_blocked")
    val is_blocked: Int,
    @SerialName("is_followed")
    val is_followed: Int,
    @SerialName("is_report")
    val is_report: Int,
    @SerialName("name")
    val name: String,
    @SerialName("others_page")
    val others_page: Int,
    @SerialName("phone_num")
    val phone_num: String,
    @SerialName("phone_num_cc")
    val phone_num_cc: String,
    @SerialName("posts")
    val posts: Int,
    @SerialName("profile_image")
    val profile_image: String,
    @SerialName("user_id")
    val user_id: Int,
    @SerialName("username")
    val username: String
)