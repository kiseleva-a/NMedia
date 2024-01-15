package ru.netology.nmedia.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.launch
import androidx.activity.viewModels
import ru.netology.nmedia.R
import ru.netology.nmedia.apapter.OnInteractionListener
import ru.netology.nmedia.apapter.PostsAdapter
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.util.AndroidUtils.focusAndShowKeyboard
import ru.netology.nmedia.viewmodel.PostViewModel

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val viewModel: PostViewModel by viewModels()

        val activityLauncher = registerForActivityResult(NewPostActivity.Contract) { text ->
            text ?: return@registerForActivityResult
            viewModel.changeContent(text.toString())
            viewModel.save()
        }

        val adapter = PostsAdapter(object : OnInteractionListener {
            override fun onLike(post: Post) {
                viewModel.likeById(post.id)
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun onEdit(post: Post) {
                activityLauncher.launch(post.content)
                viewModel.edit(post)
            }

            override fun onShare(post: Post) {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }
                val shareIntent =
                    Intent.createChooser(intent, getString(R.string.chooser_share_post))
                startActivity(shareIntent)
                viewModel.shareById(post.id)
            }

            override fun clickOnVideo(post: Post) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post.videoUrl))
                startActivity(intent)

            }
        })

        binding.list.adapter = adapter

        viewModel.data.observe(this) { posts ->
            val newPost = adapter.currentList.size < posts.size
            adapter.submitList(posts) {// submitList работает асинхронно
                if (newPost) {
                    binding.list.smoothScrollToPosition(0)
                }
            }
        }



        viewModel.edited.observe(this) { post ->
            if (post.id != 0L) {

            }

        }


        binding.add.setOnClickListener {
            activityLauncher.launch(null)
        }


    }


}