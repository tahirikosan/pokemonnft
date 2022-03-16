package com.tahirikosan.pokemonnft.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.tahirikosan.pokemonnft.base.BaseFragment
import com.tahirikosan.pokemonnft.databinding.FragmentAuthBinding
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
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.allEventsFlow.collect { event ->
                when (event) {
                    is AuthViewModel.AllEvents.Message -> {
                        Toast.makeText(requireContext(), event.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.allEventsFlow.collect { event ->
                when (event) {
                    is AuthViewModel.AllEvents.Error -> {
                        binding?.apply {
                            /*errorTxt.text =  event.error
                            progressBarSignin.isInvisible = true*/
                        }
                    }
                    is AuthViewModel.AllEvents.Message -> {
                        Toast.makeText(requireContext(), event.message, Toast.LENGTH_SHORT).show()
                    }
                    is AuthViewModel.AllEvents.ErrorCode -> {
                        if (event.code == 1)
                            binding?.apply {
                                /* userEmailEtvl.error = "email should not be empty"
                                 progressBarSignin.isInvisible = true*/
                            }

                        /*  if(event.code == 2)
                              binding?.apply {
                                  userPasswordEtvl.error = "password should not be empty"
                                  progressBarSignin.isInvisible = true
                              }*/
                    }
                    else -> {
                        Log.d("TAG", "listenToChannels: No event received so far")
                    }
                }

            }
        }
    }

    private fun registerObserver() {
        viewModel.currentUser.observe(viewLifecycleOwner, { user ->
            user?.let {
                binding?.apply {
                    routeToWalletConnectionPage()
                    /* welcomeTxt.text = "welcome ${it.email}"
                     signinButton.text = "sign out"
                     signinButton.setOnClickListener {
                         viewModel.signOut()
                     }*/
                }
            } ?: binding?.apply {
                /* welcomeTxt.isVisible = false
                 signinButton.text = "sign in"
                 signinButton.setOnClickListener {
                     findNavController().navigate(R.id.action_homeFragment_to_signInFragment)
                 }*/
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