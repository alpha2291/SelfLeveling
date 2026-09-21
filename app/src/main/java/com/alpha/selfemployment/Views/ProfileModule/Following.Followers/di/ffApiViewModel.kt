package com.alpha.selfemployment.Views.ProfileModule.Following.Followers.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alpha.selfemployment.ResultHandler
import com.alpha.selfemployment.Views.ProfileModule.Following.Followers.domain.model.FollowersFollowingResponse
import com.alpha.selfemployment.Views.ProfileModule.Following.Followers.domain.model.FollowersFollowingResponseData
import com.alpha.selfemployment.Views.ProfileModule.Following.Followers.domain.repository.FFRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

import com.alpha.selfemployment.Views.ProfileModule.Following.Followers.domain.model.FollowUnfollowResponse
import kotlinx.serialization.Serializable


data class FF_ListUiState(
    val list: List<FollowersFollowingResponseData> = emptyList(),
    val isLoading: Boolean = false,
    val isPaging: Boolean = false,
    val isLastPage: Boolean = false,
    val error: String? = null,
    val page: Int = 1,
    val isSearch: Boolean = false,
    val query: String = ""
) {
    companion object {
        const val PAGE_SIZE = 10
    }
}

class FFApiViewModel(
    private val repository: FFRepository
) : ViewModel() {

    private val _ffState = MutableStateFlow(FF_ListUiState())
    val ffState = _ffState.asStateFlow()

    private val lastRequestKeys = mutableMapOf<Int, FFRequestKey>()

    // ✅ Update follow/unfollow state in list
    fun updateFollowState(userId: Int, isFollowed: Int, imFollowed: Int) {
        _ffState.update { old ->
            old.copy(
                list = old.list.map { user ->
                    if (user.user_id == userId)
                        user.copy(is_followed = isFollowed, im_followed = imFollowed)
                    else user
                }
            )
        }
    }

    // ✅ Remove blocked user from list
    fun removeWhenBlocked(userId: Int) {
        _ffState.update { old ->
            old.copy(list = old.list.filter { it.user_id != userId })
        }
    }

    fun clear_FF_List() {
        _ffState.update {
            it.copy(
                list = emptyList(),
                page = 1,
                isLoading = false,
                isPaging = false,
                isLastPage = false,
                error = null
            )
        }
    }

    // ✅ Single entry point — handles both search and normal load
    fun tryLoadFF(requestKey: FFRequestKey, myUserId: Int) {
        val lastKey = lastRequestKeys[requestKey.userId]
        if (lastKey == requestKey && ffState.value.list.isNotEmpty()) return

        lastRequestKeys[requestKey.userId] = requestKey
        clear_FF_List()

        loadFollowersFollowing(
            userId = myUserId,
            othersId = requestKey.userId,
            status = if (requestKey.tab == FF_Profile_TabRow.FOLLOWING) "2" else "1",
            search = requestKey.search,   // empty string = no search
            loadNext = false
        )
    }

    // ✅ Combined followers/following with optional search + pagination
    fun loadFollowersFollowing(
        userId: Int,
        othersId: Int,
        status: String,
        search: String = "",
        loadNext: Boolean = false
    ) {
        val state = _ffState.value
        if (state.isPaging || state.isLastPage) return

        val pageToLoad = if (loadNext) state.page + 1 else 1

        _ffState.update {
            it.copy(
                isLoading = !loadNext,
                isPaging = loadNext,
                error = null,
                page = pageToLoad,
                isSearch = search.isNotBlank(),
                query = search,
                isLastPage = false
            )
        }

        viewModelScope.launch {
            val jsonObject = JSONObject().apply {
                put("user_id", userId)        // ✅ fixed: was using undefined `userId`
                put("others_id", othersId)    // ✅ fixed: was using undefined `othersId`
                put("status", status)
                put("page", pageToLoad)
                if (search.isNotBlank()) put("search", search)
            }

            val requestBody = jsonObject
                .toString()
                .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

            repository.followersFollowingWithSearch(requestBody).collect { result ->
                handleFFResult(result, loadNext)
            }
        }
    }

    // ✅ Central result handler — auto-removes blocked users
    private fun handleFFResult(
        result: ResultHandler<FollowersFollowingResponse>,
        loadNext: Boolean
    ) {
        when (result) {
            is ResultHandler.Loading -> {
                _ffState.update { it.copy(isLoading = !loadNext, isPaging = loadNext) }
            }

            is ResultHandler.Success -> {
                val newItems = result.data.data ?: emptyList()
                val filtered = newItems.filter { it.is_blocked != 1 }
                val isLastPage = newItems.size < FF_ListUiState.PAGE_SIZE

                _ffState.update { old ->
                    val existingIds = old.list.map { it.user_id }.toSet()
                    val deduplicated = if (loadNext)
                        filtered.filter { it.user_id !in existingIds }  // skip duplicates
                    else
                        filtered

                    old.copy(
                        list       = if (loadNext) old.list + deduplicated else deduplicated,
                        isLoading  = false,
                        isPaging   = false,
                        isLastPage = isLastPage,
                        error      = null
                    )
                }
            }

//            is ResultHandler.Success -> {
//                val newItems = result.data.data ?: emptyList()
//
//                // ✅ Filter out blocked users before adding to list
//                val filtered = newItems.filter { it.is_blocked != 1 }
//
//                val isLastPage = newItems.size < FF_ListUiState.PAGE_SIZE
//
//                _ffState.update { old ->
//                    old.copy(
//                        list = if (loadNext) old.list + filtered else filtered,
//                        isLoading = false,
//                        isPaging = false,
//                        isLastPage = isLastPage,
//                        error = null
//                    )
//                }
//            }

            is ResultHandler.Error -> {
                _ffState.update {
                    it.copy(isLoading = false, isPaging = false, error = result.message)
                }
            }

            else -> {}
        }
    }

    /* ----------- Follow / Unfollow ----------- */

    private val _followUnfollowState = MutableStateFlow(FollowUnfollowApiUiState())
    val followUnfollowState: StateFlow<FollowUnfollowApiUiState> = _followUnfollowState.asStateFlow()

    fun followUnfollowApi(
        user_id: Int,
        following_id: Int,
        status: String,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            _followUnfollowState.update { it.copy(isLoading = true, error = null) }

            val jsonObject = JSONObject().apply {
                put("user_id", user_id)
                put("following_id", following_id)
                put("status", status)
            }

            val requestBody = jsonObject
                .toString()
                .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

            try {
                repository.followUnfollow(requestBody).collect { result ->
                    when (result) {
                        is ResultHandler.Success -> {
                            when (result.data.result) {
                                "1" -> {
                                    _followUnfollowState.update {
                                        it.copy(isLoading = false, data = result.data)
                                    }
                                    onResult(true)
                                }
                                "5" -> { // Single login
                                    _followUnfollowState.update { it.copy(isLoading = false, error = "") }
                                    onResult(false)
                                }
                                "3" -> { // Account reactivation
                                    _followUnfollowState.update { it.copy(isLoading = false, error = "") }
                                    onResult(false)
                                }
                                else -> {
                                    _followUnfollowState.update {
                                        it.copy(isLoading = false, error = result.data.error ?: "")
                                    }
                                    onResult(false)
                                }
                            }
                        }
                        is ResultHandler.Error -> {
                            _followUnfollowState.update {
                                it.copy(isLoading = false, error = result.message)
                            }
                            onResult(false)
                        }
                        is ResultHandler.Loading -> {
                            _followUnfollowState.update { it.copy(isLoading = true) }
                        }
                        else -> {}
                    }
                }
            } catch (e: Exception) {
                _followUnfollowState.update {
                    it.copy(isLoading = false, error = e.message ?: "Unexpected error")
                }
                onResult(false)
            }
        }
    }
}

data class FFRequestKey(
    val userId: Int,
    val tab: FF_Profile_TabRow,
    val search: String
)


@Serializable
enum class FF_Profile_TabRow {
    FOLLOWERS , FOLLOWING
}



data class FollowUnfollowApiUiState(
    val isLoading: Boolean = false,
    val data: FollowUnfollowResponse? = null,
    val error: String? = null,
)


