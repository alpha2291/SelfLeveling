package com.alpha.selfemployment.Views.PostUpload.data.network

import com.alpha.selfemployment.Views.PostUpload.domain.model.PostUploadGetCategoryResponse
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface PostUploadAPI {

    @GET("category")
    suspend fun getCategory() : PostUploadGetCategoryResponse


    @GET("get_language")
    suspend fun get_Language() : PostUploadGetCategoryResponse


    @POST("user_post")
    suspend fun user_Posting(@Body requestBody: RequestBody) : PostUploadGetCategoryResponse
}