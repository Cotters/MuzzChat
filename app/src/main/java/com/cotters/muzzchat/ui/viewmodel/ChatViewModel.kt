package com.cotters.muzzchat.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cotters.muzzchat.data.UserRepository
import com.cotters.muzzchat.domain.IChatRepository
import com.cotters.muzzchat.domain.SendMessageResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatViewModel(
    val userRepository: UserRepository,
    val chatRepository: IChatRepository,
) : ViewModel() {

    private val staticChatId = 1L

    private val _composerState = MutableStateFlow(MessageComposerState())
    val composerState: StateFlow<MessageComposerState> = _composerState.asStateFlow()

    val viewState: StateFlow<ChatViewState> = combine(
        userRepository.currentUser,
        chatRepository.observeChatMessages(chatId = staticChatId),
    ) { currentUser, messages ->
        ChatViewState(
            currentUser = currentUser,
            listItems = UiMessageMapper.buildChatListItems(messages, currentUser.name),
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ChatViewState(
                currentUser = userRepository.currentUser.value,
                listItems = emptyList(),
            )
        )

    init {
        viewModelScope.launch {
            chatRepository.ensureChatExists(chatId = staticChatId)
        }
    }

    fun handleViewEvent(event: ChatViewEvent) {
        when (event) {
            ChatViewEvent.SwitchUser -> switchUser()
            ChatViewEvent.ClearChat -> clearChat()
            is ChatViewEvent.ComposeMessageChanged -> onMessageChanged(event.newContent)
            is ChatViewEvent.SendMessageTapped -> sendMessage()
        }
    }

    private fun switchUser() {
        userRepository.switchUser()
    }

    private fun onMessageChanged(content: String) {
        _composerState.update { current ->
            if (current.isSending) current else current.copy(draft = content, sendFailed = false)
        }
    }

    private fun sendMessage() {
        val composer = _composerState.value
        if (!composer.canSend) return

        val senderId = userRepository.currentUser.value.name
        _composerState.value = composer.copy(isSending = true, sendFailed = false)

        viewModelScope.launch {
            try {
                val sendResult = chatRepository.sendMessage(
                    content = composer.draft.trim(),
                    senderId = senderId,
                    chatId = staticChatId,
                )
                when (sendResult) {
                    SendMessageResult.Success -> _composerState.value = MessageComposerState()
                    is SendMessageResult.Fail -> _composerState.update { it.copy(sendFailed = true) }
                }
            } finally {
                _composerState.update { it.copy(isSending = false) }
            }
        }
    }

    private fun clearChat() {
        viewModelScope.launch {
            chatRepository.clearChat(staticChatId)
        }
    }
}
