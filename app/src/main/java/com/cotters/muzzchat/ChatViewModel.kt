package com.cotters.muzzchat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cotters.muzzchat.data.ChatRepository
import com.cotters.muzzchat.data.UserRepository
import com.cotters.muzzchat.domain.ChatMessage
import com.cotters.muzzchat.domain.User
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ChatViewState(
    val currentUser: User,
    val messages: List<ChatViewMessage>,
) {
    val recipient: User
        get() = currentUser.switched()
}

class ChatViewModel(
    val userRepository: UserRepository,
    val chatRepository: ChatRepository,
) : ViewModel() {

    private val staticChatId = 1L

    val viewState: StateFlow<ChatViewState> = combine(
        userRepository.currentUser,
        chatRepository.observeChatMessages(chatId = staticChatId)
    ) { currentUser, messages ->
        ChatViewState(
            currentUser = currentUser,
            messages = messages.map { toUiModel(it, currentUser) },
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ChatViewState(
                currentUser = userRepository.currentUser.value,
                messages = emptyList(),
            )
        )

    fun switchUser() {
        userRepository.switchUser()
    }

    fun sendMessage(content: String) {
        if (content.isNotBlank()) {
            val senderId = userRepository.currentUser.value.name
            viewModelScope.launch {
                chatRepository.sendMessage(content = content, senderId = senderId, chatId = staticChatId)
            }
        }
    }

    private fun toUiModel(chatMessage: ChatMessage, currentUser: User): ChatViewMessage {
        return ChatViewMessage(
            id = chatMessage.id,
            content = chatMessage.content,
            senderId = chatMessage.senderId,
            isOutgoing = chatMessage.senderId == currentUser.name,
            timestamp = chatMessage.timestamp,
        )
    }

}

data class ChatViewMessage(
    val id: Long,
    val content: String,
    val senderId: String,
    val isOutgoing: Boolean,
    val timestamp: Long,
)