package com.alpha.selfemployment.Views.ProfileModule.Settings.network.api

import com.alpha.selfemployment.Startup.domain.model.RegisterResponse
import com.alpha.selfemployment.Views.ProfileModule.Settings.domain.model.GetBlockList
import com.alpha.selfemployment.Views.ProfileModule.Settings.domain.model.LogoutResponse
import com.alpha.selfemployment.Views.ProfileModule.Settings.domain.model.MyInterestResponse
import com.alpha.selfemployment.Views.ProfileModule.Settings.domain.model.NotificationSettings
import com.alpha.selfemployment.Views.ProfileModule.Settings.domain.model.UserNameUpdate
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

interface SettingsInterface {

    @POST("logout")
    suspend fun logout(@Body requestBody: RequestBody) : LogoutResponse

    @POST("deactivate_or_restore_user")
    suspend fun activate_Deactivate_Account(@Body requestBody: RequestBody) : LogoutResponse


    @POST("change_number")
    suspend fun change_Number(@Body requestBody: RequestBody) : RegisterResponse




    @POST("updateProfile")
    suspend fun updateProfile(@Body requestBody: RequestBody) : LogoutResponse



    @POST("updateUsername")
    suspend fun updateUserName(@Body requestBody: RequestBody) : UserNameUpdate



    @POST("notification_setting")
    suspend fun notification_Settings(@Body requestBody: RequestBody) : NotificationSettings


    @POST("getSavedProperties")
    suspend fun savedProperties(@Body requestBody: RequestBody) : NotificationSettings



    @POST("getBlockedList")
    suspend fun getBlockedList(@Body requestBody: RequestBody) : GetBlockList



    @POST("my_interest")
    suspend fun myInterest(@Body requestBody: RequestBody) : MyInterestResponse




}