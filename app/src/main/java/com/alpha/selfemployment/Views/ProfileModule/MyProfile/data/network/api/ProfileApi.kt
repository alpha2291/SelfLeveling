package com.alpha.selfemployment.Views.ProfileModule.MyProfile.data.network.api

import com.alpha.selfemployment.Views.Home.Videos.domain.model.HomeReelsResponseCommon
import com.alpha.selfemployment.Views.Home.Videos.domain.model.PostLikeResponse
import com.alpha.selfemployment.Views.ProfileModule.MyProfile.domain.model.Profile_Response
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

interface ProfileApi {

    @POST("getProfileStats")
    suspend fun getProfile(@Body requestBody: RequestBody) : Profile_Response

    @POST("blockOrUnblockUser")
    suspend fun blockOrUnblock(@Body requestBody: RequestBody) : PostLikeResponse


    @POST("getpost_property")
    suspend fun getProfilePosts(@Body requestBody: RequestBody) : HomeReelsResponseCommon

}