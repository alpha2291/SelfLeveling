package com.alpha.selfemployment.Views.PostUpload.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alpha.selfemployment.GlobalSnackbar
import com.alpha.selfemployment.ResultHandler
import com.alpha.selfemployment.Views.PostUpload.domain.model.PostUploadGetCategoryResponse
import com.alpha.selfemployment.Views.PostUpload.domain.repository.PostUploadRepository
import com.alpha.selfemployment.Views.ProfileModule.Settings.domain.model.LogoutResponse
import com.alpha.selfemployment.Views.ProfileModule.Settings.domain.model.UserNameUpdate
import com.alpha.selfemployment.navigation.Screen
import com.alpha.selfemployment.toast
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class PostUploadAPIViewModel(
    private val repo : PostUploadRepository
) : ViewModel(){

    fun getCategories(
        resultHandler: (ResultHandler<PostUploadGetCategoryResponse>) -> Unit
    ) {
        viewModelScope.launch {
            resultHandler(ResultHandler.Loading)

            try {
                repo.getCategories().collect { result ->
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
                resultHandler(ResultHandler.Error(e.message ?: ""))
            }
        }
    }


    fun get_Language(
        resultHandler: (ResultHandler<PostUploadGetCategoryResponse>) -> Unit
    ) {
        viewModelScope.launch {
            resultHandler(ResultHandler.Loading)

            try {
                repo.get_Language().collect { result ->
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
                resultHandler(ResultHandler.Error(e.message ?: ""))
            }
        }
    }



    fun user_Post(
        user_id: Int,
        user_post_id: Int,
        language: String,
        title: String,
        post_type: String,
        video: String,
        category: String,
        short_description: String,
        long_description: String,
        images: List<String>,
        resultHandler: (ResultHandler<ResultHandler.Success<PostUploadGetCategoryResponse>>) -> Unit
    ) {
        viewModelScope.launch {

            resultHandler(ResultHandler.Loading)

            val jsonObject = JSONObject().apply {
                put("user_id", user_id)
                put("user_post_id", user_post_id)
                put("language", language)
                put("title", title)
                put("post_type", post_type)
                put("video", video)
                put("category", category)
                put("short_description", short_description)
                put("long_description", long_description)
                put("images", images)
            }

            val requestBody = jsonObject.toString()
                .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

            try {
                repo.user_Posting(requestBody).collect { result ->

                    when (result) {


                        is ResultHandler.Success -> {
                            resultHandler(ResultHandler.Success(result))
                        }

                        is ResultHandler.Error -> {
                            toast(message = result.message)
//                            GlobalSnackbar.show(result.message)
                            resultHandler(ResultHandler.Error(result.message))
                        }

                        else -> Unit
                    }
                }
            } catch (e: Exception) {
                toast(message = e.message ?: "")
                resultHandler(ResultHandler.Error(e.message ?: ""))
            }
        }
    }

}