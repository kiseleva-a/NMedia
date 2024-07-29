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
import ru.netology.nmedia.SignInViewModel
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.FragmentSignInUpBinding

class SignInFragment : Fragment() {
    lateinit var binding: FragmentSignInUpBinding
    val viewModel by viewModels<SignInViewModel>(ownerProducer = ::requireParentFragment)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignInUpBinding.inflate(inflater, container, false)

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
            AppAuth.getInstance().setAuth(it.id, it.token)
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