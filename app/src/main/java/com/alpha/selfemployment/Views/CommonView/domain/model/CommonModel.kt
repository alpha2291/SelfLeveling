package com.alpha.selfemployment.Views.CommonView.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommentResponse(
    @SerialName("data")
    val `data`: List<CommentResponseData>,
    @SerialName("error")
    val error: String,
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
data class CommentResponseData(

    // Common
    @SerialName("comment_id")
    val comment_id: Int,

    @SerialName("comment")
    val comment: String,

    @SerialName("created_at")
    val created_at: String,

    @SerialName("username")
    val username: String,

    @SerialName("profile_image")
    val profile_image: String? = null,

    @SerialName("user_id")
    val user_id: Int,

    @SerialName("author")
    val author: Int = 0,

    @SerialName("like_count")
    val like_count: Int = 0,

    @SerialName("is_liked")
    val is_liked: Int = 0,

    @SerialName("is_report")
    val is_report: Int = 0,

    // Reply-specific (NULL for main comments)

    @SerialName("parent_comment_id")
    val parent_comment_id: Int? = null,

    @SerialName("mention_id")
    val mention_id: Int? = null,

    @SerialName("mention_username")
    val mention_username: String? = null,

    // Main-comment-only
    @SerialName("total_reply")
    val total_reply: Int? = null,

    @SerialName("last_reply")
    val last_reply: List<CommentResponseData>? = null
)

