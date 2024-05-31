package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.utils.SingleLiveEvent
import kotlin.concurrent.thread

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    published = "",
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryImpl()
    val edited = MutableLiveData(empty)
    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _data
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    private val _postUpdated = SingleLiveEvent<Unit>()
    val postUpdated: LiveData<Unit>
        get() = _postUpdated

    var draft = ""

    init {
        load()
    }

    fun load() {
        _data.postValue(FeedModel(loading = true))
        repository.getAllAsync(object : PostRepository.GetPostsCallback {
            override fun onSuccess(posts: List<Post>) {
                _data.postValue(FeedModel(posts = posts, empty = posts.isEmpty()))
            }

            override fun onError(e: Exception) {
                _data.postValue(FeedModel(error = true))
            }
        })
    }

    fun empty() {
        edited.value = empty
    }

    fun save() {
        edited.value?.let {
            repository.saveAsync(it, object : PostRepository.SaveCallback {
                override fun onSuccess(post: Post) {
                    _postCreated.postValue(Unit)
                }

                override fun onError(e: Exception) {
                    e.printStackTrace()
                }
            })
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

    fun likeById(id: Long, likedByMe: Boolean) {
        repository.likeByIdAsync(id, !likedByMe, object : PostRepository.LikeCallback {
            override fun onSuccess(post: Post) {
                val newPosts =
                    (_data.value?.posts.orEmpty().map { if (it.id == id) post else it })
                _data.postValue(_data.value?.copy(posts = newPosts, empty = newPosts.isEmpty()))
            }

            override fun onError(e: Exception) {
                e.printStackTrace()
            }
        })
    }

    fun shareById(id: Long) = repository.shareById(id)
    fun removeById(id: Long) {
        val old = _data.value
        val filtered = old?.posts.orEmpty().filter { it.id != id }
        _data.postValue(old?.copy(posts = filtered, empty = filtered.isEmpty()))

        repository.removeByIdAsync(id, object : PostRepository.RemoveCallback {
            override fun onSuccess() {
            }

            override fun onError(e: Exception) {
                e.printStackTrace()
                _data.postValue(old)
            }
        })
    }
}

