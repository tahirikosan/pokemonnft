package com.tahirikosan.pokemonnft.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.tahirikosan.pokemonnft.base.BaseFragment
import com.tahirikosan.pokemonnft.data.remote.Resource
import com.tahirikosan.pokemonnft.databinding.FragmentAuthBinding
import com.tahirikosan.pokemonnft.utils.Utils.closeKeyboard
import com.tahirikosan.pokemonnft.utils.Utils.handleApiError
import com.tahirikosan.pokemonnft.utils.Utils.showToastShort
import com.tahirikosan.pokemonnft.utils.Utils.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AuthFragment : BaseFragment<FragmentAuthBinding>(FragmentAuthBinding::inflate) {

    private val viewModel by viewModels<AuthViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleClicks()
        getUser()
        observe()
    }

    private fun handleClicks() {
        with(binding) {
            btnLogin.setOnClickListener {
                val email = etEmail.text.toString()
                val password = etPassword.text.toString()
                if (validateInputs(email, password)) {
                    viewLoading.visible(true)
                    viewModel.signInUser(email, password)
                    closeKeyboard(requireActivity())
                }
            }

            tvRegister.setOnClickListener {
                findNavController().navigate(AuthFragmentDirections.actionAuthFragmentToRegisterFragment())
            }
            tvResetPassword.setOnClickListener {
                findNavController().navigate(AuthFragmentDirections.actionAuthFragmentToResetPasswordFragment())
            }
        }
    }

    private fun getUser() {
        viewModel.getCurrentUser()
    }

    private fun observe() {
        viewModel.currentUser.observe(viewLifecycleOwner, {
            binding.viewLoading.visible(it is Resource.Loading)
            when (it) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    it.value?.let {
                        findNavController().navigate(AuthFragmentDirections.actionAuthFragmentToGameMenuFragment())
                    }
                }
                is Resource.Failure -> {
                }
            }
        })
        viewModel.signinResponse.observe(viewLifecycleOwner, {
            binding.viewLoading.visible(it is Resource.Loading)
            when (it) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    it.value?.let {
                        findNavController().navigate(AuthFragmentDirections.actionAuthFragmentToGameMenuFragment())
                    }
                }
                is Resource.Failure -> handleApiError(it)
            }
        })
    }

    private fun validateInputs(email: String, password: String): Boolean {
        if (email.isEmpty() || password.isEmpty()) {
            showToastShort(requireContext(), "Fields can not be empty!")
            return false
        }
        return true
    }
}