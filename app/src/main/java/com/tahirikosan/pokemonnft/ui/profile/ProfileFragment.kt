package com.tahirikosan.pokemonnft.ui.profile

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tahirikosan.pokemonnft.R
import com.tahirikosan.pokemonnft.base.BaseFragment
import com.tahirikosan.pokemonnft.data.remote.Resource
import com.tahirikosan.pokemonnft.databinding.FragmentProfileBinding
import com.tahirikosan.pokemonnft.utils.Utils
import com.tahirikosan.pokemonnft.utils.Utils.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {

    private val viewModel: ProfileViewModel by viewModels()
    private var isMnemonicVisible = false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleClicks()
        setViews()
        observe()
        viewModel.getCurrentUser()
    }

    private fun setViews() {
        with(binding) {
            userPreferences.publicKey?.let {
                tvPublicKey.text = it
            }
        }
    }

    private fun observe() {
        viewModel.userInfo.observe(this, {
            binding.viewLoading.visible(it is Resource.Loading)
            when (it) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    with(binding) {
                        tvBalance.text = it.value.coin.toString()
                        tvPvp.text = it.value.pvp.toString()
                    }
                }
                is Resource.Failure -> {
                    Utils.showToastShort(requireContext(), it.errorBody.toString())
                }
            }
        })
    }

    private fun handleClicks() {
        with(binding) {
            btnSignout.setOnClickListener {
                viewModel.sigout()
                findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToAuthFragment())
            }
            btnConnectToWallet.setOnClickListener {
                findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToWalletConnectionFragment())
            }

            ivToggleMnemonic.setOnClickListener {
                toggleMnemonic()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun toggleMnemonic() {
        if (isMnemonicVisible) {
            binding.ivToggleMnemonic.setImageResource(
                R.drawable.ic_baseline_lock_24
            )
            // Corrupt mnemonic view
            binding.tvMnemonic.text = "Click lock image to unlock mnemonic text."
        } else {
            binding.ivToggleMnemonic.setImageResource(
                R.drawable.ic_baseline_lock_open_24
            )
            // Corrupt mnemonic view
            userPreferences.getMnemonic()?.let {
                binding.tvMnemonic.text =
                    "Phrase: ${it.phrase} \n\nPath: ${it.path} \n\nLocale: ${it.locale}"
            }
        }
        isMnemonicVisible = !isMnemonicVisible
    }
}