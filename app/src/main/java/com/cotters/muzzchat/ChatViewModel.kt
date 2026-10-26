package com.cotters.muzzchat

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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


class ChatViewModel : ViewModel() {

    private val _currentUser: MutableStateFlow<User> = MutableStateFlow(User.John)
    val currentUser: StateFlow<User> = _currentUser.asStateFlow()

    val messages: StateFlow<List<ChatMessage>> = MutableStateFlow(
        listOf(
            ChatMessage(content = "Hi John", senderId = "Sarah", isOutgoing = false, timestamp = 0L),
            ChatMessage(
                content = "Let's do Tuesday. What time?",
                senderId = "Sarah",
                isOutgoing = false,
                timestamp = 0L
            ),
            ChatMessage(content = "Tuesday works. 3pm ok?", senderId = "John", isOutgoing = true, timestamp = 0L),
        )
    ).asStateFlow()

    fun switchUser() {
        _currentUser.value = _currentUser.value.switched()
    }

}

data class ChatMessage(
    val id: Int = Random().nextInt(),
    val content: String,
    val senderId: String,
    val isOutgoing: Boolean,
    val timestamp: Long,
)