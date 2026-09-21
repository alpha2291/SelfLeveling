package com.alpha.selfemployment.Views.Message.FirebaseChat.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alpha.selfemployment.Views.Message.FirebaseChat.domain.models.MessageModel
import com.alpha.selfemployment.Views.Message.FirebaseChat.domain.repository.ChatRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ConversationUiState(
    val isLoading: Boolean = true,
    val isSending: Boolean = false,
    val messages:  List<MessageModel> = emptyList(),
    val error:     String? = null
)

class ConversationViewModel(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConversationUiState())
    val uiState: StateFlow<ConversationUiState> = _uiState.asStateFlow()

    // Track the active observation job so we can cancel it if chatId changes
    // (shouldn't happen in normal nav, but guards against ViewModel reuse)
    private var observeJob: Job? = null
    private var currentChatId: String? = null

    /**
     * Start listening to messages for [chatId].
     * Safe to call multiple times — re-subscribes only if chatId changed.
     */
    fun observeMessages(chatId: String, currentUserId: String) {
        if (chatId == currentChatId) return   // already observing this chat

        currentChatId = chatId
        observeJob?.cancel()

        _uiState.value = ConversationUiState(isLoading = true)

        observeJob = viewModelScope.launch {
            chatRepository.observeMessages(chatId).collect { list ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    messages  = list,
                    error     = null
                )
            }
        }
    }

    /**
     * Mark the chat as read.
     * Call this ONLY after the message list is visible to the user —
     * NOT immediately on screen entry, to avoid zeroing the count before
     * the user actually sees the messages.
     */
    fun markRead(chatId: String, currentUserId: String) {
        viewModelScope.launch {
            chatRepository.markChatAsRead(chatId, currentUserId)
        }
    }

    fun sendMessage(
        chatId:     String,
        postId:     String,
        senderId:   String,
        receiverId: String,
        text:       String
    ) {
        if (text.isBlank()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSending = true)

            chatRepository.sendMessage(
                chatId     = chatId,
                senderId   = senderId,
                receiverId = receiverId,
                text       = text
            ).onSuccess {
                _uiState.value = _uiState.value.copy(isSending = false)
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    isSending = false,
                    error     = e.message ?: "Failed to send message"
                )
            }
        }
    }

    fun deleteForMe(chatId: String, currentUserId: String) {
        viewModelScope.launch { chatRepository.deleteForMe(chatId, currentUserId) }
    }

    fun deleteForEveryone(chatId: String, messageId: String) {
        viewModelScope.launch { chatRepository.deleteForEveryone(chatId, messageId) }
    }

    fun deleteMessageForMe(chatId: String, messageId: String, currentUserId: String) {
        viewModelScope.launch { chatRepository.deleteMessageForMe(chatId, messageId, currentUserId) }
    }

    fun blockUser(chatId: String, currentUserId: String) {
        viewModelScope.launch { chatRepository.blockUser(chatId, currentUserId) }
    }

    fun unblockUser(chatId: String, currentUserId: String) {
        viewModelScope.launch { chatRepository.unblockUser(chatId, currentUserId) }
    }

    fun setTyping(chatId: String, currentUserId: String, isTyping: Boolean) {
        viewModelScope.launch { chatRepository.setTyping(chatId, currentUserId, isTyping) }
    }

    override fun onCleared() {
        super.onCleared()
        observeJob?.cancel()
    }
}