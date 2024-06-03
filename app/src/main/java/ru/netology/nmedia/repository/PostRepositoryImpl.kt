package ru.netology.nmedia.repository


import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.api.PostsApi
import java.io.IOException
import java.util.concurrent.TimeUnit


class PostRepositoryImpl() : PostRepository {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()
    private val typeToken = object : TypeToken<List<Post>>() {}

    companion object {
        private val jsonType = "application/json".toMediaType()
    }

    override fun getAll(callback: PostRepository.GetPostsCallback) {
        PostsApi.retrofitService.getAll()
            .enqueue(object : Callback<List<Post>> {
                override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                    try {
                        if (!response.isSuccessful) {
                            callback.onError(response.code().toString())
                            return
                        }
                        callback.onSuccess(
                            response.body() ?: throw RuntimeException("body is null")
                        )
                    } catch (e: Exception) {
                        callback.onError(e.toString())
                    }
                }

                override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                    callback.onError(java.lang.Exception(t).toString())
                }
            })
    }

    override fun save(post: Post, callback: PostRepository.SaveCallback) {
        PostsApi.retrofitService.save(post)
            .enqueue(object : Callback<Post> {
                override fun onResponse(call: Call<Post>, response: Response<Post>) {
                    try {
                        if (!response.isSuccessful) {
                            callback.onError(response.code().toString())
                            return
                        }
                        callback.onSuccess(
                            response.body() ?: throw RuntimeException("body is null")
                        )
                    } catch (e: Exception) {
                        callback.onError(e.toString())
                    }
                }

                override fun onFailure(call: Call<Post>, t: Throwable) {
                    callback.onError(java.lang.Exception(t).toString())
                }
            })

    }

    override fun likeById(id: Long, willLike: Boolean, callback: PostRepository.LikeCallback) {
        val request = if(willLike)
            PostsApi.retrofitService.likeById(id)
        else
            PostsApi.retrofitService.unlikeById(id)

        request.enqueue(object : Callback<Post>{
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                try{
                    if(!response.isSuccessful){
                        callback.onError(response.code().toString())
                        return
                    }
                    callback.onSuccess(
                        response.body()?: throw RuntimeException ("body is null")
                    )
                } catch (e: Exception){
                    callback.onError(e.toString())
                }
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                callback.onError(java.lang.Exception(t).toString())
            }
        })
    }

    override fun shareById(id: Long) {
        //TODO
    }

    override fun removeById(id: Long, callback: PostRepository.RemoveCallback) {
        PostsApi.retrofitService.removeById(id)
            .enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if (!response.isSuccessful) {
                        callback.onError(response.code().toString())
                        return
                    }
                    callback.onSuccess()
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    callback.onError(java.lang.Exception(t).toString())
                }

            })
    }
}
