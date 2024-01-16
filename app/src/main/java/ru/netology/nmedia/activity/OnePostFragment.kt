package ru.netology.nmedia.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewPostFragment.Companion.textArg
import ru.netology.nmedia.apapter.OnInteractionListener
import ru.netology.nmedia.apapter.PostViewHolder
import ru.netology.nmedia.apapter.PostsAdapter
import ru.netology.nmedia.databinding.FragmentOnePostBinding
import ru.netology.nmedia.utils.LongArg


class OnePostFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentOnePostBinding.inflate(inflater, container, false)
        val viewModel by viewModels<PostViewModel> (ownerProducer = ::requireParentFragment)
        val viewHolder = PostViewHolder(binding.onePostFragment, object : OnInteractionListener {

            override fun onLike(post: Post) {
                viewModel.likeById(post.id)
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
                findNavController().navigateUp()
            }

            override fun onEdit(post: Post) {
                findNavController().navigate(
                    R.id.action_feedFragment_to_newPostFragment,
                    Bundle().apply { textArg = post.content })
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

            override fun clickOnPost(post: Post) {
                findNavController().navigateUp()
            }
        })

        viewModel.data.observe(viewLifecycleOwner) { posts ->
            val post = posts.find { it.id == arguments?.idArg } ?: run {
                findNavController().navigateUp()
                return@observe
            }
            viewHolder.bind(post)
        }
        return binding.root
    }
    companion object {
        var Bundle.idArg by LongArg
    }
}