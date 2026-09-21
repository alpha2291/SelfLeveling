package com.alpha.selfemployment.Views.CommonView.domain

import com.alpha.selfemployment.ResultHandler
import com.alpha.selfemployment.Views.CommonView.data.network.CommonViewApi
import com.alpha.selfemployment.Views.CommonView.domain.model.CommentResponse
import com.alpha.selfemployment.Views.Home.Videos.domain.model.PostLikeResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.RequestBody
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class CommonRepository(val commonViewApi : CommonViewApi) {
    fun getMainComments(request: RequestBody): Flow<ResultHandler<CommentResponse>> = flow {
        emit(ResultHandler.Loading)

        try {
            val response = commonViewApi.getMainComments(request)
            emit(ResultHandler.Success(response))


            if (response.result == "1" || response.result  ==  "5" || response.result == "3") {
                emit(ResultHandler.Success(response))
            } else {
                emit(ResultHandler.Error(response.error))
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


    fun getReplyComments(request: RequestBody): Flow<ResultHandler<CommentResponse>> = flow {
        emit(ResultHandler.Loading)

        try {
            val response = commonViewApi.getReplyComments(request)


            if (response.result == "1" || response.result  ==  "5" || response.result == "3" ) {
                emit(ResultHandler.Success(response))
            } else {
                emit(ResultHandler.Error(response.error))
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


    fun addComments(request: RequestBody): Flow<ResultHandler<CommentResponse>> = flow {
        emit(ResultHandler.Loading)

        try {
            val response = commonViewApi.addComments(request)
            if (response.result == "1" || response.result  ==  "5" || response.result == "3") {
                emit(ResultHandler.Success(response))
            } else {
                emit(ResultHandler.Error(response.error))
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


    fun commentLike(request: RequestBody): Flow<ResultHandler<PostLikeResponse>> = flow {
        emit(ResultHandler.Loading)

        try {
            val response = commonViewApi.commentLike(request)

            if (response.result == "1" || response.result  ==  "5" || response.result == "3") {
                emit(ResultHandler.Success(response))
            } else {
                emit(ResultHandler.Error(response.error))
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


