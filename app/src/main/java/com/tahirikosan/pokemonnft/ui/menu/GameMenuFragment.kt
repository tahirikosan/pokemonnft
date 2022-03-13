package com.tahirikosan.pokemonnft.ui.menu

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.tahirikosan.pokemonnft.base.BaseFragment
import com.tahirikosan.pokemonnft.data.remote.Resource
import com.tahirikosan.pokemonnft.databinding.FragmentGameMenuBinding
import com.tahirikosan.pokemonnft.utils.Utils
import com.tahirikosan.pokemonnft.utils.Utils.visible
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class GameMenuFragment : BaseFragment<FragmentGameMenuBinding>(FragmentGameMenuBinding::inflate) {

    private val viewModel: GameMenuViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleClicks()
        observe()
    }

    override fun onStart() {
        super.onStart()
        viewModel.getOwnerPokemons("0xe23039537a44e0C338e3B087F93f31089A36900a")
    }

    private fun handleClicks() {
        with(binding) {

            btnGoToMinting.setOnClickListener {
               // viewModel.mintPokemon("1413ba0cf742eb529a8c216ac8241f8a9fe5494f98f80af7d3ea1748bdde8a9f")
            }
        }
    }

    private fun observe() {
        // Observe owner pokemons.
        viewModel.ownerPokemonsResponse.observe(this, {
            binding.viewLoading.visible(it is Resource.Loading)
            when (it) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    Timber.d("Pokemons: " + it.value.toString())
                }
                is Resource.Failure -> {
                    Utils.showToastShort(requireContext(), it.errorBody.toString())
                }
            }
        })
        // observe minting
        /*viewModel.mintPokemonResponse.observe(this, {
            binding.viewLoading.visible(it is Resource.Loading)
            when (it) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    Timber.d(it.value.toString())
                }
                is Resource.Failure -> {
                    Utils.showToastShort(requireContext(), it.errorBody.toString())
                }
            }
            binding.btnMintPokemon.enable(true)
        })*/
    }

    private fun routeToMintingPage(){

    }
}