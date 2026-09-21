package com.alpha.selfemployment.Startup.domain.repository

import android.net.http.HttpException
import com.alpha.selfemployment.ResultHandler
import com.alpha.selfemployment.Startup.domain.model.OtpResponse
import com.alpha.selfemployment.Startup.domain.model.RegisterResponse
import com.alpha.selfemployment.Startup.network.api.AuthInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.RequestBody
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException


class AuthRepository(private val authApi: AuthInterface) {

    fun register(request: RequestBody): Flow<ResultHandler<RegisterResponse>> = flow {
        emit(ResultHandler.Loading)

        try {
            val response = authApi.register(request)
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


    fun verify(request: RequestBody): Flow<ResultHandler<OtpResponse>> = flow {
        emit(ResultHandler.Loading)

        try {
            val response = authApi.verify(request)

            println("response === ${response.result}")

            if (response.result == "1" || response.result == "5" || response.result == "3") {
                emit(ResultHandler.Success(response))
            } else {
                emit(ResultHandler.Error(response.error ?: ""))
            }

        } catch (e: HttpException) {
            println("response === 111")
            // API error (4xx, 5xx)
            val message = "Server error: "
            emit(ResultHandler.Error(message, e))

        } catch (e: UnknownHostException) {
            println("response === 222")
            // No internet
            emit(ResultHandler.Error("No internet connection", e))

        } catch (e: SocketTimeoutException) {
            println("response === 333")
            // Timeout
            emit(ResultHandler.Error("Request timed out", e))

        } catch (e: IOException) {
            println("response === 444")
            // Network IO error
            emit(ResultHandler.Error("Network error occurred", e))

        } catch (e: Exception) {
            println("response === 555")
            // Other unexpected errors
            emit(ResultHandler.Error("An unexpected error occurred", e))
        }
    }


    fun login(request: RequestBody): Flow<ResultHandler<RegisterResponse>> = flow {
        emit(ResultHandler.Loading)

        try {
            val response = authApi.login(request)
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