package ru.netology.nmedia.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewPostFragment.Companion.textArg
import ru.netology.nmedia.activity.OnePostFragment.Companion.idArg
import ru.netology.nmedia.activity.PictureFragment.Companion.urlArg
import ru.netology.nmedia.apapter.OnInteractionListener
import ru.netology.nmedia.apapter.PostsAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.viewmodel.PostViewModel


class FeedFragment : Fragment() {

    lateinit var binding: FragmentFeedBinding
    val viewModel by viewModels<PostViewModel>(ownerProducer = ::requireParentFragment)

    private val interactionListener = object : OnInteractionListener {

        override fun onEdit(post: Post) {
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment,
                Bundle().apply
                { textArg = post.content })
            viewModel.edit(post)
        }

        override fun onLike(post: Post) {
            viewModel.likeById(post.id, post.likedByMe)
        }

        override fun onShare(post: Post) {
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, post.content)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(intent, getString(R.string.chooser_share_post))
            startActivity(shareIntent)

            viewModel.shareById(post.id)
        }

        override fun onRemove(post: Post) {
            viewModel.removeById(post.id)
        }

        override fun clickOnPicture(url: String) {
            findNavController().navigate(R.id.action_feedFragment_to_pictureFragment,
                Bundle().apply
                { urlArg = url })
        }

        override fun clickOnVideo(post: Post) {
//            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post.videoUrl))
//            startActivity(intent)
        }

        override fun clickOnPost(post: Post) {
            findNavController().navigate(R.id.action_feedFragment_to_onePostFragment,
                Bundle().apply
                { idArg = post.id })
        }
    }

    val adapter = PostsAdapter(interactionListener)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFeedBinding.inflate(inflater, container, false)

        binding.list.adapter = adapter
        subscribe()

        return binding.root
    }


    private fun subscribe() {
        viewModel.data.observe(viewLifecycleOwner) { state ->
            adapter.submitList(state.posts)
            binding.empty.isVisible = state.empty
        }


        viewModel.newerCount.observe(viewLifecycleOwner){
            if(it>0){
                binding.newerPostsButton.isVisible = true
                binding.newerPostsButton.text = getString(R.string.newer_posts,it.toString())
            }
            else{
                binding.newerPostsButton.isVisible = false
            }
            println("Newer count $it")
        }

        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            //binding.errorGroup.isVisible = state.error
            binding.add.isVisible = state is FeedModelState.Idle
            binding.loading.isVisible = state is FeedModelState.Loading
            if (state is FeedModelState.Error) {
                Snackbar.make(binding.root, getString(R.string.specific_load_error, viewModel.data.value?.errorText), Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry) {
                        viewModel.load()
                    }
                    .show()
            }
        }

//        viewModel.postsEditError.observe(viewLifecycleOwner) {
//            Toast.makeText(
//                activity,
//                getString(R.string.specific_edit_error, it),
//                Toast.LENGTH_LONG
//            )
//                .show()
//        }

        viewModel.postsRemoveError.observe(viewLifecycleOwner) {
            val id = it.second
            Snackbar.make(
                binding.root,
                getString(R.string.specific_edit_error, it.first),
                Snackbar.LENGTH_LONG
            )
                .setAction("Retry"){
                    viewModel.removeById(id)
                }
                .show()
        }

        viewModel.postsLikeError.observe(viewLifecycleOwner) {
            val id = it.second.first
            val willLike = it.second.second
            Snackbar.make(
                binding.root,
                getString(R.string.specific_edit_error, it.first),
                Snackbar.LENGTH_LONG
            )
                .setAction("Retry"){
                    viewModel.likeById(id,willLike)
                }
                .show()
        }

        binding.retry.setOnClickListener {
            viewModel.load()
        }

        binding.newerPostsButton.setOnClickListener{
            binding.newerPostsButton.isVisible = false
            viewModel.showNewPosts()
        }

        binding.add.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
        }
    }
}