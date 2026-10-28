package com.cotters.muzzchat.ui

import android.text.format.DateUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cotters.muzzchat.ui.theme.MuzzChatTheme
import com.cotters.muzzchat.ui.viewmodel.ChatListItem

@Composable
fun ChatListView(
    listItems: List<ChatListItem>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(
            listItems,
            key = { item ->
                when (item) {
                    is ChatListItem.TimeSeparator -> { "time_${item.timestamp}" }
                    is ChatListItem.MessageBlock -> { "message_block_${item.messages.first().id}" }
                }
            },
            contentType = { item ->
                when (item) {
                    is ChatListItem.TimeSeparator -> "time_separator"
                    is ChatListItem.MessageBlock -> "message_block"
                }
            }
        ) { item ->
            when (item) {
                is ChatListItem.TimeSeparator -> {
                    TimeLabelSeparator(item)
                }
                is ChatListItem.MessageBlock -> {
                    MessageBlock(item)
                }
            }
        }
    }
}

@Composable
private fun TimeLabelSeparator(item: ChatListItem.TimeSeparator) {
    // TODO: Format within the mapper: weekday if within a week, otherwise date
    val context = LocalContext.current
    val flags = DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_TIME
    Text(
        text = DateUtils.formatDateTime(context, item.timestamp, flags),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun MessageBlock(item: ChatListItem.MessageBlock) {
    Column(
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        item.messages.forEach { message ->
            ChatListMessageItemView(message)
        }
    }
}

@Composable
private fun ChatListMessageItemView(message: ChatViewMessage) {
    val arrangement = if (message.isOutgoing) Arrangement.End else Arrangement.Start
    val backgroundColor = if (message.isOutgoing) MaterialTheme.colorScheme.primaryContainer else
        MaterialTheme.colorScheme.surfaceContainerHigh
    val textColor = if (message.isOutgoing) MaterialTheme.colorScheme.onPrimaryContainer else
        MaterialTheme.colorScheme.onSurface
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = arrangement
    ) {
        MessageBubble(
            content = message.content,
            backgroundColor = backgroundColor,
            textColor = textColor,
        )
    }
}

@Composable
private fun MessageBubble(
    content: String,
    backgroundColor: Color,
    textColor: Color,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(color = backgroundColor)
            .padding(8.dp)
            .widthIn(max = 300.dp)
    ) {
        Text(content, modifier = Modifier.padding(4.dp), color = textColor)
    }
}

@Preview
@Composable
private fun ChatListViewPreview() {
    MuzzChatTheme {
        ChatListView(
            listItems = emptyList(),
        )
    }
}