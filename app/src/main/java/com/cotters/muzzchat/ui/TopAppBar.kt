package com.cotters.muzzchat.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import com.cotters.muzzchat.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
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
        },
    )
}
