package com.alpha.selfemployment.Views.PostUpload.domain.repository

import com.alpha.selfemployment.ResultHandler
import com.alpha.selfemployment.Views.PostUpload.data.network.PostUploadAPI
import com.alpha.selfemployment.Views.PostUpload.domain.model.PostUploadGetCategoryResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.RequestBody
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException


class PostUploadRepository(
    private val api : PostUploadAPI
) {
    fun getCategories(): Flow<ResultHandler<PostUploadGetCategoryResponse>> = flow {
        emit(ResultHandler.Loading)

        try {
            val response = api.getCategory()
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

    fun get_Language(): Flow<ResultHandler<PostUploadGetCategoryResponse>> = flow {
        emit(ResultHandler.Loading)

        try {
            val response = api.get_Language()
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



    fun user_Posting(requestBody: RequestBody): Flow<ResultHandler<PostUploadGetCategoryResponse>> = flow {
        emit(ResultHandler.Loading)

        try {
            val response = api.user_Posting(requestBody = requestBody)
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