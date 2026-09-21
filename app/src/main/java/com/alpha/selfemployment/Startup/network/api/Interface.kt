package com.alpha.selfemployment.Startup.network.api

import com.alpha.selfemployment.Startup.domain.model.OtpResponse
import com.alpha.selfemployment.Startup.domain.model.RegisterResponse
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthInterface {

    @POST("register")
    suspend fun register(@Body requestBody: RequestBody): RegisterResponse


    @POST("verifyOtp")
    suspend fun verify(@Body requestBody: RequestBody): OtpResponse


    @POST("login")
    suspend fun login(@Body requestBody: RequestBody): RegisterResponse


}