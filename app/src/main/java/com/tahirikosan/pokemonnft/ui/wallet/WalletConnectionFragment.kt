package com.tahirikosan.pokemonnft.ui.wallet

import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tahirikosan.pokemonnft.base.BaseFragment
import com.tahirikosan.pokemonnft.data.remote.Resource
import com.tahirikosan.pokemonnft.data.response.wallet.Mnemonic
import com.tahirikosan.pokemonnft.databinding.FragmentWalletConnectionBinding
import com.tahirikosan.pokemonnft.utils.Anonymous
import com.tahirikosan.pokemonnft.utils.Utils
import com.tahirikosan.pokemonnft.utils.Utils.enable
import com.tahirikosan.pokemonnft.utils.Utils.visible
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.security.KeyStore
import java.security.PrivateKey
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

@AndroidEntryPoint
class WalletConnectionFragment :
    BaseFragment<FragmentWalletConnectionBinding>(FragmentWalletConnectionBinding::inflate) {

    private val viewModel by viewModels<WalletConnectionViewModel>()
    private var publicKey: String? = null
    private var privateKey: String? = null
    private var mnemonic: Mnemonic? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleClicks()
        observe()
        toggleConnectionViews()
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
            btnDisconnectWallet.setOnClickListener {
                viewModel.removePublicKeyToFirestore(userPreferences.publicKey!!)
            }

        }
    }

    private fun observe() {
        // Observe wallet.
        viewModel.walletCreateResponse.observe(this, {
            binding.viewLoading.visible(it is Resource.Loading)
            when (it) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    binding.etMnemonic.setText(it.value.mnemonic.phrase)
                }
                is Resource.Failure -> {
                    Utils.showToastShort(requireContext(), it.errorBody.toString())
                }
            }
            binding.btnCreateWallet.enable(true)
        })

        viewModel.walletConnectResponse.observe(this, {
            binding.viewLoading.visible(it is Resource.Loading)
            when (it) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    privateKey = it.value.privateKey
                    publicKey = it.value.publicKey
                    mnemonic = it.value.mnemonic

                    viewModel.isWalletOccupied(publicKey!!)
                }
                is Resource.Failure -> {
                    Utils.showToastShort(requireContext(), it.errorBody.toString())
                }
            }
            binding.btnConnectToWallet.enable(true)
        })

        viewModel.walletOccupiedBool.observe(this, {
            binding.viewLoading.visible(it is Resource.Loading)
            when (it) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    // If wallet not occupied.
                    if (!it.value) {
                        viewModel.addPublicKeyToFirestore(publicKey!!)
                    } else {
                        Utils.showToastShort(requireContext(), "The wallet is already in use.")
                    }
                }
                is Resource.Failure -> {
                    Utils.showToastShort(requireContext(), it.errorBody.toString())
                }
            }
            binding.btnConnectToWallet.enable(true)
        })

        viewModel.walletAddToOccupiedListBool.observe(this, {
            binding.viewLoading.visible(it is Resource.Loading)
            when (it) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    // If wallet added to occupied list successfully.
                    if (it.value) {
                        userPreferences.savePrivateKey(privateKey!!)
                        userPreferences.savePublicKey(publicKey!!)
                        userPreferences.saveMnemonic(mnemonic!!)
                        routeToGameMenuPage()
                    }
                }
                is Resource.Failure -> {
                    Utils.showToastShort(requireContext(), it.errorBody.toString())
                }
            }
            binding.btnConnectToWallet.enable(true)
        })

        viewModel.walletRemoveToOccupiedListBool.observe(this, {
            binding.viewLoading.visible(it is Resource.Loading)
            when (it) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    // If wallet removed from occupied list successfully.
                    if (it.value) {
                        // Then clear shared prefs.
                        userPreferences.clear()
                        toggleConnectionViews()
                    }
                }
                is Resource.Failure -> {
                    Utils.showToastShort(requireContext(), it.errorBody.toString())
                }
            }
            binding.btnConnectToWallet.enable(true)
        })
    }

    private fun toggleConnectionViews() {
        binding.layoutConnection.visible(userPreferences.publicKey == null)
        binding.btnDisconnectWallet.visible(userPreferences.publicKey != null)
    }

    private fun routeToGameMenuPage() {
        findNavController().navigate(WalletConnectionFragmentDirections.actionWalletConnectionFragmentToGameMenuFragment())
    }
}