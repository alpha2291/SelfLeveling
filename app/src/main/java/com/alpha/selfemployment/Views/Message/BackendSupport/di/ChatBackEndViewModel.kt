package com.alpha.selfemployment.Views.Message.BackendSupport.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alpha.selfemployment.ResultHandler
import com.alpha.selfemployment.Views.Home.Videos.domain.model.PostLikeResponse
import com.alpha.selfemployment.Views.Message.BackendSupport.domain.model.ChatBEListResponse
import com.alpha.selfemployment.Views.Message.BackendSupport.domain.repository.ChatBERepository
import com.alpha.selfemployment.Views.SharedRepository
import com.alpha.selfemployment.toast
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject


class ChatBackEndViewModel(private val repo : ChatBERepository,
    private val sharedRepo: SharedRepository) : ViewModel() {


    fun msgConfig(
        user_id: Int,
        post_owner_id: Int,
        post_id: Int,
        resultHandler: (ResultHandler<PostLikeResponse>) -> Unit
    )
    {
        viewModelScope.launch {



            val jsonObject = JSONObject().apply {
                put("user_id", user_id)
                put("post_owner_id", post_owner_id)
                put("post_id", post_id)
            }

            val requestBody = jsonObject.toString()
                .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

            try {
                repo.chatConfig(requestBody).collect { result ->

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

    private var chatListPage = 1

    private var isChatListLoading = false

    private var isChatListLastPage = false

    // ============================
    // 🔥 GET REELS
    // ============================
    fun chat_list(
        user_id: Int,
        filter_type: String,
        loadMore: Boolean = false
    ) {
        // ✅ Only block pagination, NOT fresh reload
        if (loadMore && (isChatListLoading || isChatListLastPage)) return

        viewModelScope.launch {
            try {
                isChatListLoading = true
                sharedRepo.setLoading(true)

                // ✅ Fresh reload
                if (!loadMore) {
                    chatListPage = 1
                    isChatListLastPage = false
                    sharedRepo.clearChatList()
                }

                val jsonObject = JSONObject().apply {
                    put("user_id", user_id)
                    put("filter_type", filter_type)
                    put("page", chatListPage)
                }

                android.util.Log.d("ChatBackEndVM", "REQUEST = $jsonObject")

                val requestBody = jsonObject.toString()
                    .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

                repo.chat_List(requestBody).collect { result ->

                    when (result) {

                        is ResultHandler.Success -> {
                            val response = result.data
                            val newList = response.data ?: emptyList()

                            android.util.Log.d("ChatBackEndVM", "API returned size = ${newList.size}")
                            newList.forEach {
                                android.util.Log.d(
                                    "ChatBackEndVM",
                                    "postId=${it.video_model.user_post_id}, chat_type=${it.chat_type}"
                                )
                            }

                            // ✅ Last page check
                            isChatListLastPage = newList.isEmpty() || response.nxtpage == 0

                            if (newList.isNotEmpty()) {
                                chatListPage++
                            }

                            // ✅ Correctly append or replace
                            sharedRepo.setChatList(
                                newList = newList,
                                loadMore = loadMore
                            )
                        }

                        is ResultHandler.Error -> {
                            android.util.Log.e("ChatBackEndVM", "API Error = ${result.message}")
                            sharedRepo.setError(result.message)

                            // optional: clear only on first load
                            if (!loadMore) {
                                sharedRepo.clearChatList()
                            }
                        }

                        else -> Unit
                    }

                    isChatListLoading = false
                    sharedRepo.setLoading(false)
                }

            } catch (e: Exception) {
                android.util.Log.e("ChatBackEndVM", "Exception = ${e.message}", e)
                isChatListLoading = false
                sharedRepo.setLoading(false)

                if (!loadMore) {
                    sharedRepo.clearChatList()
                }

                sharedRepo.setError(e.message ?: "Something went wrong")
            }
        }
    }

}