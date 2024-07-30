//package ru.netology.nmedia.viewmodel
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import ru.netology.nmedia.api.PostsApiService
//import ru.netology.nmedia.auth.AppAuth
//import ru.netology.nmedia.repository.PostRepository
//import ru.netology.nmedia.viewmodel.AuthViewModel
//import ru.netology.nmedia.viewmodel.PostViewModel
//import ru.netology.nmedia.viewmodel.SignInViewModel
//import ru.netology.nmedia.viewmodel.SignUpViewModel
//
//class ViewModelFactory(
//    private val repository: PostRepository,
//    private val appAuth: AppAuth,
//    private val postApiService: PostsApiService,
//) : ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T =
//        when {
//            modelClass.isAssignableFrom(PostViewModel::class.java) -> {
//                PostViewModel(repository, appAuth) as T
//            }
//            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
//                AuthViewModel(appAuth) as T
//            }
//            modelClass.isAssignableFrom(SignUpViewModel::class.java) -> {
//                SignInViewModel(postApiService) as T
//            }
//            modelClass.isAssignableFrom(SignUpViewModel::class.java) -> {
//                SignUpViewModel(postApiService) as T
//            }
//            else -> error("Unknown class: $modelClass")
//        }
//}