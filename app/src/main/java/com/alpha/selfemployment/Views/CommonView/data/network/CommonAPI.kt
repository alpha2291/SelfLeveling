package com.alpha.selfemployment.Views.CommonView.data.network

import com.alpha.selfemployment.Views.CommonView.domain.model.CommentResponse
import com.alpha.selfemployment.Views.Home.Videos.domain.model.PostLikeResponse
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

interface CommonViewApi {


    @POST("getcomment")
    suspend fun getMainComments(@Body requestBody: RequestBody): CommentResponse


    @POST("getreplay_comment")
    suspend fun getReplyComments(@Body requestBody: RequestBody): CommentResponse


    @POST("add_firstcomment")
    suspend fun addComments(@Body requestBody: RequestBody): CommentResponse


    @POST("likeComment")
    suspend fun commentLike(@Body requestBody: RequestBody): PostLikeResponse


}