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
        viewModel.getMintedPokemon("0xd2638055075e9018c27877deff3cfa033abecd41d19cdfd80bd30d8e34f9eb94")
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
        viewModel.mintedNFTPokemon.observe(this, {
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
                viewModel.mintPokemon("d34c3e1cb5264a7372a5ee8a7e8aa6e28b8c2158797472dcacf2435195ffad05")
            }
        }
    }
}