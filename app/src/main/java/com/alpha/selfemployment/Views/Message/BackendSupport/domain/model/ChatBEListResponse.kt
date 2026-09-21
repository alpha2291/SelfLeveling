package com.alpha.selfemployment.Views.Message.BackendSupport.domain.model


import com.alpha.selfemployment.Views.Home.Videos.domain.model.PostPropertyData
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatBEListResponse(
    @SerialName("data")
    val `data`: List<ChatBEListResponseData>,
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
data class ChatBEListResponseData(
    @SerialName("chat_type")
    val chat_type: Int,
    @SerialName("delete_post")
    val delete_post: Int,
    @SerialName("enquire_details")
    val enquire_details: CBEEnquireDetails,
    @SerialName("user_details")
    val user_details: List<UserDetail>,
    @SerialName("video_model")
    val video_model: PostPropertyData
)

@Serializable
data class CBEEnquireDetails(
    @SerialName("enquiry_created")
    val enquiry_created: String,
    @SerialName("enquiry_updated")
    val enquiry_updated: String
)





@Serializable
data class UserDetail(
    @SerialName("email_id")
    val email_id: String,
    @SerialName("name")
    val name: String,
    @SerialName("phone_num")
    val phone_num: String,
    @SerialName("phone_num_cc")
    val phone_num_cc: String,
    @SerialName("profile_image")
    val profile_image: String,
    @SerialName("user_id")
    val user_id: Int,
    @SerialName("username")
    val username: String,
    @SerialName("whatsapp_num")
    val whatsapp_num: String,
    @SerialName("whatsapp_num_cc")
    val whatsapp_num_cc: String
)