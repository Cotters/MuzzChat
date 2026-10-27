package com.cotters.muzzchat.ui

data class ChatViewMessage(
    val id: Long,
    val content: String,
    val senderId: String,
    val isOutgoing: Boolean,
    val timestamp: Long,
)