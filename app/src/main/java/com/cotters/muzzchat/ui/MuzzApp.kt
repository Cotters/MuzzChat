package com.cotters.muzzchat.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cotters.muzzchat.R
import com.cotters.muzzchat.domain.User
import com.cotters.muzzchat.ui.theme.MuzzChatTheme
import com.cotters.muzzchat.ui.viewmodel.ChatViewState
import com.cotters.muzzchat.ui.viewmodel.MessageComposerState
import com.cotters.muzzchat.ui.viewmodel.UiMessageMapper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MuzzApp(
    viewState: ChatViewState,
    composerState: MessageComposerState,
    onSwitchUserTapped: () -> Unit,
    onClearChatTapped: () -> Unit,
    onMessageChanged: (String) -> Unit,
    onSendMessage: () -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                recipientName = viewState.recipient.name,
                onSwitchUserTapped = onSwitchUserTapped,
                onClearChatTapped = onClearChatTapped
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .imePadding()
                .padding(horizontal = 4.dp),
        ) {
            ChatListView(
                listItems = viewState.listItems,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )
            ComposeMessageView(
                state = composerState,
                onMessageChanged = onMessageChanged,
                onSendMessage = onSendMessage,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBar(
    recipientName: String,
    onSwitchUserTapped: () -> Unit,
    onClearChatTapped: () -> Unit
) {
    var isShowingDropdown by rememberSaveable { mutableStateOf(false) }
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // TODO: Replace with Profile Image.
                Icon(
                    painterResource(R.drawable.ic_launcher_foreground),
                    contentDescription = null,
                    modifier = Modifier.size(50.dp)
                )
                Text(recipientName)
            }
        },
        actions = {
            IconButton(
                onClick = {
                    isShowingDropdown = !isShowingDropdown
                }
            ) {
                val iconId = if (isShowingDropdown) R.drawable.arrow_up else R.drawable.arrow_down
                Icon(
                    painterResource(iconId),
                    contentDescription = null,
                    modifier = Modifier.size(50.dp)
                )
            }
            DropdownMenu(
                isShowingDropdown,
                onDismissRequest = { isShowingDropdown = false }
            ) {
                DropdownMenuItem(
                    text = {
                        Text("Switch User")
                    },
                    onClick = onSwitchUserTapped,
                )
                DropdownMenuItem(
                    text = {
                        Text("Clear Chat")
                    },
                    onClick = onClearChatTapped,
                )
            }
        }
    )
}

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
                enabled = !state.isSending,
                isError = state.sendFailed,
                modifier = Modifier.weight(1f),
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

@Preview
@Composable
private fun MuzzAppPreview() {
    val listItems = UiMessageMapper.buildChatListItems(
        messages = SampleChat.messages,
        currentUserId = "John"
    )
    MuzzChatTheme {
        MuzzApp(
            viewState = ChatViewState(
                currentUser = User.John,
                listItems = listItems,
            ),
            composerState = MessageComposerState(),
            onSwitchUserTapped = {},
            onClearChatTapped = {},
            onMessageChanged = {},
            onSendMessage = {},
        )
    }
}
