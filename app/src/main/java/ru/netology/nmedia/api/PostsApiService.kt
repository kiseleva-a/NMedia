package ru.netology.nmedia.api


import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import ru.netology.nmedia.auth.AuthPair
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.PushToken

interface PostsApiService {

    @GET("posts")
    suspend fun getAll(): Response<List<Post>>

    @GET("posts/{id}/newer")
    suspend fun getNewer(@Path("id") id: Long): Response<List<Post>>

    @GET("posts/{id}")
    suspend fun getById(@Path("id") id: Long): Response<Post>

    @POST("posts")
    suspend fun save(@Body post: Post): Response<Post>

    @POST("posts/{id}/likes ")
    suspend fun likeById(@Path("id") id: Long): Response<Post>

    @DELETE("posts/{id}/likes ")
    suspend fun unlikeById(@Path("id") id: Long): Response<Post>

    @DELETE("posts/{id}")
    suspend fun removeById(@Path("id") id: Long): Response<Unit>

    @Multipart
    @POST("media")
    suspend fun upload(@Part file: MultipartBody.Part): Response<Media>

    @FormUrlEncoded
    @POST("users/authentication")
    suspend fun updateUser(
        @Field("login") login: String,
        @Field("pass") pass: String
    ): Response<AuthPair>

    @FormUrlEncoded
    @POST("users/registration")
    suspend fun registerUser(
        @Field("login") login: String,
        @Field("pass") pass: String,
        @Field("name") name: String
    ): Response<AuthPair>

    @POST("users/push-tokens")
    suspend fun sendPushToken(@Body token: PushToken): Response<Unit>

}
//object PostsApi {
//
//    private const val BASE_URL = "${BuildConfig.BASE_URL}/api/slow/"
//
//    private val logging = HttpLoggingInterceptor().apply {
//        if(BuildConfig.DEBUG){
//            level = HttpLoggingInterceptor.Level.HEADERS
//        }
//    }
//
//    private val client = OkHttpClient.Builder()
//        .addInterceptor(logging)
//        .addInterceptor { chain ->
//            val request = AppAuth.getInstance().state.value?.token?.let {
//                chain.request()
//                    .newBuilder()
//                    .addHeader("Authorization", it)
//                    .build()
//            } ?: chain.request()
//
//            chain.proceed(request)
//        }
//        .build()
//
//    val retrofit = Retrofit.Builder()
//        .addConverterFactory(GsonConverterFactory.create())
//        .client(client)
//        .baseUrl(BASE_URL)
//        .build()
//
//    val retrofitService by lazy{
//        retrofit.create<PostsApiService>()
//    }
//}