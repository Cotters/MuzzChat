package com.cotters.muzzchat.ui.viewmodel

data class MessageComposerState(
    val draft: String = "",
    val isSending: Boolean = false,
    val sendFailed: Boolean = false,
) {
    val canSend: Boolean
        get() = draft.isNotBlank() && !isSending
}
