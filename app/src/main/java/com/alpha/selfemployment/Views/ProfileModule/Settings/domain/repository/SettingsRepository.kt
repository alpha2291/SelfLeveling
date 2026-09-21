package com.alpha.selfemployment.Views.ProfileModule.Settings.domain.repository

import retrofit2.HttpException
import com.alpha.selfemployment.ResultHandler
import com.alpha.selfemployment.Startup.domain.model.RegisterResponse
import com.alpha.selfemployment.Views.ProfileModule.Settings.domain.model.GetBlockList
import com.alpha.selfemployment.Views.ProfileModule.Settings.domain.model.LogoutResponse
import com.alpha.selfemployment.Views.ProfileModule.Settings.domain.model.MyInterestResponse
import com.alpha.selfemployment.Views.ProfileModule.Settings.domain.model.NotificationSettings
import com.alpha.selfemployment.Views.ProfileModule.Settings.domain.model.UserNameUpdate
import com.alpha.selfemployment.Views.ProfileModule.Settings.network.api.SettingsInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.RequestBody
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class SettingsRepository(private val settingsApi: SettingsInterface) {

    fun logout(request: RequestBody): Flow<ResultHandler<LogoutResponse>> = flow {
        emit(ResultHandler.Loading)

        try {
            val response = settingsApi.logout(request)
            if (response.result == "1" || response.result == "5" || response.result == "3") {
                emit(ResultHandler.Success(response))
            } else {
                emit(ResultHandler.Error(response.error ?: ""))
            }

        } catch (e: HttpException) {
            // API error (4xx, 5xx)
            val message = "Server error: "
            emit(ResultHandler.Error(message, e))

        } catch (e: UnknownHostException) {
            // No internet
            emit(ResultHandler.Error("No internet connection", e))

        } catch (e: SocketTimeoutException) {
            // Timeout
            emit(ResultHandler.Error("Request timed out", e))

        } catch (e: IOException) {
            // Network IO error
            emit(ResultHandler.Error("Network error occurred", e))

        } catch (e: Exception) {
            // Other unexpected errors
            emit(ResultHandler.Error("An unexpected error occurred", e))
        }
    }

    fun activate_Deactivate_Account(request: RequestBody): Flow<ResultHandler<LogoutResponse>> = flow {
        emit(ResultHandler.Loading)

        try {
            val response = settingsApi.activate_Deactivate_Account(request)
            if (response.result == "1" || response.result == "5" || response.result == "3") {
                emit(ResultHandler.Success(response))
            } else {
                emit(ResultHandler.Error(response.error ?: ""))
            }

        } catch (e: HttpException) {
            // API error (4xx, 5xx)
            val message = "Server error: "
            emit(ResultHandler.Error(message, e))

        } catch (e: UnknownHostException) {
            // No internet
            emit(ResultHandler.Error("No internet connection", e))

        } catch (e: SocketTimeoutException) {
            // Timeout
            emit(ResultHandler.Error("Request timed out", e))

        } catch (e: IOException) {
            // Network IO error
            emit(ResultHandler.Error("Network error occurred", e))

        } catch (e: Exception) {
            // Other unexpected errors
            emit(ResultHandler.Error("An unexpected error occurred", e))
        }
    }

    fun change_Number(request: RequestBody): Flow<ResultHandler<RegisterResponse>> = flow {
        emit(ResultHandler.Loading)

        try {
            val response = settingsApi.change_Number(request)
            if (response.result == "1" || response.result == "5" || response.result == "3") {
                emit(ResultHandler.Success(response))
            } else {
                emit(ResultHandler.Error(response.error ?: ""))
            }

        } catch (e: HttpException) {
            // API error (4xx, 5xx)
            val message = "Server error: "
            emit(ResultHandler.Error(message, e))

        } catch (e: UnknownHostException) {
            // No internet
            emit(ResultHandler.Error("No internet connection", e))

        } catch (e: SocketTimeoutException) {
            // Timeout
            emit(ResultHandler.Error("Request timed out", e))

        } catch (e: IOException) {
            // Network IO error
            emit(ResultHandler.Error("Network error occurred", e))

        } catch (e: Exception) {
            // Other unexpected errors
            emit(ResultHandler.Error("An unexpected error occurred", e))
        }
    }


    fun update_Profile(request: RequestBody): Flow<ResultHandler<LogoutResponse>> = flow {
        emit(ResultHandler.Loading)

        try {
            val response = settingsApi.updateProfile(request)
            if (response.result == "1" || response.result == "5" || response.result == "3") {
                emit(ResultHandler.Success(response))
            } else {
                emit(ResultHandler.Error(response.error ?: ""))
            }

        } catch (e: HttpException) {
            // API error (4xx, 5xx)
            val message = "Server error: "
            emit(ResultHandler.Error(message, e))

        } catch (e: UnknownHostException) {
            // No internet
            emit(ResultHandler.Error("No internet connection", e))

        } catch (e: SocketTimeoutException) {
            // Timeout
            emit(ResultHandler.Error("Request timed out", e))

        } catch (e: IOException) {
            // Network IO error
            emit(ResultHandler.Error("Network error occurred", e))

        } catch (e: Exception) {
            // Other unexpected errors
            emit(ResultHandler.Error("An unexpected error occurred", e))
        }
    }


    fun updateUserName(request: RequestBody): Flow<ResultHandler<UserNameUpdate>> = flow {
        emit(ResultHandler.Loading)

        try {
            val response = settingsApi.updateUserName(request)
            if (response.result == "1" || response.result == "5" || response.result == "3") {
                emit(ResultHandler.Success(response))
            } else {
                emit(ResultHandler.Error(response.error ?: ""))
            }

        } catch (e: HttpException) {
            // API error (4xx, 5xx)
            val message = "Server error: "
            emit(ResultHandler.Error(message, e))

        } catch (e: UnknownHostException) {
            // No internet
            emit(ResultHandler.Error("No internet connection", e))

        } catch (e: SocketTimeoutException) {
            // Timeout
            emit(ResultHandler.Error("Request timed out", e))

        } catch (e: IOException) {
            // Network IO error
            emit(ResultHandler.Error("Network error occurred", e))

        } catch (e: Exception) {
            // Other unexpected errors
            emit(ResultHandler.Error("An unexpected error occurred", e))
        }
    }


    fun notification_Settings(request: RequestBody): Flow<ResultHandler<NotificationSettings>> = flow {
        emit(ResultHandler.Loading)

        try {
            val response = settingsApi.notification_Settings(request)
            if (response.result == "1" || response.result == "5" || response.result == "3") {
                emit(ResultHandler.Success(response))
            } else {
                emit(ResultHandler.Error(response.error ?: ""))
            }

        } catch (e: HttpException) {
            // API error (4xx, 5xx)
            val message = "Server error: "
            emit(ResultHandler.Error(message, e))

        } catch (e: UnknownHostException) {
            // No internet
            emit(ResultHandler.Error("No internet connection", e))

        } catch (e: SocketTimeoutException) {
            // Timeout
            emit(ResultHandler.Error("Request timed out", e))

        } catch (e: IOException) {
            // Network IO error
            emit(ResultHandler.Error("Network error occurred", e))

        } catch (e: Exception) {
            // Other unexpected errors
            emit(ResultHandler.Error("An unexpected error occurred", e))
        }
    }



    fun getBlockedList(request: RequestBody): Flow<ResultHandler<GetBlockList>> = flow {
        emit(ResultHandler.Loading)

        try {
            val response = settingsApi.getBlockedList(request)
            if (response.result == "1" || response.result == "5" || response.result == "3") {
                emit(ResultHandler.Success(response))
            } else {
                emit(ResultHandler.Error(response.error ?: ""))
            }

        } catch (e: HttpException) {
            // API error (4xx, 5xx)
            val message = "Server error: "
            emit(ResultHandler.Error(message, e))

        } catch (e: UnknownHostException) {
            // No internet
            emit(ResultHandler.Error("No internet connection", e))

        } catch (e: SocketTimeoutException) {
            // Timeout
            emit(ResultHandler.Error("Request timed out", e))

        } catch (e: IOException) {
            // Network IO error
            emit(ResultHandler.Error("Network error occurred", e))

        } catch (e: Exception) {
            // Other unexpected errors
            emit(ResultHandler.Error("An unexpected error occurred", e))
        }
    }


    fun myInterest(request: RequestBody): Flow<ResultHandler<MyInterestResponse>> = flow {
        emit(ResultHandler.Loading)

        try {
            val response = settingsApi.myInterest(request)
            if (response.result == "1" || response.result == "5" || response.result == "3") {
                emit(ResultHandler.Success(response))
            } else {
                emit(ResultHandler.Error(response.error ?: ""))
            }

        } catch (e: HttpException) {
            // API error (4xx, 5xx)
            val message = "Server error: "
            emit(ResultHandler.Error(message, e))

        } catch (e: UnknownHostException) {
            // No internet
            emit(ResultHandler.Error("No internet connection", e))

        } catch (e: SocketTimeoutException) {
            // Timeout
            emit(ResultHandler.Error("Request timed out", e))

        } catch (e: IOException) {
            // Network IO error
            emit(ResultHandler.Error("Network error occurred", e))

        } catch (e: Exception) {
            // Other unexpected errors
            emit(ResultHandler.Error("An unexpected error occurred", e))
        }
    }

}