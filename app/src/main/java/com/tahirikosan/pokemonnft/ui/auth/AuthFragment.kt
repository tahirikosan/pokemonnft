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
        registerObserver()
        listenToChannels()
    }

    private fun handleClicks() {
        with(binding) {
            btnLogin.setOnClickListener {
                //  progressBarSignin.isVisible = true
                val email = etEmail.text.toString()
                val password = etPassword.text.toString()
                viewModel.signInUser(email, password)
            }

            btnRegister.setOnClickListener {
                findNavController().navigate(AuthFragmentDirections.actionAuthFragmentToRegisterFragment())
            }
            btnResetPassword.setOnClickListener {
                findNavController().navigate(AuthFragmentDirections.actionAuthFragmentToResetPasswordFragment())
            }
        }
    }

    private fun getUser() {
        viewModel.getCurrentUser()
    }

    private fun listenToChannels() {

    }

    private fun registerObserver() {
        viewModel.currentUser.observe(viewLifecycleOwner, { it ->
            binding.viewLoading.visible(it is Resource.Loading)
            when (it) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    routeToWalletConnectionPage()
                }
                is Resource.Failure -> {

                }
            }
        })
    }

    private fun routeToWalletConnectionPage() {
        if (userPreferences.isPrivateKeyExist())
            findNavController().navigate(AuthFragmentDirections.actionAuthFragmentToGameMenuFragment())
        else
            findNavController().navigate(AuthFragmentDirections.actionAuthFragmentToWalletConnectionFragment())
    }
}