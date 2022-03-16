package com.tahirikosan.pokemonnft.ui.wallet

import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tahirikosan.pokemonnft.base.BaseFragment
import com.tahirikosan.pokemonnft.data.remote.Resource
import com.tahirikosan.pokemonnft.databinding.FragmentWalletConnectionBinding
import com.tahirikosan.pokemonnft.utils.Anonymous
import com.tahirikosan.pokemonnft.utils.Utils
import com.tahirikosan.pokemonnft.utils.Utils.enable
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

@AndroidEntryPoint
class WalletConnectionFragment :
    BaseFragment<FragmentWalletConnectionBinding>(FragmentWalletConnectionBinding::inflate) {

    private val TEST_MNEMONIC = ""
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
                viewModel.connectWallet(etMnemonic.text.trim().toString())
            }

        }
    }

    private fun observe() {
        // Observe wallet.
        viewModel.walletCreateResponse.observe(this, {
            //binding.viewLoading.visible(it is Resource.Loading)
            when (it) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    Timber.d(it.value.toString())
                    userPreferences.savePrivateKey(it.value.privateKey)
                    userPreferences.savePublicKey(it.value.publicKey)
                    binding.etMnemonic.setText(it.value.mnemonic.phrase)
                    //routeToGameMenuPage()
                }
                is Resource.Failure -> {
                    Utils.showToastShort(requireContext(), it.errorBody.toString())
                }
            }
            binding.btnCreateWallet.enable(true)
        })

        viewModel.walletConnectResponse.observe(this, {
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
            binding.btnConnectToWallet.enable(true)
        })
    }

    private fun routeToGameMenuPage() {
        findNavController().navigate(WalletConnectionFragmentDirections.actionWalletConnectionFragmentToGameMenuFragment())
    }
}