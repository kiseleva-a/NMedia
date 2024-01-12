package ru.netology.nmedia.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
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
        val adapter = PostsAdapter(object : OnInteractionListener {
            override fun onLike(post: Post) { viewModel.likeById(post.id) }

            override fun onShare(post: Post) { viewModel.shareById(post.id) }

            override fun onRemove(post: Post) { viewModel.removeById(post.id) }

            override fun onEdit(post: Post) { viewModel.edit(post) }
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

        viewModel.edited.observe(this) {post ->
            if (post.id != 0L) {
                binding.content.setText(post.content)
                binding.content.focusAndShowKeyboard()
            }
            binding.oldText.text = post.content
            binding.editCancelGroup.visibility = View.VISIBLE
        }

        binding.save.setOnClickListener {
            with(binding.content) {
                val text = binding.content.text.toString()
                if (text.isBlank()) {
                    Toast.makeText(
                        this@MainActivity,
                        R.string.error_empty_content,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    return@setOnClickListener
                }

                viewModel.changeContent(text)
                viewModel.save()
                setText("")
                clearFocus()
                AndroidUtils.hideKeyboard(it)
                binding.editCancelGroup.visibility = View.GONE
            }
        }

        binding.cancelEdit.setOnClickListener {
            with(binding.content) {
                viewModel.cancelEdit()
                setText("")
                clearFocus()
                AndroidUtils.hideKeyboard(this)
                binding.editCancelGroup.visibility = View.GONE
            }
            binding.oldText.text = ""
        }



    }



}