package com.tahirikosan.pokemonnft.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.tahirikosan.pokemonnft.base.BaseFragment
import com.tahirikosan.pokemonnft.databinding.FragmentRegisterBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : BaseFragment<FragmentRegisterBinding>(FragmentRegisterBinding::inflate) {

    private val viewModel by viewModels<AuthViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        registerObservers()
        listenToChannels()
        binding?.apply {
            btnRegister.setOnClickListener {
                viewLoading.isVisible = true
                val email = etEmail.text.toString()
                val password = etPassword.text.toString()
                val confirmPass = etPasswordConfirm.text.toString()
                viewModel.signUpUser(email, password, confirmPass)
            }

            btnLogin.setOnClickListener {
                findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToAuthFragment())
            }

        }
    }

    private fun registerObservers() {
        viewModel.currentUser.observe(viewLifecycleOwner, { user ->
            user?.let {
                findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToWalletConnectionFragment())
            }
        })
    }

    private fun listenToChannels() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.allEventsFlow.collect { event ->
                when (event) {
                    is AuthViewModel.AllEvents.Error -> {
                        binding?.apply {
                            /*errorTxt.text = event.error
                            progressBarSignup.isInvisible = true*/
                        }
                    }
                    is AuthViewModel.AllEvents.Message -> {
                        Toast.makeText(requireContext(), event.message, Toast.LENGTH_SHORT).show()
                    }
                    is AuthViewModel.AllEvents.ErrorCode -> {
                        /*  if (event.code == 1)
                              binding?.apply {
                                  userEmailEtvl.error = "email should not be empty"
                                  progressBarSignup.isInvisible = true
                              }


                          if (event.code == 2)
                              binding?.apply {
                                  userPasswordEtvl.error = "password should not be empty"
                                  progressBarSignup.isInvisible = true
                              }

                          if (event.code == 3)
                              binding?.apply {
                                  confirmPasswordEtvl.error = "passwords do not match"
                                  progressBarSignup.isInvisible = true
                              }*/
                    }

                    else -> {
                        Log.d("TAG", "listenToChannels: No event received so far")
                    }
                }

            }
        }
    }
}