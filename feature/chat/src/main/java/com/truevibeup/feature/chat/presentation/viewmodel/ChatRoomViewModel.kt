package com.truevibeup.feature.chat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.truevibeup.core.common.api.ApiResult
import com.truevibeup.core.common.model.Conversation
import com.truevibeup.core.common.model.Message
import com.truevibeup.core.network.repository.ChatRepository
import com.truevibeup.core.network.socket.SocketManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatRoomState(
    val conversation: Conversation? = null,
    val messages: List<Message> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasMore: Boolean = true,
    val otherIsTyping: Boolean = false,
    val messageInput: String = "",
    val error: String? = null,
)

@HiltViewModel
class ChatRoomViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val socketManager: SocketManager,
) : ViewModel() {

    private val _state = MutableStateFlow(ChatRoomState())
    val state: StateFlow<ChatRoomState> = _state

    private var conversationId: Long = -1

    fun init(id: Long) {
        if (conversationId == id) return
        conversationId = id
        loadConversation()
        loadMessages()
        observeSocket()
    }

    private fun loadConversation() {
        viewModelScope.launch {
            when (val result = chatRepository.getConversation(conversationId)) {
                is ApiResult.Success -> _state.value = _state.value.copy(conversation = result.data)
                else -> {}
            }
        }
    }

    fun loadMessages(loadMore: Boolean = false) {
        viewModelScope.launch {
            if (loadMore) {
                if (_state.value.isLoadingMore || !_state.value.hasMore) return@launch
                _state.value = _state.value.copy(isLoadingMore = true)
                val oldest = _state.value.messages.firstOrNull()?.createdAt
                when (val result = chatRepository.getMessages(conversationId, before = oldest)) {
                    is ApiResult.Success -> _state.value = _state.value.copy(
                        messages = result.data + _state.value.messages,
                        isLoadingMore = false,
                        hasMore = result.data.size >= 30,
                    )
                    else -> _state.value = _state.value.copy(isLoadingMore = false)
                }
            } else {
                _state.value = _state.value.copy(isLoading = true)
                when (val result = chatRepository.getMessages(conversationId)) {
                    is ApiResult.Success -> _state.value = _state.value.copy(
                        messages = result.data, isLoading = false,
                        hasMore = result.data.size >= 30,
                    )
                    is ApiResult.Error -> _state.value = _state.value.copy(isLoading = false, error = result.message)
                    else -> {}
                }
            }
        }
    }

    private fun observeSocket() {
        viewModelScope.launch {
            socketManager.newMessage.collect { msg ->
                if (msg.conversationId == conversationId) {
                    _state.value = _state.value.copy(messages = _state.value.messages + msg)
                    socketManager.markRead(msg.id)
                }
            }
        }
        viewModelScope.launch {
            socketManager.typingEvent.collect { (convId, isTyping) ->
                if (convId == conversationId) {
                    _state.value = _state.value.copy(otherIsTyping = isTyping)
                }
            }
        }
    }

    fun setMessageInput(text: String) {
        _state.value = _state.value.copy(messageInput = text)
        socketManager.sendTyping(conversationId, text.isNotBlank())
    }

    fun sendMessage() {
        val text = _state.value.messageInput.trim()
        if (text.isBlank()) return
        _state.value = _state.value.copy(messageInput = "")
        socketManager.sendTyping(conversationId, false)
        socketManager.sendMessage(conversationId, content = text, type = "text")
    }

    fun sendImage(mediaUrl: String) {
        socketManager.sendMessage(conversationId, content = null, type = "image", mediaUrl = mediaUrl)
    }

    fun sendAudio(mediaUrl: String, duration: Int) {
        socketManager.sendMessage(conversationId, content = null, type = "audio", mediaUrl = mediaUrl, duration = duration)
    }
}
