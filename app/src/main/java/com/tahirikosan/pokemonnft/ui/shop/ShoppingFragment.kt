package com.tahirikosan.pokemonnft.ui.shop

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tahirikosan.pokemonnft.base.BaseFragment
import com.tahirikosan.pokemonnft.data.remote.Resource
import com.tahirikosan.pokemonnft.data.response.pokedex.PokedexPokemonWrapper
import com.tahirikosan.pokemonnft.databinding.FragmentShoppingBinding
import com.tahirikosan.pokemonnft.ui.adapter.filter.PokedexFilterAdapter
import com.tahirikosan.pokemonnft.ui.adapter.pokedex.PokedexAdapter
import com.tahirikosan.pokemonnft.ui.adapter.pokedex.PokedexItemUIModel
import com.tahirikosan.pokemonnft.utils.Utils
import com.tahirikosan.pokemonnft.utils.Utils.pokemonImageUrlGenerateById
import com.tahirikosan.pokemonnft.utils.Utils.showToastShort
import com.tahirikosan.pokemonnft.utils.Utils.visible
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class ShoppingFragment : BaseFragment<FragmentShoppingBinding>(FragmentShoppingBinding::inflate) {

    private val viewModel: ShoppingViewModel by viewModels()
    private lateinit var adapter: PokedexAdapter
    private lateinit var filterAdapter: PokedexFilterAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getPokedexByType(1)
        setFilterAdapter()
        observe()
        handleClicks()
    }


    fun observe() {
        viewModel.pokedexTypeResponse.observe(this, {
            binding.viewLoading.visible(it is Resource.Loading)
            when (it) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    Timber.d("Pokemons by type : " + it.value.toString())
                    setPokedexRecyclerView(convertPokemonsToUIModel(it.value.pokemon))
                }
                is Resource.Failure -> {
                    Utils.showToastShort(requireContext(), it.errorBody.toString())
                }
            }
        })
    }

    fun handleClicks() {

    }

    private fun setPokedexRecyclerView(pokedex: ArrayList<PokedexItemUIModel>) {
        adapter = PokedexAdapter(pokedex){
            findNavController().navigate(ShoppingFragmentDirections.actionShoppingFragmentToPokemonDetailFragment(it))
        }
        binding.recyclerViewPokedex.adapter = adapter
    }

    private fun setFilterAdapter() {
        filterAdapter = PokedexFilterAdapter {
            showToastShort(requireContext(), it.toString())
            viewModel.getPokedexByType(it)
        }
        binding.recyclerViewPokedexFilter.adapter = filterAdapter
    }


    // Create pokemon UI model for adapter.
    private fun convertPokemonsToUIModel(pokemonWrappers: List<PokedexPokemonWrapper>): ArrayList<PokedexItemUIModel> {
        var pokemons = ArrayList<PokedexItemUIModel>()
        var pokemon: PokedexItemUIModel
        pokemonWrappers.forEach {
            val id = extractPokemonIdFromUrl(it.pokemon.url)
            val pokemonName = it.pokemon.name
            val imageUrl = pokemonImageUrlGenerateById(id)
            pokemon = PokedexItemUIModel(
                id,
                pokemonName,
                imageUrl
            )
            pokemons.add(pokemon)
        }
        return pokemons
    }

    // Get pokemon id from pokemon url.
    private fun extractPokemonIdFromUrl(url: String): Int {
        val parts = url.split("/")
        return parts[6].toInt()
    }
}