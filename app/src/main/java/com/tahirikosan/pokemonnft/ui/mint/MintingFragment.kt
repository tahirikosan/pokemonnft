package com.tahirikosan.pokemonnft.ui.mint

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.tahirikosan.pokemonnft.base.BaseFragment
import com.tahirikosan.pokemonnft.data.remote.Resource
import com.tahirikosan.pokemonnft.databinding.FragmentMintingBinding
import com.tahirikosan.pokemonnft.utils.Utils
import com.tahirikosan.pokemonnft.utils.Utils.enable
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MintingFragment : BaseFragment<FragmentMintingBinding>(FragmentMintingBinding::inflate) {

    private val viewModel: MintViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe()
        viewModel.getMintedPokemon("0x567a3a3bc7e88100f5ac24eb052ff3688ee22e676215bff47cb4b792c1ec8b0c")
        handleClicks()
    }

    private fun observe() {
        // observe minting
        viewModel.mintPokemonResponse.observe(this, {
            // binding.viewLoading.visible(it is Resource.Loading)
            when (it) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    Timber.d(it.value.toString())
                    viewModel.getMintedPokemon(it.value.hash)
                }
                is Resource.Failure -> {
                    Utils.showToastShort(requireContext(), it.errorBody.toString())
                    binding.btnMint.enable(true)
                }
            }
        })
        // observe mited pokemon
        viewModel.mintedPokemon.observe(this, {
            // binding.viewLoading.visible(it is Resource.Loading)
            when (it) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    Timber.d(it.value.toString())
                    Utils.showToastShort(requireContext(), it.value.toString())
                }
                is Resource.Failure -> {
                    Utils.showToastShort(requireContext(), it.errorBody.toString())
                }
            }
            binding.btnMint.enable(true)
        })
    }

    private fun handleClicks() {
        with(binding) {
            btnMint.setOnClickListener {
                viewModel.mintPokemon("0bf07f36c862d6a625e59407d10e5eee8fc17e5c5bc0e46fed917be9b69238bd")
            }
        }
    }
}