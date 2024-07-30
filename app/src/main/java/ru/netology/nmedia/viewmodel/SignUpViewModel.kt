package ru.netology.nmedia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.netology.nmedia.api.PostsApiService
import ru.netology.nmedia.auth.AuthPair
import ru.netology.nmedia.utils.SingleLiveEvent

class SignUpViewModel(
    private val postApiService: PostsApiService
) : ViewModel() {
    private val _signUpError = SingleLiveEvent<String>()
    val signUpError: LiveData<String>
        get() = _signUpError
    private val _signUpRight = SingleLiveEvent<AuthPair>()
    val signUpRight: LiveData<AuthPair>
        get() = _signUpRight


    fun signUp(login: String, password: String, username: String)  = viewModelScope.launch {
        try {
            val response = postApiService.registerUser(login, password,username)
            if (!response.isSuccessful) {
                _signUpError.postValue(response.code().toString())
            }
            val authPair = response.body() ?: throw RuntimeException("body is null")
            _signUpRight.postValue(authPair)
        } catch (e: Exception) {
            _signUpError.postValue(e.toString())
        }
    }
}