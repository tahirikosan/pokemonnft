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
import com.tahirikosan.pokemonnft.data.remote.Resource
import com.tahirikosan.pokemonnft.databinding.FragmentRegisterBinding
import com.tahirikosan.pokemonnft.utils.Utils.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : BaseFragment<FragmentRegisterBinding>(FragmentRegisterBinding::inflate) {

    private val viewModel by viewModels<AuthViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerObservers()
        with(binding) {
            btnRegister.setOnClickListener {
                viewLoading.isVisible = true
                val email = etEmail.text.toString()
                val password = etPassword.text.toString()
                val confirmPass = etPasswordConfirm.text.toString()
                viewModel.signUpUser(email, password)
            }

            btnLogin.setOnClickListener {
                findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToAuthFragment())
            }

        }
    }

    private fun registerObservers() {
        viewModel.currentUser.observe(viewLifecycleOwner, { it ->
            binding.viewLoading.visible(it is Resource.Loading)
            when (it) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    viewModel.addUserToFirestore(userId = it.value.uid)
                }
                is Resource.Failure -> {

                }
            }
        })

        viewModel.isUserAddedFirestore.observe(viewLifecycleOwner, { it ->
            binding.viewLoading.visible(it is Resource.Loading)
            when (it) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    // If user successfully added to firestore route to wallet page.
                    if (it.value) {
                        findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToWalletConnectionFragment())
                    }
                }
                is Resource.Failure -> {

                }
            }
        })
    }
}