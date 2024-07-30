package ru.netology.nmedia.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import ru.netology.nmedia.auth.AppAuth

class AuthViewModel(appAuth: AppAuth) : ViewModel() {
    val state = appAuth.state
        .asLiveData()
    val authorized: Boolean
        get() = state.value != null
}