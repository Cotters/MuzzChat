package com.cotters.muzzchat.ui

import com.cotters.muzzchat.domain.User

data class ChatViewState(
    val currentUser: User,
    val messages: List<ChatViewMessage>,
) {
    val recipient: User
        get() = currentUser.switched()
}