package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {

    fun getAll():List<Post>
    fun getAllAsync(callback: GetPostsCallback)
    interface GetPostsCallback {
        fun onSuccess(posts: List<Post>)
        fun onError(e: Exception)
    }
    fun likeById(id: Long, willLike: Boolean): Post
    fun likeByIdAsync(id: Long, willLike: Boolean, callback: LikeCallback)
    interface LikeCallback {
        fun onSuccess(post: Post) {}
        fun onError(e: Exception) {}
    }
//    fun unLikeById(id: Long): Post
    fun shareById(id: Long)
    fun removeById(id: Long)
    fun removeByIdAsync(id: Long, callback: RemoveCallback)
    interface RemoveCallback {
        fun onSuccess() {}
        fun onError(e: Exception) {}
    }
    fun save(post: Post) : Post
    fun saveAsync(post: Post, callback: SaveCallback)
    interface SaveCallback {
        fun onSuccess(post: Post) {}
        fun onError(e: Exception) {}
    }


}