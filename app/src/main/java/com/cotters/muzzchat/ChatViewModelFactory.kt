package com.cotters.muzzchat

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.cotters.muzzchat.data.ChatRepository
import com.cotters.muzzchat.data.UserRepository
import com.cotters.muzzchat.database.DatabaseModule

fun createChatViewModelFactory(context: Context): ViewModelProvider.Factory {
    val appContext = context.applicationContext
    return viewModelFactory {
        initializer {
            val database = DatabaseModule.getDatabase(appContext)
            ChatViewModel(
                userRepository = UserRepository(),
                chatRepository = ChatRepository(database),
            )
        }
    }
}
