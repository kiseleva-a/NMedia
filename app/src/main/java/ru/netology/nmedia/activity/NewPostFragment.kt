package ru.netology.nmedia.activity

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.di.DependencyContainer
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel
import ru.netology.nmedia.viewmodel.ViewModelFactory

class NewPostFragment : Fragment() {
    private val dependencyContainer = DependencyContainer.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModel by viewModels<PostViewModel>(
            ownerProducer = ::requireParentFragment,
            factoryProducer = {
                ViewModelFactory(
                    dependencyContainer.repository,
                    dependencyContainer.appAuth,
                    dependencyContainer.postApiService
                )
            })
        val binding = FragmentNewPostBinding.inflate(layoutInflater, container, false)
        binding.edit.requestFocus()

//        binding.edit.setText(arguments?.textArg.orEmpty())

        arguments?.textArg
            ?.let(binding.edit::setText)

        val contract =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                val resultCode = result.resultCode
                val data = result.data

                when (resultCode) {
                    ImagePicker.RESULT_ERROR -> {
                        Snackbar.make(
                            binding.root,
                            ImagePicker.getError(data),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }

                    Activity.RESULT_OK -> {
                        //Image Uri will not be null for RESULT_OK
                        val fileUri = data?.data

                        viewModel.changePhoto(fileUri, fileUri?.toFile())
                    }
                }
            }

//        binding.ok.setOnClickListener {
//            if (!binding.edit.text.isEmpty()) {
//                val content = binding.edit.text.toString()
//                viewModel.changeContent(content)
//                viewModel.save()
//            }
//            findNavController().navigateUp()
//        }

        viewModel.photo.observe(viewLifecycleOwner) {
            binding.photo.setImageURI(it.uri)
            binding.photoContainer.isVisible = it.uri != null
        }

        viewModel.postCreatedError.observe(viewLifecycleOwner) {
            Toast.makeText(
                activity,
                getString(R.string.specific_posting_error, it),
                Toast.LENGTH_LONG
            )
                .show()
        }

        binding.pickPhoto.setOnClickListener {
            ImagePicker.with(this)
                .galleryOnly()
                .crop()
                .compress(2048)
                .createIntent(contract::launch)
        }

        binding.takePhoto.setOnClickListener {
            ImagePicker.with(this)
                .cameraOnly()
                .crop()
                .compress(2048)
                .createIntent(contract::launch)
        }

        binding.removePhoto.setOnClickListener {
            viewModel.deletePhoto()
        }

        activity?.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_new_post, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.logOut -> {
                        binding.logOutTab.isVisible = true
                        true
                    }

                    R.id.save -> {
                        viewModel.draft = ""
                        val text = binding.edit.text.toString()
                        if (text.isNotBlank()) {
                            viewModel.changeContent(text)
                            viewModel.save()
                            findNavController().navigateUp()
                        }
                        AndroidUtils.hideKeyboard(requireView())
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner)
        binding.goBackButton.setOnClickListener {
            binding.logOutTab.isVisible = false
        }

        binding.logOutButton.setOnClickListener {
            dependencyContainer.appAuth.removeAuth()
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