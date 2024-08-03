package ru.netology.nmedia.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.FeedItem
import ru.netology.nmedia.dto.PhotoModel
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.utils.SingleLiveEvent
import java.io.File
import javax.inject.Inject


private val empty = Post(
    id = 0,
    content = "",
    author = "",
    authorAvatar = "",
    published = "",
)

private val noPhoto = PhotoModel(null, null)

@HiltViewModel
class PostViewModel @Inject constructor(private val repository: PostRepository, appAuth: AppAuth) :
    ViewModel() {
    val edited = MutableLiveData(empty)

    //    val data: LiveData<FeedModel> = repository.data
//        .map(::FeedModel)
//        .asLiveData(Dispatchers.Default)
    @OptIn(ExperimentalCoroutinesApi::class)
    val data: Flow<PagingData<FeedItem>> = appAuth.state
        .map { it?.id }
        .flatMapLatest { id ->
            repository.data.cachedIn(viewModelScope)
                .map { posts ->
                    posts.map { post ->
                        if (post is Post) {
                            post.copy(ownedByMe = post.authorId == id)
                        } else {
                            post
                        }
                    }
                }
        }.flowOn(Dispatchers.Default)


//    val newerCount: LiveData<Int> = data.switchMap {
//        val newerId = it.posts.firstOrNull()?.id ?: 0L
//        repository.getNewerCount(newerId)
//            .asLiveData()
//    }

    private val _dataState = MutableLiveData<FeedModelState>(FeedModelState.Idle)
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    private val _photo = MutableLiveData(noPhoto)
    val photo: LiveData<PhotoModel>
        get() = _photo

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated
    private val _postCreatedError = SingleLiveEvent<Pair<String, Post>>()
    val postCreatedError: LiveData<Pair<String, Post>>
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

    fun showNewPosts() = viewModelScope.launch {
        repository.showNewPosts()
    }

    fun load(isRefreshing: Boolean = false) = viewModelScope.launch {
        _dataState.value = if (isRefreshing) FeedModelState.Refreshing else FeedModelState.Loading
        try {
//            repository.getAll()
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
            try {
                when (_photo.value) {
                    noPhoto -> repository.save(it)
                    else -> _photo.value?.file?.let { file ->
                        repository.saveWithAttachment(
                            it,
                            file
                        )
                    }
                }
                _postCreated.postValue(Unit)
                empty()
            } catch (e: Exception) {
                _postCreatedError.postValue(e.message.toString() to it)
            }
        }
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

    fun changePhoto(uri: Uri?, file: File?) {
        _photo.value = PhotoModel(uri, file)
    }

    fun deletePhoto() {
        _photo.value = noPhoto
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


