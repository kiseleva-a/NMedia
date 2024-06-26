package ru.netology.nmedia.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Post

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val authorAvatar: String,
    val content: String,
    val published: String,
    val likes: Long = 0,
    val likedByMe: Boolean = false,
    val shared: Long = 0,
    val viewed: Long = 0,
    val videoViews: Long = 0,
    val videoUrl: String = "",
    val show: Boolean = true,
){
    fun toDto() = Post(id, author, authorAvatar, content, published, likes, likedByMe, shared, viewed, videoViews, videoUrl)

    companion object{
        fun fromDto(dto: Post) =
            PostEntity(dto.id,dto.author, dto.authorAvatar, dto.content,dto.published,dto.likes,dto.likedByMe,dto.shared, dto.viewed, dto.videoViews,dto.videoUrl)
    }
}

fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(show: Boolean = true): List<PostEntity> = map(PostEntity::fromDto)
    .map { it.copy(show = show) }