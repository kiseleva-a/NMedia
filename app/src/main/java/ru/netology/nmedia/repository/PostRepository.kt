package ru.netology.nmedia.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.Post
import java.io.File

interface PostRepository {

    val data: Flow<PagingData<Post>>

    fun getNewerCount(id: Long): Flow<Int>

    suspend fun showNewPosts()
    suspend fun getAll()
    suspend fun save(post: Post)
    suspend fun removeById(id: Long)
    suspend fun shareById(id: Long)
    suspend fun likeById(id: Long, willLike: Boolean): Post

    suspend fun saveWithAttachment(post: Post, file: File)

//    fun getAll(callback: GetPostsCallback)
//    interface GetPostsCallback {
//        fun onSuccess(posts: List<Post>) {}
//        fun onError(e: String) {}
//    }
//
//    fun save(post: Post, callback: SaveCallback)
//    interface SaveCallback {
//        fun onSuccess(post: Post) {}
//        fun onError(e: String) {}
//    }
//
//    fun likeById(id: Long, willLike: Boolean, callback: LikeCallback)
//    interface LikeCallback {
//        fun onSuccess(post: Post) {}
//        fun onError(e: String) {}
//    }
//
//    fun shareById(id: Long)
//
//    fun removeById(id: Long, callback: RemoveCallback)
//    interface RemoveCallback {
//        fun onSuccess() {}
//        fun onError(e: String) {}
//    }

}