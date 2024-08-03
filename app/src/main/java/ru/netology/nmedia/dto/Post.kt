package ru.netology.nmedia.dto

import android.net.Uri
import java.io.File

sealed interface FeedItem {
    val id: Long
}

data class Post(
    override val id: Long,
    val author: String,
    val authorAvatar: String,
    val content: String,
    val published: String,
    val likes: Long = 0,
    val likedByMe: Boolean = false,
    val shared: Long = 0,
    val viewed: Long = 0,
    val attachment: Attachment? = null,
    val ownedByMe: Boolean = false,
    val authorId: Long = 0,
) : FeedItem

data class Ad(
    override val id: Long,
    val image: String,
) : FeedItem

data class Attachment(
    val url: String,
    val description: String,
    val type: AttachmentType = AttachmentType.IMAGE
)

enum class AttachmentType {
    IMAGE
}

data class PhotoModel(val uri: Uri?, val file: File?)
data class Media(val id: String)