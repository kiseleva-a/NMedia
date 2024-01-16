package ru.netology.nmedia.apapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post

interface OnInteractionListener {
    fun onLike(post: Post) {}
    fun onEdit(post: Post) {}
    fun onRemove(post: Post) {}
    fun onShare(post: Post) {}
    fun clickOnVideo(post: Post)

    fun clickOnPost(post: Post)
}


class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }
}

class PostsAdapter(
    private val onInteractionListener: OnInteractionListener
) : ListAdapter<Post, PostViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(view, onInteractionListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {
        binding.apply {
            author.text = post.author
            published.text = post.published
            content.text = post.content


            likeSign.text = numbersRoundings(post.likes)
            likeSign.isChecked = post.likedByMe
            likeSign.setOnClickListener { onInteractionListener.onLike(post) }

            shareSign.text = numbersRoundings(post.shared)
            shareSign.setOnClickListener { onInteractionListener.onShare(post) }

            viewsSign.text = numbersRoundings(post.viewed)

//            likesCount.text = numbersRoundings(post.likes)
//            shareCount.text = numbersRoundings(post.shared)
//            viewsCount.text = numbersRoundings(post.viewed)
//            like.setImageResource(
//                if (post.likedByMe) ru.netology.nmedia.R.drawable.ic_baseline_liked_24 else ru.netology.nmedia.R.drawable.ic_baseline_likes_24
//            )


            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    setOnMenuItemClickListener { menuItem ->
                        when (menuItem.itemId) {
                            R.id.edit -> {
                                onInteractionListener.onEdit(post)
                                true
                            }

                            R.id.remove -> {
                                onInteractionListener.onRemove(post)
                                true
                            }

                            else -> false
                        }
                    }
                }.show()
            }

            root.setOnClickListener {
                onInteractionListener.clickOnPost(post)
            }

            if (!post.videoUrl.isNullOrBlank()) {
                videoGroup.visibility = View.VISIBLE
                videoButton.setOnClickListener { onInteractionListener.clickOnVideo(post) }
                videoPicture.setOnClickListener { onInteractionListener.clickOnVideo(post) }

            } else {videoGroup.visibility = View.GONE}
        }
    }

    fun numbersRoundings(count: Long): String {
        when (count) {
            in 0..999 -> return count.toString()
            in 1000..1099 -> return "1K"
            in 1100..9_999 -> return (count.toDouble() / 1000).toString().take(3) + "K"
            in 10_000..99_999 -> return (count.toDouble() / 1000).toString().take(2) + "K"
            in 100_000..999_999 -> return (count.toDouble() / 1000).toString().take(3) + "K"
            else -> {
                val milNumber = (count.toDouble() / 1_000_000).toString()
                val strings = milNumber.split(".")
                return strings[0] + "." + strings[1].take(1) + "M"
            }
        }
    }
}
