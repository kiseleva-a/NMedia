package ru.netology.nmedia.api


import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.dto.Post

interface PostsApiService{

    @GET("posts")
    suspend fun getAll(): Response<List<Post>>

    @GET("posts/{id}")
    suspend fun getById(@Path("id") id: Long): Response<Post>

    @POST("posts")
    suspend fun save (@Body post: Post): Response<Post>

    @POST("posts/{id}/likes ")
    suspend fun likeById(@Path("id")id: Long): Response<Post>

    @DELETE("posts/{id}/likes ")
    suspend fun unlikeById(@Path("id")id: Long): Response<Post>

    @DELETE("posts/{id}")
    suspend fun removeById(@Path("id")id: Long): Response<Unit>
}

object PostsApi {

    private const val BASE_URL = "${BuildConfig.BASE_URL}/api/slow/"

    private val logging = HttpLoggingInterceptor().apply {
        if(BuildConfig.DEBUG){
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .baseUrl(BASE_URL)
        .build()

    val retrofitService by lazy{
        retrofit.create<PostsApiService>()
    }
}