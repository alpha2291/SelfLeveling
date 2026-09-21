package com.alpha.selfemployment.Views.Home.Videos.data.remote

import com.alpha.selfemployment.Views.Home.Videos.domain.model.HomeReelsResponseCommon
import com.alpha.selfemployment.Views.Home.Videos.domain.model.PostLikeResponse
import com.alpha.selfemployment.Views.PostUpload.domain.model.PostUploadGetCategoryResponse
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

interface HomeAPI {

    @POST("get_reels")
    suspend fun getReels(@Body requestBody: RequestBody) : HomeReelsResponseCommon

    @POST("get_articles")
    suspend fun getArticles(@Body requestBody: RequestBody) : HomeReelsResponseCommon


    @POST("save_property")
    suspend fun savePost(@Body requestBody: RequestBody) : PostLikeResponse


    @POST("post_like")
    suspend fun likePost(@Body requestBody: RequestBody) : PostLikeResponse

    @POST("not_interest")
    suspend fun notInterest(@Body requestBody: RequestBody) : PostLikeResponse



}