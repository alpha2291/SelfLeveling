package com.alpha.selfemployment.Views.Message.FirebaseChat.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alpha.selfemployment.AppPreferences
import com.alpha.selfemployment.Views.Message.FirebaseChat.domain.models.PostChatUserItem
import com.alpha.selfemployment.Views.Message.FirebaseChat.domain.repository.ChatRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PostChatUsersViewModel(
    private val chatRepository: ChatRepository,
    private val appPrefs: AppPreferences
) : ViewModel() {

    private val _users     = MutableStateFlow<List<PostChatUserItem>>(emptyList())
    val users: StateFlow<List<PostChatUserItem>> = _users.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var observeJob: Job? = null

    fun observePostUsers(postId: String) {
        val ownerId = appPrefs.getUserId().toString()

        android.util.Log.d("PostChatUsersVM", "observePostUsers postId=$postId ownerId=$ownerId")

        observeJob?.cancel()
        _isLoading.value = true

        observeJob = viewModelScope.launch {
            chatRepository.observePostChats(postId, ownerId).collect { list ->
                android.util.Log.d("PostChatUsersVM", "Received ${list.size} users for post $postId")
                _users.value     = list
                _isLoading.value = false
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        observeJob?.cancel()
    }
}