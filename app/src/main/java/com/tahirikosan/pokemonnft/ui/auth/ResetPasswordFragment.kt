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
import com.tahirikosan.pokemonnft.databinding.FragmentResetPasswordBinding
import com.tahirikosan.pokemonnft.utils.Utils.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ResetPasswordFragment :
    BaseFragment<FragmentResetPasswordBinding>(FragmentResetPasswordBinding::inflate) {

    private val viewModel by viewModels<AuthViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpWidgets()
        observe()
    }

    private fun observe() {

        viewModel.passwordResetSuccess.observe(viewLifecycleOwner, {
            binding.viewLoading.visible(it is Resource.Loading)
            when (it) {
                is Resource.Loading -> {

                }
                is Resource.Success -> {
                    if (it.value) {
                        findNavController().navigate(ResetPasswordFragmentDirections.actionResetPasswordFragmentToAuthFragment())
                    }
                }
                is Resource.Failure -> {

                }
            }
        })
    }

    private fun setUpWidgets() {
        with(binding) {
            btnReset.setOnClickListener {
                viewLoading.visible(true)
                val email = etEmail.text.toString()
                viewModel.verifySendPasswordReset(email)
            }
        }
    }
}