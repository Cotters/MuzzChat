package com.cotters.muzzchat.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cotters.muzzchat.ui.viewmodel.MessageComposerState

@Composable
fun ComposeMessageView(
    state: MessageComposerState,
    onMessageChanged: (String) -> Unit,
    onSendMessage: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            OutlinedTextField(
                value = state.draft,
                onValueChange = onMessageChanged,
                modifier = Modifier.weight(1f),
                enabled = !state.isSending,
                isError = state.sendFailed,
                maxLines = 4,
            )
            Button(
                onClick = onSendMessage,
                enabled = state.canSend,
            ) {
                Text("Send")
            }
        }
        if (state.sendFailed) {
            Text(
                text = "Could not send message. Try again.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}
