package ru.netology.nmedia.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.AttachmentType
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
    @Embedded
    val attachment: AttachmentEmbedabble?,
    val authorId: Long = 0,
    val notOnServer: Boolean = false,
    val show: Boolean = true,
){
    fun toDto() = Post(id, author, authorAvatar, content, published, likes, likedByMe, shared, viewed, attachment?.toDto(), authorId = authorId)

    companion object{
        fun fromDto(dto: Post) =
            PostEntity(dto.id,dto.author, dto.authorAvatar, dto.content,dto.published,dto.likes,dto.likedByMe,dto.shared, dto.viewed, AttachmentEmbedabble.fromDto(dto.attachment), authorId = dto.authorId)
    }
}

data class AttachmentEmbedabble(
    val url: String,
    val description: String,
    val type: AttachmentType
) {
    fun toDto() = Attachment(url, description, type)

    companion object {
        fun fromDto(dto: Attachment?) = dto?.let {
            AttachmentEmbedabble(it.url, it.description, it.type)
        }
    }
}

fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(show: Boolean = true): List<PostEntity> = map(PostEntity.Companion::fromDto)
    .map { it.copy(show = show) }