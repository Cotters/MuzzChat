package com.cotters.muzzchat.domain

enum class User {
    John, Sarah;

    fun switched(): User {
        return when (this) {
            John -> Sarah
            Sarah -> John
        }
    }
}