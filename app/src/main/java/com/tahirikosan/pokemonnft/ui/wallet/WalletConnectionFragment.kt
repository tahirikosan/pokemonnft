package com.tahirikosan.pokemonnft.ui.wallet

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tahirikosan.pokemonnft.base.BaseFragment
import com.tahirikosan.pokemonnft.data.remote.Resource
import com.tahirikosan.pokemonnft.databinding.FragmentWalletConnectionBinding
import com.tahirikosan.pokemonnft.utils.Utils
import com.tahirikosan.pokemonnft.utils.Utils.enable
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class WalletConnectionFragment :
    BaseFragment<FragmentWalletConnectionBinding>(FragmentWalletConnectionBinding::inflate) {

    private val viewModel by viewModels<WalletConnectionViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleClicks()
        observe()
    }

    private fun handleClicks() {
        with(binding) {
            btnCreateWallet.setOnClickListener {
                btnCreateWallet.enable(false)
                viewModel.createWallet()
            }

            btnConnectToWallet.setOnClickListener {
                btnConnectToWallet.enable(false)
                viewModel.connectWallet()
            }

        }
    }

    private fun observe() {
        // Observe wallet.
        viewModel.walletResponse.observe(this, {
            //binding.viewLoading.visible(it is Resource.Loading)
            when (it) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    Timber.d(it.value.toString())
                    routeToGameMenuPage()
                }
                is Resource.Failure -> {
                    Utils.showToastShort(requireContext(), it.errorBody.toString())
                }
            }
            binding.btnCreateWallet.enable(true)
        })
    }

    private fun routeToGameMenuPage(){
        findNavController().navigate(WalletConnectionFragmentDirections.actionWalletConnectionFragmentToGameMenuFragment())
    }
}