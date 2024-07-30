package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.auth.AuthPair
import ru.netology.nmedia.utils.SingleLiveEvent

class SignUpViewModel(application: Application) : AndroidViewModel(application) {
    private val _signUpError = SingleLiveEvent<String>()
    val signUpError: LiveData<String>
        get() = _signUpError
    private val _signUpRight = SingleLiveEvent<AuthPair>()
    val signUpRight: LiveData<AuthPair>
        get() = _signUpRight


    fun signUp(login: String, password: String, username: String)  = viewModelScope.launch {
        try {
            val response = PostsApi.retrofitService.registerUser(login, password,username)
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