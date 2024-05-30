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
        thread {
            _data.postValue(FeedModel(loading = true))
            try {
                val posts = repository.getAll()
                FeedModel(posts, empty = posts.isEmpty())
            } catch (e: Exception) {
                FeedModel(error = true)
            }.also(_data::postValue)
        }
    }

    fun empty() {
        edited.value = empty
    }

    fun save() {
        thread {
            edited.value?.let {
                try {
                    repository.save(it)
                    _postCreated.postValue(Unit)
                } catch (e: Exception) {
                    e.printStackTrace()
                    //todo
                }
            }
            edited.postValue(empty)
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
        thread {
            if (likedByMe) repository.unLikeById(id) else repository.likeById(id)
            _postUpdated.postValue(Unit)
        }
    }

    fun shareById(id: Long) = repository.shareById(id)
    fun removeById(id: Long) {
        thread {
            val old = _data.value
            val filtered = old?.posts.orEmpty().filter { it.id != id }
            _data.postValue(old?.copy(posts = filtered, empty = filtered.isEmpty()))
            try {
                repository.removeById(id)
            } catch (e: Exception) {
                _data.postValue(old)
            }
        }
    }
}

