package ru.netology.nmedia.apapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post


typealias OnLikeListener = (post: Post) -> Unit
typealias OnShareListener = (post: Post) -> Unit

class PostsAdapter(
    private val onLikeListener: OnLikeListener,
    private val onShareListener: OnShareListener
) : RecyclerView.Adapter<PostViewHolder>() {
    var list: List<Post> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(view, onLikeListener, onShareListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = list[position]
        holder.bind(post)
    }

    override fun getItemCount(): Int = list.size
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onLikeListener: OnLikeListener,
    private val onShareListener: OnShareListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {
        with(binding) {
            author.text = post.author
            published.text = post.published
            content.text = post.content
            likesCount.text = numbersRoundings(post.likes)
            shareCount.text = numbersRoundings(post.shared)
            viewsCount.text = numbersRoundings(post.viewed)
            like.setImageResource(
                if (post.likedByMe) ru.netology.nmedia.R.drawable.ic_baseline_liked_24 else ru.netology.nmedia.R.drawable.ic_baseline_likes_24
            )
            like.setOnClickListener { onLikeListener(post) }
            share.setOnClickListener { onShareListener(post) }
        }
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
