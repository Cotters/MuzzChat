package com.cotters.muzzchat

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Random

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
    val messages: List<ChatMessage>,
) {
    val recipient: User
        get() = currentUser.switched()
}

class ChatViewModel : ViewModel() {

    private val _viewState: MutableStateFlow<ChatViewState> = MutableStateFlow(
        ChatViewState(
            currentUser = User.John, messages = listOf(
                ChatMessage(content = "Hi John", senderId = "Sarah", isOutgoing = false, timestamp = 0L),
                ChatMessage(
                    content = "Let's do Tuesday. What time?",
                    senderId = "Sarah",
                    isOutgoing = false,
                    timestamp = 0L
                ),
                ChatMessage(content = "Tuesday works. 3pm ok?", senderId = "John", isOutgoing = true, timestamp = 0L),
            )
        )
    )
    val viewState: StateFlow<ChatViewState> = _viewState.asStateFlow()

    fun switchUser() {
        _viewState.update { currentState ->
            currentState.copy(currentUser = currentState.currentUser.switched())
        }
    }

}

data class ChatMessage(
    val id: Int = Random().nextInt(),
    val content: String,
    val senderId: String,
    val isOutgoing: Boolean,
    val timestamp: Long,
)