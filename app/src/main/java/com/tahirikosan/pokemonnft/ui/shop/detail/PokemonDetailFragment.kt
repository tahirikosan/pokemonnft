package com.tahirikosan.pokemonnft.ui.shop.detail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.tahirikosan.pokemonnft.base.BaseFragment
import com.tahirikosan.pokemonnft.data.remote.Resource
import com.tahirikosan.pokemonnft.data.response.pokedex.pokemondetail.PokedexPokemonResponse
import com.tahirikosan.pokemonnft.databinding.FragmentPokemonDetailBuyBinding
import com.tahirikosan.pokemonnft.ui.shop.ShoppingViewModel
import com.tahirikosan.pokemonnft.utils.Utils
import com.tahirikosan.pokemonnft.utils.Utils.pokemonImageUrlGenerateById
import com.tahirikosan.pokemonnft.utils.Utils.visible
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class PokemonDetailFragment :
    BaseFragment<FragmentPokemonDetailBuyBinding>(FragmentPokemonDetailBuyBinding::inflate) {

    private val viewModel: ShoppingViewModel by viewModels()
    private val args: PokemonDetailFragmentArgs by navArgs()

    private val pokemonId by lazy {
        args.pokemonId
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getPokedexByType(pokemonId)
        observe()
        handleClicks()
    }

    fun observe(){
        viewModel.pokedexPokemonResponse.observe(this, {
            binding.viewLoading.visible(it is Resource.Loading)
            binding.buyPokemonButton.visible(it is Resource.Success)
            when (it) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    Timber.d("Pokemons by type : " + it.value.toString())
                    setViews(it.value)
                }
                is Resource.Failure -> {
                    Utils.showToastShort(requireContext(), it.errorBody.toString())
                }
            }
        })
    }


    fun handleClicks(){
        with(binding){
            buyPokemonButton.setOnClickListener {
               // viewModel.buyPokemon(pokemonId)
            }
        }
    }

    private fun setViews(pokemonDetails: PokedexPokemonResponse){
        with(binding){
            Glide.with(requireContext()).load(pokemonImageUrlGenerateById(pokemonDetails.id)).into(pokemonImage)
        }
    }
}