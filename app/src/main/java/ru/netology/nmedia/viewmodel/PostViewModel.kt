package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.utils.SingleLiveEvent


private val empty = Post(
    id = 0,
    content = "",
    author = "",
    authorAvatar = "",
    published = "",
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryImpl(AppDb.getInstance(application).postDao())
    val edited = MutableLiveData(empty)
    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = repository.data.map {
            FeedModel(it, it.isEmpty())
        }
    private val _dataState = MutableLiveData<FeedModelState>(FeedModelState.Idle)
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated
    private val _postCreatedError = SingleLiveEvent<String>()
    val postCreatedError: LiveData<String>
        get() = _postCreatedError

    private val _postsRemoveError = SingleLiveEvent<Pair<String, Long>>()
    val postsRemoveError: LiveData<Pair<String, Long>>
        get() = _postsRemoveError
    private val _postsLikeError = SingleLiveEvent<Pair<String, Pair<Long, Boolean>>>()
    val postsLikeError: LiveData<Pair<String, Pair<Long, Boolean>>>
        get() = _postsLikeError



    var draft = ""

    init {
        load()
    }

    fun load(isRefreshing: Boolean = false) = viewModelScope.launch {
        _dataState.value = if (isRefreshing) FeedModelState.Refreshing else FeedModelState.Loading
        try {
            repository.getAll()
            _dataState.value = FeedModelState.Idle
        } catch (e: Exception) {
            _dataState.value = FeedModelState.Error
        }
    }

    fun empty() {
        edited.value = empty
    }

    fun save() = viewModelScope.launch {
        edited.value?.let {
            repository.save(it)
            _postCreated.postValue(Unit)
            //_postCreated.value = Unit
        }
        empty()
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun changeContent(content: String) {
        edited.value?.let {
            val text = content.trim()
            if (it.content == text) {
                return
            }
            edited.value = it.copy(content = text)
        }
    }

    fun likeById(id: Long, likedByMe: Boolean) = viewModelScope.launch {
        try {
            repository.likeById(id, !likedByMe)
        } catch (e: Exception) {
            _postsLikeError.postValue(e.toString() to (id to likedByMe))
        }
    }

    fun shareById(id: Long) = viewModelScope.launch { repository.shareById(id) }

    fun removeById(id: Long) = viewModelScope.launch {
        try {
            repository.removeById(id)
        } catch (e: Exception) {
            _postsRemoveError.postValue(e.message.toString() to id)
        }

    }
}
