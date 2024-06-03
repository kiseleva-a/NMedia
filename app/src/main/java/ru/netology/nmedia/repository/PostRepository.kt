package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {

    fun getAll(callback: GetPostsCallback)
    interface GetPostsCallback {
        fun onSuccess(posts: List<Post>) {}
        fun onError(e: String) {}
    }

    fun save(post: Post, callback: SaveCallback)
    interface SaveCallback {
        fun onSuccess(post: Post) {}
        fun onError(e: String) {}
    }

    fun likeById(id: Long, willLike: Boolean, callback: LikeCallback)
    interface LikeCallback {
        fun onSuccess(post: Post) {}
        fun onError(e: String) {}
    }

    fun shareById(id: Long)

    fun removeById(id: Long, callback: RemoveCallback)
    interface RemoveCallback {
        fun onSuccess() {}
        fun onError(e: String) {}
    }

}