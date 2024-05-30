package ru.netology.nmedia.repository

import androidx.lifecycle.map
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.dao.PostDao

class PostRepositoryImpl(
    private val dao: PostDao
) : PostRepository {
    override fun getAll() = dao.getAll().map { list ->
        list.map {
            Post(it.id,it.author,it.content,it.published,it.likes, it.likedByMe, it.shared,it.viewed,it.videoViews, it.videoUrl)
        }
    }

    override fun save(post: Post) {
        dao.save(PostEntity.fromDto(post))
    }

    override fun likeById(id: Long) {
        dao.likeById(id)
    }

    override fun shareById(id: Long) {
        dao.shareById(id)
    }

    override fun removeById(id: Long) {
        dao.removeById(id)
    }
}