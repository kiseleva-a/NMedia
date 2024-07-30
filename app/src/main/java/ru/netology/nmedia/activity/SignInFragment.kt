package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentSignInBinding
import ru.netology.nmedia.di.DependencyContainer
import ru.netology.nmedia.viewmodel.SignInViewModel
import ru.netology.nmedia.viewmodel.ViewModelFactory

class SignInFragment : Fragment() {
    private val dependencyContainer = DependencyContainer.getInstance()
    lateinit var binding: FragmentSignInBinding
    val viewModel by viewModels<SignInViewModel>(
        ownerProducer = ::requireParentFragment,
        factoryProducer = {
            ViewModelFactory(
                dependencyContainer.repository,
                dependencyContainer.appAuth,
                dependencyContainer.postApiService
            )
        })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignInBinding.inflate(inflater, container, false)

        subscribe()

        return binding.root
    }

    private fun subscribe() {
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            goBack()
        }

        binding.signInButton.setOnClickListener {
            if (binding.loginInput.text.isNullOrEmpty() || binding.passwordInput.text.isNullOrEmpty()) {
                Toast.makeText(context, "Both fields need to be filled!", Toast.LENGTH_LONG)
                    .show()
            } else {
                viewModel.signIn(
                    binding.loginInput.text.toString(),
                    binding.passwordInput.text.toString()
                )
            }
        }

        viewModel.signInRight.observe(viewLifecycleOwner){
            dependencyContainer.appAuth.setAuth(it.id, it.token)
            goBack()
        }

        viewModel.signInError.observe(viewLifecycleOwner) {
            Toast.makeText(context, getString(R.string.login_error,it), Toast.LENGTH_LONG)
                .show()
        }

        viewModel.signInWrong.observe(viewLifecycleOwner) {
            Toast.makeText(context, getString(R.string.login_wrong), Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun goBack() {
        findNavController().navigateUp()
    }
}