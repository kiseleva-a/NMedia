package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentPictureBinding
import ru.netology.nmedia.util.StringArg

class PictureFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPictureBinding.inflate(inflater, container, false)

        val attachmentUrl = "${BuildConfig.BASE_URL}/media/${arguments?.urlArg}"

        binding.photo.load(attachmentUrl)

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigateUp()
        }

        return binding.root
    }

    private fun ImageView.load(url: String, timeout: Int = 10_000) {
        Glide.with(this)
            .load(url)
            .error(R.drawable.ic_baseline_error_outline_48)
            .placeholder(R.drawable.ic_baseline_downloading_48)
            .timeout(timeout)
            .into(this)
    }

    companion object {
        var Bundle.urlArg by StringArg
    }
}