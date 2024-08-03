package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardAdBinding
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Ad
import ru.netology.nmedia.dto.AttachmentType
import ru.netology.nmedia.dto.FeedItem
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.view.load

interface OnInteractionListener {
    fun onLike(post: Post) {}
    fun onEdit(post: Post) {}
    fun onRemove(post: Post) {}
    fun onShare(post: Post) {}
    fun clickOnVideo(post: Post)

    fun clickOnPost(post: Post)

    fun clickOnPicture(url: String) {}
}


class PostDiffCallback : DiffUtil.ItemCallback<FeedItem>() {
    override fun areItemsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
        return oldItem.id == newItem.id
    }
    override fun areContentsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
        return oldItem == newItem
    }
}

//class PostsAdapter(
//    private val onInteractionListener: OnInteractionListener
//) : PagingDataAdapter<Post, PostViewHolder>(PostDiffCallback()) {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
//        val view = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return PostViewHolder(view, onInteractionListener)
//    }
//
//    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
//        val post = getItem(position) ?: return
//        holder.bind(post)
//    }
//}

class PostsAdapter(
    private val onInteractionListener: OnInteractionListener
) :
    PagingDataAdapter<FeedItem, RecyclerView.ViewHolder>(PostDiffCallback()) {
    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is Ad -> R.layout.card_ad
            is Post -> R.layout.card_post
            else -> error("Unknown view type ")
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            R.layout.card_post -> {
                val binding =
                    CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return PostViewHolder(binding, onInteractionListener)
            }
            R.layout.card_ad -> {
                val binding =
                    CardAdBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return AdViewHolder(binding)
            }
            else -> error("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is Ad -> (holder as? AdViewHolder)?.bind(item)
            is Post -> (holder as? PostViewHolder)?.bind(item)
            else -> error("Unknown view type ")
        }
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


            menu.isVisible = post.ownedByMe
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

//            if (!post.videoUrl.isNullOrBlank()) {
//                videoGroup.visibility = View.VISIBLE
//                videoButton.setOnClickListener { onInteractionListener.clickOnVideo(post) }
//                videoPicture.setOnClickListener { onInteractionListener.clickOnVideo(post) }
//
//            } else {videoGroup.visibility = View.GONE}

            if (post.attachment != null) {
                attachmentPicture.visibility = View.VISIBLE
                val attachmentUrl = "${BuildConfig.BASE_URL}/media/${post.attachment.url}"
                binding.attachmentPicture.load(attachmentUrl)
                if (post.attachment.type == AttachmentType.IMAGE) {
                    attachmentPicture.setOnClickListener { onInteractionListener.clickOnPicture(post.attachment.url) }
                    videoButton.visibility = View.GONE
                } else {
                    attachmentPicture.setOnClickListener { onInteractionListener.clickOnVideo(post) }
                }
            } else attachmentPicture.visibility = View.GONE

            val url = "${BuildConfig.BASE_URL}/avatars/${post.authorAvatar}"
            binding.avatar.load(url)
        }
    }

    private fun ImageView.load(url: String, timeout : Int = 10_000){
        Glide.with(this)
            .load(url)
            .circleCrop()
            .error(R.drawable.ic_baseline_error_outline_48)
            .placeholder(R.drawable.ic_baseline_downloading_48)
            .timeout(timeout)
            .into(this)
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

class AdViewHolder(
    private val binding : CardAdBinding
) :RecyclerView.ViewHolder(binding.root) {

    fun bind(ad: Ad){
        binding.image.load("${BuildConfig.BASE_URL}/media/${ad.image}")
    }
}