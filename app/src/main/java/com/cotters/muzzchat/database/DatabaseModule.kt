package com.cotters.muzzchat.database

import android.content.Context
import androidx.room3.Room

object DatabaseModule {
    fun getDatabase(context: Context): ChatDatabase {
        return Room.databaseBuilder<ChatDatabase>(context, "muzz_chat_database")
            .fallbackToDestructiveMigration(true)
            .build()
    }
}