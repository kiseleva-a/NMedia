package ru.netology.nmedia.dto

data class Post(
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    var likes: Long = 0,
    var likedByMe: Boolean = false,
    var shared: Long = 0,
    var sharedBy: Long = 0,
    var viewed: Long = 0
    )