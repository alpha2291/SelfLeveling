package com.alpha.selfemployment.Views.Message.BackendSupport.data.remote

import com.alpha.selfemployment.Views.Home.Videos.domain.model.PostLikeResponse
import com.alpha.selfemployment.Views.Message.BackendSupport.domain.model.ChatBEListResponse
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

interface ChatAPI {


    @POST("message_config")
    suspend fun message_Config(@Body requestBody: RequestBody) : PostLikeResponse


    @POST("chat_list")
    suspend fun chat_List(@Body requestBody: RequestBody) : ChatBEListResponse


}