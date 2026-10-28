package com.cotters.muzzchat.ui.viewmodel

import com.cotters.muzzchat.domain.User
import com.cotters.muzzchat.ui.ChatViewMessage

data class ChatViewState(
    val currentUser: User,
    val listItems: List<ChatListItem>,
) {
    val recipient: User
        get() = currentUser.switched()
}

sealed interface ChatListItem {
    data class MessageBlock(val messages: List<ChatViewMessage>) : ChatListItem
    data class TimeSeparator(val timestamp: Long) : ChatListItem
}