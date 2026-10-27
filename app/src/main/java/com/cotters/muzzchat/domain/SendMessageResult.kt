package com.cotters.muzzchat.domain

sealed interface SendMessageResult {
    data object Success : SendMessageResult
    data class Fail(val message: String) : SendMessageResult
}
