package com.alpha.selfemployment.Views.ProfileModule.Following.Followers.domain.repository

import android.net.http.HttpException
import com.alpha.selfemployment.ResultHandler
import com.alpha.selfemployment.Views.ProfileModule.Following.Followers.data.FFInterface
import com.alpha.selfemployment.Views.ProfileModule.Following.Followers.domain.model.FollowUnfollowResponse
import com.alpha.selfemployment.Views.ProfileModule.Following.Followers.domain.model.FollowersFollowingResponse
import com.alpha.selfemployment.Views.ProfileModule.Following.Followers.domain.model.UserBlockUnblockResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.RequestBody
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException


class FFRepository(private val profApi: FFInterface) {

    fun followUnfollow(request: RequestBody):  Flow<ResultHandler<FollowUnfollowResponse>> = flow {
        emit(ResultHandler.Loading)

        try {
            val response = profApi.followUnfollow(request)

            if (response.result == "1" || response.result == "5" || response.result == "3") {
                emit(ResultHandler.Success(response))
            } else {
                emit(ResultHandler.Error(response.error ?: ""))
            }


        }
        catch (e: HttpException) {
            // API error (4xx, 5xx)
            val message = "Server error: "
            emit(ResultHandler.Error(message, e))

        }
        catch (e: UnknownHostException) {
            // No internet
            emit(ResultHandler.Error("No internet connection", e))

        }
        catch (e: SocketTimeoutException) {
            // Timeout
            emit(ResultHandler.Error("Request timed out", e))

        }
        catch (e: IOException) {
            // Network IO ResultHandler
            emit(ResultHandler.Error("Network error occurred", e))

        }
        catch (e: Exception) {
            // Other unexpected errors
            emit(ResultHandler.Error("An unexpected error occurred", e))
        }
    }


    fun followersFollowingWithSearch(request: RequestBody): Flow<ResultHandler<FollowersFollowingResponse>> = flow {
        emit(ResultHandler.Loading)

        try {
            val response = profApi.followersFollowingWithSearch(request)

            if (response.result == "1" || response.result == "5" || response.result == "3") {
                emit(ResultHandler.Success(response))
            } else {
                emit(ResultHandler.Error(response.error ?: ""))
            }


        }
        catch (e: HttpException) {
            // API error (4xx, 5xx)
            val message = "Server error: "
            emit(ResultHandler.Error(message, e))

        }
        catch (e: UnknownHostException) {
            // No internet
            emit(ResultHandler.Error("No internet connection", e))

        }
        catch (e: SocketTimeoutException) {
            // Timeout
            emit(ResultHandler.Error("Request timed out", e))

        }
        catch (e: IOException) {
            // Network IO error
            emit(ResultHandler.Error("Network error occurred", e))

        }
        catch (e: Exception) {
            // Other unexpected errors
            emit(ResultHandler.Error("An unexpected error occurred", e))
        }
    }


    fun userBlockUnblock(request: RequestBody): Flow<ResultHandler<UserBlockUnblockResponse>> = flow {
        emit(ResultHandler.Loading)

        try {
            val response = profApi.userBlockUnblock(request)

            if (response.result == "1" || response.result == "5" || response.result == "3") {
                emit(ResultHandler.Success(response))
            } else {
                emit(ResultHandler.Error(response.error ?: ""))
            }



        }
        catch (e: HttpException) {
            // API error (4xx, 5xx)
            val message = "Server error: "
            emit(ResultHandler.Error(message, e))

        }
        catch (e: UnknownHostException) {
            // No internet
            emit(ResultHandler.Error("No internet connection", e))

        }
        catch (e: SocketTimeoutException) {
            // Timeout
            emit(ResultHandler.Error("Request timed out", e))

        }
        catch (e: IOException) {
            // Network IO error
            emit(ResultHandler.Error("Network error occurred", e))

        }
        catch (e: Exception) {
            // Other unexpected errors
            emit(ResultHandler.Error("An unexpected error occurred", e))
        }
    }

    
}