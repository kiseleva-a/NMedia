package ru.netology.nmedia

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.auth.AuthPair
import ru.netology.nmedia.utils.SingleLiveEvent

class SignInViewModel(application: Application) : AndroidViewModel(application) {
    private val _signInError = SingleLiveEvent<String>()
    val signInError: LiveData<String>
        get() = _signInError
    private val _signInWrong = SingleLiveEvent<Unit>()
    val signInWrong: LiveData<Unit>
        get() = _signInWrong
    private val _signInRight = SingleLiveEvent<AuthPair>()
    val signInRight: LiveData<AuthPair>
        get() = _signInRight


    fun signIn(login: String, password: String)  = viewModelScope.launch {
        try {
            val response = PostsApi.retrofitService.updateUser(login, password)
            if (!response.isSuccessful) {
                if (response.code()==400||response.code() == 404){
                    _signInWrong.postValue(Unit)
                }
                _signInError.postValue(response.code().toString())
            }
            val authPair = response.body() ?: throw RuntimeException("body is null")
            _signInRight.postValue(authPair)
        } catch (e: Exception) {
            _signInError.postValue(e.toString())
        }
    }
}