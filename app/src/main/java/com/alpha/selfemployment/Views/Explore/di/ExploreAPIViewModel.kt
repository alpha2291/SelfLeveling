package com.alpha.selfemployment.Views.Explore.di

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alpha.selfemployment.ResultHandler
import com.alpha.selfemployment.UiState
import com.alpha.selfemployment.Views.Explore.domain.model.ExploreMostPopularResponse
import com.alpha.selfemployment.Views.Explore.domain.model.SearchHistoryResponse
import com.alpha.selfemployment.Views.Explore.domain.repository.ExploreRepository
import com.alpha.selfemployment.Views.Home.Videos.domain.model.HomeReelsResponseCommon
import com.alpha.selfemployment.Views.Home.Videos.domain.model.PostLikeResponse
import com.alpha.selfemployment.Views.Home.Videos.domain.model.PostPropertyData
import com.alpha.selfemployment.Views.SharedRepository
import com.alpha.selfemployment.toast
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class ExploreAPIViewModel (
    private val repo: ExploreRepository,
    private val sharedRepo: SharedRepository
) : ViewModel() {


    private var propertySearchPage = 1

    private var isPropertySearchLoading = false

    private var isPropertySearchLastPage = false

    fun getSearchProperty(
        user_id: Int,
        search_text: String,
        loadMore: Boolean = false
    )
    {
        if (isPropertySearchLoading || isPropertySearchLastPage) return

        viewModelScope.launch {

            isPropertySearchLoading = true
            sharedRepo.setLoading(true)

            if (!loadMore) {
                propertySearchPage = 1
                isPropertySearchLastPage = false
            }

            val jsonObject = JSONObject().apply {
                put("user_id", user_id)
                put("search_text", search_text)
                put("page", propertySearchPage)
            }

            val requestBody = jsonObject.toString()
                .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

            try {
                repo.getExploreProperty(requestBody).collect { result ->

                    when (result) {

                        is ResultHandler.Success -> {

                            val response = result.data
                            val newList = response.data

                            // ✅ Last page check
                            if (newList.isNullOrEmpty()) {
                                isPropertySearchLastPage = true
                            } else {
                                propertySearchPage++
                            }

                            // ✅ Update list (append or replace)
                            sharedRepo.setPostCommonItems(
                                newList = newList,
                                isFirstPage = !loadMore
                            )
                        }

                        is ResultHandler.Error -> {
                            sharedRepo.setError(result.message)
                        }

                        else -> Unit
                    }

                    isPropertySearchLoading = false
                    sharedRepo.setLoading(false)
                }

            } catch (e: Exception) {
                isPropertySearchLoading = false
                sharedRepo.setLoading(false)
                sharedRepo.setError(e.message ?: "Something went wrong")
            }
        }
    }

    private var profileSearchPage = 1

    private var isProfileSearchLoading = false

    private var isProfileSearchLastPage = false

    fun searchProfile(
        user_id: Int,
        name: String,
        loadMore: Boolean = false
    )
    {
        if (isProfileSearchLoading || isProfileSearchLastPage) return

        viewModelScope.launch {

            isProfileSearchLoading = true
            sharedRepo.setLoading(true)

            if (!loadMore) {
                profileSearchPage = 1
                isProfileSearchLastPage = false
            }

            val jsonObject = JSONObject().apply {
                put("user_id", user_id)
                put("name", name)
                put("page", profileSearchPage)
            }

            val requestBody = jsonObject.toString()
                .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

            try {
                repo.searchProfile(requestBody).collect { result ->

                    when (result) {

                        is ResultHandler.Success -> {

                            val response = result.data
                            val newList = response.data

                            // ✅ Last page check
                            if (newList.isNullOrEmpty()) {
                                isProfileSearchLastPage = true
                            } else {
                                profileSearchPage++
                            }

                            // ✅ Update list (append or replace)
                            sharedRepo.setProfileSearch(
                                newList = newList,
                                isFirstPage = !loadMore
                            )
                        }

                        is ResultHandler.Error -> {
                            sharedRepo.setError(result.message)
                        }

                        else -> Unit
                    }

                    isProfileSearchLoading = false
                    sharedRepo.setLoading(false)
                }

            } catch (e: Exception) {
                isProfileSearchLoading = false
                sharedRepo.setLoading(false)
                sharedRepo.setError(e.message ?: "Something went wrong")
            }
        }
    }



    fun searchHistory(
        user_id: Int,
        resultHandler: (ResultHandler<SearchHistoryResponse>) -> Unit
    ) {
        viewModelScope.launch {

            resultHandler(ResultHandler.Loading)

            val jsonObject = JSONObject().apply {
                put("user_id", user_id)

            }

            val requestBody = jsonObject.toString()
                .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

            try {
                repo.getSearchHistory(requestBody).collect { result ->

                    when (result) {


                        is ResultHandler.Success -> {
                           // _profileData.value = UiState.Success(result.data.data.first())
                            resultHandler(ResultHandler.Success(result.data))
                        }

                        is ResultHandler.Error -> {
                            toast(message = result.message)
                           // _profileData.value = UiState.Error(result.message)
                            resultHandler(ResultHandler.Error(result.message))
                        }

                        else -> Unit
                    }
                }
            } catch (e: Exception) {
                toast(message = e.message ?: "")
               // _profileData.value = UiState.Error(e.message ?: "")
                resultHandler(ResultHandler.Error(e.message ?: ""))
            }
        }
    }



    fun deleteSearchHistory(
        user_id: Int,
        delete_type : String,
        search_id : String,
        resultHandler: (ResultHandler<PostLikeResponse>) -> Unit
    ) {
        viewModelScope.launch {

            resultHandler(ResultHandler.Loading)

            val jsonObject = JSONObject().apply {
                put("user_id", user_id)
                put("delete_type", delete_type)
                put("search_id", search_id)

            }

            val requestBody = jsonObject.toString()
                .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

            try {
                repo.deleteSearchHistory(requestBody).collect { result ->

                    when (result) {


                        is ResultHandler.Success -> {
                           // _profileData.value = UiState.Success(result.data.data.first())
                            resultHandler(ResultHandler.Success(result.data))
                        }

                        is ResultHandler.Error -> {
                            toast(message = result.message)
                           // _profileData.value = UiState.Error(result.message)
                            resultHandler(ResultHandler.Error(result.message))
                        }

                        else -> Unit
                    }
                }
            } catch (e: Exception) {
                toast(message = e.message ?: "")
               // _profileData.value = UiState.Error(e.message ?: "")
                resultHandler(ResultHandler.Error(e.message ?: ""))
            }
        }
    }



    // In ExploreAPIViewModel — change exploreMostPopular to callback style
    fun exploreMostPopular(user_id: Int, onResult: (ResultHandler<ExploreMostPopularResponse>) -> Unit) {
        viewModelScope.launch {
            onResult(ResultHandler.Loading)

            val jsonObject = JSONObject().apply { put("user_id", user_id) }
            val requestBody = jsonObject.toString()
                .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

            try {
                repo.exploreMostPopular(requestBody).collect { result ->
                    onResult(result)
                }
            } catch (e: Exception) {
                onResult(ResultHandler.Error(e.message ?: "Unexpected error"))
            }
        }
    }
}

