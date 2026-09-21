package com.alpha.selfemployment.Views.Explore.domain.repository

import android.util.Log
import com.alpha.selfemployment.ResultHandler
import com.alpha.selfemployment.Views.CommonView.domain.model.CommentResponse
import com.alpha.selfemployment.Views.Explore.data.network.ExploreAPI
import com.alpha.selfemployment.Views.Explore.domain.model.ExploreMostPopularResponse
import com.alpha.selfemployment.Views.Explore.domain.model.ExploreSearchProfileResponse
import com.alpha.selfemployment.Views.Explore.domain.model.SearchHistoryResponse
import com.alpha.selfemployment.Views.Home.Videos.domain.model.HomeReelsResponseCommon
import com.alpha.selfemployment.Views.Home.Videos.domain.model.PostLikeResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.RequestBody
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ExploreRepository (private val exploreAPI : ExploreAPI){


    fun getExploreProperty(request: RequestBody): Flow<ResultHandler<HomeReelsResponseCommon>> = flow {
        emit(ResultHandler.Loading)

        try {
            val response = exploreAPI.getExploreProperty(request)
            emit(ResultHandler.Success(response))


            if (response.result == "1" || response.result  ==  "5" || response.result == "3") {
                emit(ResultHandler.Success(response))
            } else {
                emit(ResultHandler.Error(response.result))
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




    fun searchProfile(request: RequestBody): Flow<ResultHandler<ExploreSearchProfileResponse>> = flow {
        emit(ResultHandler.Loading)

        try {
            val response = exploreAPI.searchProfile(request)
            emit(ResultHandler.Success(response))


            if (response.result == "1" || response.result  ==  "5" || response.result == "3") {
                emit(ResultHandler.Success(response))
            } else {
                emit(ResultHandler.Error(response.result))
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



    fun getSearchHistory(request: RequestBody): Flow<ResultHandler<SearchHistoryResponse>> = flow {
        emit(ResultHandler.Loading)

        try {
            val response = exploreAPI.getSearchHistory(request)
            emit(ResultHandler.Success(response))


            if (response.result == "1" || response.result  ==  "5" || response.result == "3") {
                emit(ResultHandler.Success(response))
            } else {
                emit(ResultHandler.Error(response.result))
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




    fun deleteSearchHistory(request: RequestBody): Flow<ResultHandler<PostLikeResponse>> = flow {
        emit(ResultHandler.Loading)

        try {
            val response = exploreAPI.deleteSearchHistory(request)
            emit(ResultHandler.Success(response))


            if (response.result == "1" || response.result  ==  "5" || response.result == "3") {
                emit(ResultHandler.Success(response))
            } else {
                emit(ResultHandler.Error(response.result))
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



    fun exploreMostPopular(request: RequestBody): Flow<ResultHandler<ExploreMostPopularResponse>> = flow {
        emit(ResultHandler.Loading)

        try {
            val response = exploreAPI.exploreMostPopular(request)

            Log.d("API_CHECK", "response = $response")
            Log.d("API_CHECK", "result = ${response.result}")
            Log.d("API_CHECK", "data size = ${response.data.size}")

            if (response.result == "1" || response.result == "5" || response.result == "3") {
                emit(ResultHandler.Success(response))
            } else {
                emit(ResultHandler.Error(response.message.ifEmpty { "Something went wrong" }))
            }

        } catch (e: HttpException) {
            Log.e("API_CHECK", "HttpException: ${e.message}", e)
            emit(ResultHandler.Error("Server error", e))

        } catch (e: UnknownHostException) {
            Log.e("API_CHECK", "UnknownHostException: ${e.message}", e)
            emit(ResultHandler.Error("No internet connection", e))

        } catch (e: SocketTimeoutException) {
            Log.e("API_CHECK", "SocketTimeoutException: ${e.message}", e)
            emit(ResultHandler.Error("Request timed out", e))

        } catch (e: IOException) {
            Log.e("API_CHECK", "IOException: ${e.message}", e)
            emit(ResultHandler.Error("Network error occurred", e))

        } catch (e: Exception) {
            Log.e("API_CHECK", "Exception: ${e.message}", e)
            emit(ResultHandler.Error("An unexpected error occurred: ${e.message}", e))
        }
    }
}