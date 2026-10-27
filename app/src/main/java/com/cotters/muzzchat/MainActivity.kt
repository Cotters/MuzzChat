package com.cotters.muzzchat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cotters.muzzchat.data.ChatRepository
import com.cotters.muzzchat.data.UserRepository
import com.cotters.muzzchat.database.DatabaseModule
import com.cotters.muzzchat.ui.theme.MuzzChatTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = DatabaseModule.getDatabase(this)
        val userRepository = UserRepository()
        val chatRepository = ChatRepository(database = database)
        val viewModel = ChatViewModel(userRepository = userRepository, chatRepository = chatRepository)
        setContent {
            val viewState: ChatViewState by viewModel.viewState.collectAsStateWithLifecycle()
            MuzzChatTheme {
                MuzzApp(
                    viewState = viewState,
                    onSwitchUserTapped = viewModel::switchUser,
                    onClearChatTapped = viewModel::onClearChatTapped,
                    onSendMessage = viewModel::sendMessage,
                )
            }
        }
    }
}
