package com.cotters.muzzchat.ui

data class MessageComposerState(
    val draft: String = "",
    val isSending: Boolean = false,
    val sendFailed: Boolean = false,
) {
    val canSend: Boolean
        get() = draft.isNotBlank() && !isSending
}
