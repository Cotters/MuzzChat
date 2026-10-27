package com.cotters.muzzchat.data

import com.cotters.muzzchat.domain.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserRepository {
    private val _currentUser: MutableStateFlow<User> = MutableStateFlow(User.John)
    val currentUser: StateFlow<User> = _currentUser.asStateFlow()

    fun switchUser() {
        _currentUser.value = _currentUser.value.switched()
    }
}