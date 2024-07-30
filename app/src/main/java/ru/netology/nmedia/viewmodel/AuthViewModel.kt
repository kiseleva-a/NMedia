package ru.netology.nmedia.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.netology.nmedia.auth.AppAuth
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(appAuth: AppAuth) : ViewModel() {
    val state = appAuth.state
        .asLiveData()
    val authorized: Boolean
        get() = state.value != null
}