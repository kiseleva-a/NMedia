package ru.netology.nmedia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val post = Post(
            id = 1,
            author = "Нетология. Университет интернет-профессий будущего",
            published = "21 мая в 18:36",
            content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
            likedByMe = true,
            likes = 60,
            shared = 999,
            sharedBy = 10,
            viewed = 1000,
        )



        with(binding) {
            author.text = post.author
            published.text = post.published
            content.text = post.content
            likesCount.text = numbersRoundings(post.likes)
            shareCount.text = numbersRoundings(post.shared)
            viewsCount.text = numbersRoundings(post.viewed)


            if (post.likedByMe) {
                like?.setImageResource(R.drawable.ic_baseline_liked_24)
            }

            like.setOnClickListener {
                post.likedByMe = !post.likedByMe
                post.likes += if (post.likedByMe) +1 else -1
                likesCount.text = numbersRoundings(post.likes)
                like.setImageResource(if (post.likedByMe) R.drawable.ic_baseline_liked_24 else R.drawable.ic_baseline_likes_24)


            }

            share.setOnClickListener {
                post.shared += post.sharedBy
                shareCount.text = numbersRoundings(post.shared)
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
}