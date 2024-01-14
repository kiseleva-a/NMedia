package ru.netology.nmedia.dto

data class Post(
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    val likes: Long = 0,
    val likedByMe: Boolean = false,
    val shared: Long = 0,
    val sharedBy: Long = 0,
    val viewed: Long = 0,
    var videoViews: Long = 0,
    var videoUrl: String? = null,
    )