package com.alpha.selfemployment.Views.ProfileModule.MyProfile.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alpha.selfemployment.ResultHandler
import com.alpha.selfemployment.UiState
import com.alpha.selfemployment.Views.Home.Videos.domain.model.PostLikeResponse
import com.alpha.selfemployment.Views.ProfileModule.MyProfile.domain.model.Profile_Response
import com.alpha.selfemployment.Views.ProfileModule.MyProfile.domain.model.Profile_ResponseData
import com.alpha.selfemployment.Views.ProfileModule.MyProfile.domain.repository.ProfileRepository
import com.alpha.selfemployment.Views.ProfileModule.Settings.domain.model.LogoutResponse
import com.alpha.selfemployment.Views.SharedRepository
import com.alpha.selfemployment.toast
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class ProfileAPIViewModel(
    private val repo : ProfileRepository,
    private val sharedRepo : SharedRepository
) : ViewModel() {

    private var _profileData = MutableStateFlow<UiState<Profile_ResponseData>>(UiState.Idle)
    var profileData : StateFlow<UiState<Profile_ResponseData>> = _profileData.asStateFlow()




    fun getProfile(
        user_id: Int,
        others_id: Int,
        device_id: String,
        device_type: String,
        device_token: String,
        token: String,
    ) {
        viewModelScope.launch {

            _profileData.value = UiState.Loading


            val jsonObject = JSONObject().apply {
                put("user_id", user_id)
                put("others_id", others_id)
                put("device_id", device_id)
                put("device_type", device_type)
                put("device_token", device_token)
                put("token", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjozLCJpYXQiOjE3NzQzNDY3NzcsImV4cCI6MTc3NjkzODc3N30.j1_R2EmoT-FtZwSDLUld-INy33JQr-zMNy27aekCO8Q")
            }

            val requestBody = jsonObject.toString()
                .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

            try {
                repo.getProfile(requestBody).collect { result ->

                    when (result) {


                        is ResultHandler.Success -> {
                            _profileData.value = UiState.Success(result.data.data.first())
                        }

                        is ResultHandler.Error -> {
                            toast(message = result.message)
                            _profileData.value = UiState.Error(result.message)
                        }

                        else -> Unit
                    }
                }
            } catch (e: Exception) {
                toast(message = e.message ?: "")
                _profileData.value = UiState.Error(e.message ?: "")
            }
        }
    }



    fun userBlockOrUnblock(
        user_id: Int,
        blocker_id: Int,
        status: String,
        resultHandler: (ResultHandler<ResultHandler.Success<PostLikeResponse>>) -> Unit
    ) {
        viewModelScope.launch {

            resultHandler(ResultHandler.Loading)


            val jsonObject = JSONObject().apply {
                put("user_id", user_id)
                put("blocker_id", blocker_id)
                put("status", status)
            }

            val requestBody = jsonObject.toString()
                .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

            try {
                repo.blockOrUnblock(requestBody).collect { result ->

                    when (result) {


                        is ResultHandler.Success -> {
                            resultHandler(ResultHandler.Success(result))
                        }

                        is ResultHandler.Error -> {
                            toast(message = result.message)
                            resultHandler(ResultHandler.Error(result.message))
                        }

                        else -> Unit
                    }
                }
            } catch (e: Exception) {
                //toast(message = e.message ?: "")
                resultHandler(ResultHandler.Error(e.message ?: ""))
            }
        }
    }



    // -------------------------------------------------------------------------
    // LOCAL UI STATE HELPERS
    // -------------------------------------------------------------------------

    private val _isFollowActionLoading = MutableStateFlow(false)
    val isFollowActionLoading: StateFlow<Boolean> = _isFollowActionLoading.asStateFlow()

    fun setFollowActionLoading(value: Boolean) {
        _isFollowActionLoading.value = value
    }

    /**
     * Set blocked state directly
     * 1 = blocked
     * 0 = unblocked
     */
    fun updateBlockedState(isBlocked: Int) {
        val current = (_profileData.value as? UiState.Success)?.data ?: return

        _profileData.value = UiState.Success(
            current.copy(
                is_blocked = isBlocked
            )
        )
    }

    /**
     * Update follow-related states directly
     *
     * is_followed = I follow them
     * im_followed = they follow me
     */
    fun updateFollowState(
        imFollowed: Int,
        isFollowed: Int? = null
    ) {
        val current = (_profileData.value as? UiState.Success)?.data ?: return

        _profileData.value = UiState.Success(
            current.copy(
                im_followed = imFollowed,
                is_followed = isFollowed ?: current.is_followed
            )
        )
    }

    /**
     * Convenience: Block user
     */
    fun onBlock() {
        updateBlockedState(1)
    }

    /**
     * Convenience: Unblock user
     */
    fun onUnblock() {
        updateBlockedState(0)
        updateFollowState(isFollowed = 0 , imFollowed = 0)
    }

    /**
     * Convenience: Follow user
     */
    fun onFollow() {
        val current = (_profileData.value as? UiState.Success)?.data
        println("BEFORE onFollow -> im_followed=${current?.im_followed}, is_followed=${current?.is_followed}")

        updateFollowState(imFollowed = 1)

        val updated = (_profileData.value as? UiState.Success)?.data
        println("AFTER onFollow -> im_followed=${updated?.im_followed}, is_followed=${updated?.is_followed}")
    }

    fun onUnfollow() {
        val current = (_profileData.value as? UiState.Success)?.data
        println("BEFORE onUnfollow -> im_followed=${current?.im_followed}, is_followed=${current?.is_followed}")

        updateFollowState(imFollowed = 0)

        val updated = (_profileData.value as? UiState.Success)?.data
        println("AFTER onUnfollow -> im_followed=${updated?.im_followed}, is_followed=${updated?.is_followed}")
    }

    fun onFollowBack() {
        updateFollowState(imFollowed = 1)
    }




    /// get profilepost

    private var profilePostsPage = 1
    private var isProfilePostsLoading = false
    private var isProfilePostsLastPage = false

    fun getProfilePosts(
        user_id: Int,
        others_id: Int,
        post_type: String,
        loadMore: Boolean = false
    ) {
        viewModelScope.launch {

            // ✅ Reset state when switching tabs / fresh load
            if (!loadMore) {
                profilePostsPage = 1
                isProfilePostsLastPage = false
                sharedRepo.setProfilePosts(emptyList(), isFirstPage = true)
                sharedRepo.setError(null)
            }

            // ✅ Block duplicate API calls
            if (isProfilePostsLoading || (loadMore && isProfilePostsLastPage)) return@launch

            isProfilePostsLoading = true
            sharedRepo.setLoading(true)

            val jsonObject = JSONObject().apply {
                put("user_id", user_id)
                put("others_id", others_id)
                put("post_type", post_type) // "" = all, "1" = videos, "2" = articles
                put("page", profilePostsPage)
            }

            val requestBody = jsonObject.toString()
                .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

            try {
                repo.getProfilePosts(requestBody).collect { result ->

                    when (result) {

                        is ResultHandler.Success -> {
                            val response = result.data
                            val newList = response.data ?: emptyList()

                            // ✅ Check last page
                            val currentList = sharedRepo.profilePosts.value ?: emptyList()

                            val updatedList = if (!loadMore) {
                                newList.distinctBy { it.user_post_id }
                            } else {
                                (currentList + newList).distinctBy { it.user_post_id }
                            }

                            sharedRepo.setProfilePosts(
                                newList = updatedList,
                                isFirstPage = true // because we're already sending final merged list
                            )
                        }

                        is ResultHandler.Error -> {
                            sharedRepo.setError(result.message)
                        }

                        else -> Unit
                    }

                    isProfilePostsLoading = false
                    sharedRepo.setLoading(false)
                }

            } catch (e: Exception) {
                isProfilePostsLoading = false
                sharedRepo.setLoading(false)
                sharedRepo.setError(e.message ?: "Something went wrong")
            }
        }
    }



}