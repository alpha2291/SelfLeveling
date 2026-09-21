package com.alpha.selfemployment.Views.ProfileModule.Following.Followers.data

import com.alpha.selfemployment.Views.ProfileModule.Following.Followers.domain.model.FollowUnfollowResponse
import com.alpha.selfemployment.Views.ProfileModule.Following.Followers.domain.model.FollowersFollowingResponse
import com.alpha.selfemployment.Views.ProfileModule.Following.Followers.domain.model.UserBlockUnblockResponse
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

interface FFInterface {

    @POST("searchfollower")
    suspend fun followersFollowingWithSearch(@Body requestBody: RequestBody) : FollowersFollowingResponse


    @POST("followUser")
    suspend fun followUnfollow(@Body requestBody: RequestBody) : FollowUnfollowResponse


    @POST("block")
    suspend fun userBlockUnblock(@Body requestBody: RequestBody) : UserBlockUnblockResponse


}