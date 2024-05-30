package ru.netology.nmedia.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContract
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel

class NewPostFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModel by viewModels <PostViewModel> (ownerProducer = ::requireParentFragment)
        val binding = FragmentNewPostBinding.inflate(layoutInflater, container, false)
        binding.edit.requestFocus()

//        binding.edit.setText(arguments?.textArg.orEmpty())

        arguments?.textArg
            ?.let(binding.edit::setText)

        binding.ok.setOnClickListener {
            if (!binding.edit.text.isEmpty()) {
                val content = binding.edit.text.toString()
                viewModel.changeContent(content)
                viewModel.save()
            }
            findNavController().navigateUp()
        }
        return binding.root
    }

    companion object {
//        private  const val TEXT_ARG = "TEXT_ARG"
        var Bundle.textArg: String? by StringArg
//            get() = getString(TEXT_ARG)
//            set (value) {
//                putString(TEXT_ARG, value)
//            }
    }

}