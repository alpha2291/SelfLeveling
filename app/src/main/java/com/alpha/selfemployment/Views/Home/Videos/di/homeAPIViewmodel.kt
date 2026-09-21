package com.alpha.selfemployment.Views.Home.Videos.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alpha.selfemployment.ResultHandler
import com.alpha.selfemployment.UiState
import com.alpha.selfemployment.Views.Home.Videos.domain.model.HomeReelsResponseCommon
import com.alpha.selfemployment.Views.Home.Videos.domain.model.PostLikeResponse
import com.alpha.selfemployment.Views.Home.Videos.domain.repository.HomeRepository
import com.alpha.selfemployment.Views.PostUpload.domain.model.PostUploadGetCategoryResponse
import com.alpha.selfemployment.Views.SharedRepository
import com.alpha.selfemployment.toast
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject


class HomeAPIViewModel(
    private val repo: HomeRepository,
    private val sharedRepo: SharedRepository
) : ViewModel() {

    // Pagination states
    private var reelsPage = 1
    private var articlesPage = 1

    private var isReelsLoading = false
    private var isArticlesLoading = false

    private var isReelsLastPage = false
    private var isArticlesLastPage = false

    // ============================
    // 🔥 GET REELS
    // ============================
    fun getReels(
        user_id: Int,
        user_post_id: Int,
        loadMore: Boolean = false
    ) {
        if (isReelsLoading || isReelsLastPage) return

        viewModelScope.launch {

            isReelsLoading = true
            sharedRepo.setLoading(true)

            if (!loadMore) {
                reelsPage = 1
                isReelsLastPage = false
            }

            val jsonObject = JSONObject().apply {
                put("user_id", user_id)
                put("user_post_id", user_post_id)
                put("page", reelsPage)
            }

            val requestBody = jsonObject.toString()
                .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

            try {
                repo.getReels(requestBody).collect { result ->

                    when (result) {

                        is ResultHandler.Success -> {

                            val response = result.data
                            val newList = response.data

                            // ✅ Last page check
                            if (newList.isNullOrEmpty()) {
                                isReelsLastPage = true
                            } else {
                                reelsPage++
                            }

                            // ✅ Update list (append or replace)
                            sharedRepo.setReels(
                                newList = newList,
                                isFirstPage = !loadMore
                            )
                        }

                        is ResultHandler.Error -> {
                            sharedRepo.setError(result.message)
                        }

                        else -> Unit
                    }

                    isReelsLoading = false
                    sharedRepo.setLoading(false)
                }

            } catch (e: Exception) {
                isReelsLoading = false
                sharedRepo.setLoading(false)
                sharedRepo.setError(e.message ?: "Something went wrong")
            }
        }
    }

    // ============================
    // 🔥 GET ARTICLES
    // ============================
    fun getArticles(
        user_id: Int,
        user_post_id: Int,
        loadMore: Boolean = false
    ) {
        if (isArticlesLoading || isArticlesLastPage) return

        viewModelScope.launch {

            isArticlesLoading = true
            sharedRepo.setLoading(true)

            if (!loadMore) {
                articlesPage = 1
                isArticlesLastPage = false
            }

            val jsonObject = JSONObject().apply {
                put("user_id", user_id)
                put("user_post_id", user_post_id)
                put("page", articlesPage)
            }

            val requestBody = jsonObject.toString()
                .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

            try {
                repo.getArticles(requestBody).collect { result ->

                    when (result) {

                        is ResultHandler.Success -> {

                            val response = result.data
                            val newList = response.data

                            if (newList.isNullOrEmpty()) {
                                isArticlesLastPage = true
                            } else {
                                articlesPage++
                            }

                            sharedRepo.setArticles(
                                newList = newList,
                                isFirstPage = !loadMore
                            )
                        }

                        is ResultHandler.Error -> {
                            sharedRepo.setError(result.message)
                        }

                        else -> Unit
                    }

                    isArticlesLoading = false
                    sharedRepo.setLoading(false)
                }

            } catch (e: Exception) {
                isArticlesLoading = false
                sharedRepo.setLoading(false)
                sharedRepo.setError(e.message ?: "Something went wrong")
            }
        }
    }


    fun postLike(
        user_id: Int,
        user_post_id: Int,
        status: String,
        resultHandler: (ResultHandler<PostLikeResponse>) -> Unit
    ) {
        viewModelScope.launch {



            val jsonObject = JSONObject().apply {
                put("user_id", user_id)
                put("user_post_id", user_post_id)
                put("status", status)
            }

            val requestBody = jsonObject.toString()
                .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

            try {
                repo.post_Like(requestBody).collect { result ->

                    when (result) {
                        is ResultHandler.Success -> {
                            resultHandler(ResultHandler.Success(result.data))
                        }

                        is ResultHandler.Error -> {
                            toast(message = result.message)
                            resultHandler(ResultHandler.Error(result.message))
                        }

                        is ResultHandler.Loading -> {
                            resultHandler(ResultHandler.Loading)
                        }

                        else -> {}
                    }
                }
            } catch (e: Exception) {
                toast(message = e.message ?: "")
                resultHandler(ResultHandler.Error( e.message ?: ""))
            }
        }
    }



    fun notinterest(
        user_id: Int,
        user_post_id: Int,
        statement: String,
        resultHandler: (ResultHandler<PostLikeResponse>) -> Unit
    )
    {
        viewModelScope.launch {



            val jsonObject = JSONObject().apply {
                put("user_id", user_id)
                put("user_post_id", user_post_id)
                put("statement", statement)
            }

            val requestBody = jsonObject.toString()
                .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

            try {
                repo.notInterest(requestBody).collect { result ->

                    when (result) {
                        is ResultHandler.Success -> {
                            resultHandler(ResultHandler.Success(result.data))
                        }

                        is ResultHandler.Error -> {
                            toast(message = result.message)
                            resultHandler(ResultHandler.Error(result.message))
                        }

                        is ResultHandler.Loading -> {
                            resultHandler(ResultHandler.Loading)
                        }

                        else -> {}
                    }
                }
            } catch (e: Exception) {
                toast(message = e.message ?: "")
                resultHandler(ResultHandler.Error( e.message ?: ""))
            }
        }
    }



    fun postSave(
        user_id: Int,
        user_post_id: Int,
        status: String,
        resultHandler: (ResultHandler<PostLikeResponse>) -> Unit
    ) {
        viewModelScope.launch {



            val jsonObject = JSONObject().apply {
                put("user_id", user_id)
                put("user_post_id", user_post_id)
                put("status", status)
            }

            val requestBody = jsonObject.toString()
                .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

            try {
                repo.post_Save(requestBody).collect { result ->

                    when (result) {
                        is ResultHandler.Success -> {
                            resultHandler(ResultHandler.Success(result.data))
                        }

                        is ResultHandler.Error -> {
                            toast(message = result.message)
                            resultHandler(ResultHandler.Error(result.message))
                        }

                        is ResultHandler.Loading -> {
                            resultHandler(ResultHandler.Loading)
                        }

                        else -> {}
                    }
                }
            } catch (e: Exception) {
                toast(message = e.message ?: "")
                resultHandler(ResultHandler.Error( e.message ?: ""))
            }
        }
    }
}