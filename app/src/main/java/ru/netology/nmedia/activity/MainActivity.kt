package ru.netology.nmedia.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel by viewModels<PostViewModel>()
        viewModel.data.observe(this) { post ->
            with(binding) {
                author.text = post.author
                published.text = post.published
                content.text = post.content
                likesCount.text = numbersRoundings(post.likes)
                shareCount.text = numbersRoundings(post.shared)
                viewsCount.text = numbersRoundings(post.viewed)
                like.setImageResource(if (post.likedByMe) R.drawable.ic_baseline_liked_24 else R.drawable.ic_baseline_likes_24)

            }
        }

        binding.like.setOnClickListener { viewModel.like() }
        binding.share.setOnClickListener { viewModel.share() }
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