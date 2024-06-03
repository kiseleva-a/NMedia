package ru.netology.nmedia.model

import ru.netology.nmedia.dto.Post

data class FeedModel(
    val posts : List<Post> = emptyList(),
    val loading : Boolean = false,
    val error : Boolean = false,
    val errorText: String = "Something went wrong",
    val empty : Boolean = false,
    val refreshing: Boolean = false,
)

sealed interface FeedModelState {
    object Idle : FeedModelState
    object Error : FeedModelState
    object Refreshing : FeedModelState
    object Loading : FeedModelState
}
