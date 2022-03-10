package com.tahirikosan.pokemonnft.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.tahirikosan.pokemonnft.base.BaseFragment
import com.tahirikosan.pokemonnft.data.remote.Resource
import com.tahirikosan.pokemonnft.databinding.FragmentAuthBinding
import com.tahirikosan.pokemonnft.utils.Utils
import com.tahirikosan.pokemonnft.utils.Utils.enable
import com.tahirikosan.pokemonnft.utils.Utils.visible
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class AuthFragment : BaseFragment<FragmentAuthBinding>(FragmentAuthBinding::inflate) {

    private val viewModel by viewModels<AuthViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe()
        handleClicks()
    }

    private fun handleClicks(){
        with(binding){
            btnCreateWallet.setOnClickListener {
                btnCreateWallet.enable(false)
                viewModel.createWallet()
            }

            btnGetOwnerPokemons.setOnClickListener {
                btnGetOwnerPokemons.enable(false)
                viewModel.getOwnerPokemons("0x4Ba2D7ef7E418f54fA3AdAda1ec9ceFE9409aB14")
            }

            btnMintPokemon.setOnClickListener {
                btnMintPokemon.enable(false)
                viewModel.mintPokemon("1413ba0cf742eb529a8c216ac8241f8a9fe5494f98f80af7d3ea1748bdde8a9f")
            }
        }
    }

    private fun observe(){
        // Observe wallet.
        viewModel.walletResponse.observe(this, {
            binding.viewLoading.visible(it is Resource.Loading)
            when(it){
                is Resource.Loading->{
                }
                is Resource.Success ->{
                    Timber.d(it.value.toString())
                }
                is Resource.Failure ->{
                    Utils.showToastShort(requireContext(), it.errorBody.toString())
                }
            }
            binding.btnCreateWallet.enable(true)
        })

        // Observe owner pokemons.
        viewModel.ownerPokemonsResponse.observe(this, {
            binding.viewLoading.visible(it is Resource.Loading)
            when(it){
                is Resource.Loading->{
                }
                is Resource.Success ->{
                    Timber.d(it.value.toString())
                }
                is Resource.Failure ->{
                    Utils.showToastShort(requireContext(), it.errorBody.toString())
                }
            }
            binding.btnGetOwnerPokemons.enable(true)
        })

        // observe minting
        viewModel.mintPokemonResponse.observe(this, {
            binding.viewLoading.visible(it is Resource.Loading)
            when(it){
                is Resource.Loading->{
                }
                is Resource.Success ->{
                    Timber.d(it.value.toString())
                }
                is Resource.Failure ->{
                    Utils.showToastShort(requireContext(), it.errorBody.toString())
                }
            }
            binding.btnMintPokemon.enable(true)
        })
    }
}