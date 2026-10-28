package com.cotters.muzzchat.ui.viewmodel

sealed interface ChatViewEvent {
    data object SwitchUser : ChatViewEvent
    data object ClearChat : ChatViewEvent
    data class ComposeMessageChanged(val newContent: String) : ChatViewEvent
    data object SendMessageTapped : ChatViewEvent
}