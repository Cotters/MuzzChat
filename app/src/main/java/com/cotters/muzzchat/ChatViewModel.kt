package com.cotters.muzzchat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cotters.muzzchat.data.ChatMessage
import com.cotters.muzzchat.data.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class User {
    John, Sarah;

    fun switched(): User {
        return when (this) {
            John -> Sarah
            Sarah -> John
        }
    }
}

data class ChatViewState(
    val currentUser: User,
    val messages: List<ChatViewMessage>,
) {
    val recipient: User
        get() = currentUser.switched()
}

class ChatViewModel(
    val chatRepository: ChatRepository,
) : ViewModel() {

    private val _viewState: MutableStateFlow<ChatViewState> = MutableStateFlow(
        ChatViewState(
            currentUser = User.John,
            messages = emptyList(),
        )
    )
    val viewState: StateFlow<ChatViewState> = _viewState.asStateFlow()

    init {
        viewModelScope.launch {
            val messages = chatRepository.getChatMessages()
            _viewState.update { currentState ->
                currentState.copy(messages = messages.map { toUiModel(it, currentState.currentUser.name) })
            }
        }
    }

    fun switchUser() {
        _viewState.update { currentState ->
            currentState.copy(currentUser = currentState.currentUser.switched())
        }
    }

    fun sendMessage(content: String) {
        if (content.isNotBlank()) {
            chatRepository.sendMessage(content, senderId = viewState.value.currentUser.name)
        }
    }

    private fun toUiModel(
        chatMessage: ChatMessage,
        currentUserId: String,
    ): ChatViewMessage {
        return ChatViewMessage(
            id = chatMessage.id,
            content = chatMessage.content,
            senderId = chatMessage.senderId,
            isOutgoing = chatMessage.senderId == currentUserId,
            timestamp = chatMessage.timestamp,
        )
    }

}

data class ChatViewMessage(
    val id: Int,
    val content: String,
    val senderId: String,
    val isOutgoing: Boolean,
    val timestamp: Long,
)