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
import com.tahirikosan.pokemonnft.databinding.FragmentResetPasswordBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ResetPasswordFragment : BaseFragment<FragmentResetPasswordBinding>(FragmentResetPasswordBinding::inflate) {

    private val viewModel by viewModels<AuthViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpWidgets()
        listenToChannels()
    }
    private fun listenToChannels() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.allEventsFlow.collect { event ->
                when(event){
                    is AuthViewModel.AllEvents.Message -> {
                        Toast.makeText(requireContext(), event.message, Toast.LENGTH_SHORT).show()
                        findNavController().navigate(ResetPasswordFragmentDirections.actionResetPasswordFragmentToAuthFragment())
                    }
                    is AuthViewModel.AllEvents.Error -> {
                       /* binding?.apply {
                            resetPassProgressBar.isInvisible = true
                            errorText.text = event.error
                        }*/
                    }
                    is AuthViewModel.AllEvents.ErrorCode -> {
                        /*if(event.code == 1)
                            binding?.apply {
                                userEmailEtvl.error = "email should not be empty!"
                                resetPassProgressBar.isInvisible = true
                            }*/
                    }
                }

            }
        }
    }

    private fun setUpWidgets() {
        binding?.apply {
            btnReset.setOnClickListener {
               //resetPassProgressBar.isVisible = true
                val email = etEmail.text.toString()
                viewModel.verifySendPasswordReset(email)
            }
        }
    }
}