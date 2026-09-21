package com.alpha.selfemployment.Views.Message.BackendSupport.domain.repository

import com.alpha.selfemployment.ResultHandler
import com.alpha.selfemployment.Views.Home.Videos.domain.model.HomeReelsResponseCommon
import com.alpha.selfemployment.Views.Home.Videos.domain.model.PostLikeResponse
import com.alpha.selfemployment.Views.Message.BackendSupport.data.remote.ChatAPI
import com.alpha.selfemployment.Views.Message.BackendSupport.domain.model.ChatBEListResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.RequestBody
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException


class ChatBERepository(private val chatAPI : ChatAPI)  {


    fun chatConfig(request: RequestBody): Flow<ResultHandler<PostLikeResponse>> = flow {
        emit(ResultHandler.Loading)

        try {
            val response = chatAPI.message_Config( request)
            if (response.result == "1" || response.result == "5" || response.result == "3") {
                emit(ResultHandler.Success(response))
            } else {
                emit(ResultHandler.Error( "Something Went Wrong"))
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


    fun chat_List(request: RequestBody) : Flow<ResultHandler<ChatBEListResponse>> = flow {
        emit(ResultHandler.Loading)

        try {
            val response = chatAPI.chat_List( request)
            if (response.result == "1" || response.result == "5" || response.result == "3") {
                emit(ResultHandler.Success(response))
            } else {
                emit(ResultHandler.Error( "Something Went Wrong"))
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