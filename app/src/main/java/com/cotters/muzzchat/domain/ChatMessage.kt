package com.cotters.muzzchat.domain

data class ChatMessage(
    val id: Long,
    val content: String,
    val senderId: String,
    val timestamp: Long,
)
