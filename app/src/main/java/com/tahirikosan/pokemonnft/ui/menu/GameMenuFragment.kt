package com.tahirikosan.pokemonnft.ui.menu

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tahirikosan.pokemonnft.base.BaseFragment
import com.tahirikosan.pokemonnft.data.remote.Resource
import com.tahirikosan.pokemonnft.data.response.ownerpokemons.NFTPokemon
import com.tahirikosan.pokemonnft.data.response.ownerpokemons.NFTPokemon.Companion.toPokemonModel
import com.tahirikosan.pokemonnft.data.response.pokedex.pokemondetail.PokedexPokemonResponse
import com.tahirikosan.pokemonnft.data.response.pokedex.pokemondetail.PokedexPokemonResponse.Companion.toPokemonModel
import com.tahirikosan.pokemonnft.databinding.FragmentGameMenuBinding
import com.tahirikosan.pokemonnft.model.PokemonModel
import com.tahirikosan.pokemonnft.ui.adapter.pokemon.NFTPokemonAdapter
import com.tahirikosan.pokemonnft.ui.adapter.pokemon.PokemonAdapter
import com.tahirikosan.pokemonnft.utils.Utils
import com.tahirikosan.pokemonnft.utils.Utils.visible
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class GameMenuFragment : BaseFragment<FragmentGameMenuBinding>(FragmentGameMenuBinding::inflate) {

    private lateinit var nftPokemons: List<NFTPokemon>
    private lateinit var selectedPokemon: PokemonModel
    private lateinit var pokemonAdapter: PokemonAdapter
    private val viewModel: GameMenuViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setNormalPokemonRecyclerView()
        handleClicks()
        observe()
        viewModel.getUserPokemonIds()
    }

    override fun onStart() {
        super.onStart()
        viewModel.getOwnerNFTPokemons("0xd5207C1da3B83789F7B0b43A9154ca556FB642DA")
    }

    override fun onStop() {
        super.onDestroy()
        pokemonAdapter.clear()
    }

    private fun handleClicks() {
        with(binding) {
            btnGoToMinting.setOnClickListener {
                findNavController().navigate(GameMenuFragmentDirections.actionGameMenuFragmentToMintingFragment())
            }

            btnGoToShopping.setOnClickListener {
                findNavController().navigate(GameMenuFragmentDirections.actionGameMenuFragmentToShoppingFragment())
            }

            btnFight.setOnClickListener {
                findNavController().navigate(
                    GameMenuFragmentDirections.actionGameMenuFragmentToQueueFragment(
                        selectedPokemon
                    )
                )
            }
        }
    }

    private fun observe() {
        // Observe owner nftPokemons.
        viewModel.ownerNFTPokemonsResponse.observe(this, {
            binding.viewLoading.visible(it is Resource.Loading)
            when (it) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    Timber.d("Pokemons: " + it.value.toString())
                    nftPokemons = it.value.pokemons
                    binding.recyclerViewNftPokemons.adapter =
                        NFTPokemonAdapter(nftPokemons) { nftPokemon ->
                            selectedPokemon = nftPokemon.toPokemonModel()
                        }
                    binding.btnFight.visible(true)
                }
                is Resource.Failure -> {
                    Utils.showToastShort(requireContext(), it.errorBody.toString())
                }
            }
        })

        viewModel.userPokemonIds.observe(this, {
            binding.viewLoading.visible(it is Resource.Loading)
            when (it) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    Timber.d("Pokemons: " + it.value.toString())
                    viewModel.getPokemonsByIds(it.value)
                }
                is Resource.Failure -> {
                    Utils.showToastShort(requireContext(), it.errorBody.toString())
                }
            }
        })

        // Listen one pokemon detail and add it too pokemon list.
        viewModel.pokedexPokemonResponse.observe(this, {
            when (it) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    val pokemon = it.value.toPokemonModel()
                  //  normalPokemons.add(pokemon)
                    pokemonAdapter.addPokemon(pokemon)
                }
                is Resource.Failure -> {
                    Utils.showToastShort(requireContext(), it.errorBody.toString())
                }
            }
        })
    }

    private fun setNormalPokemonRecyclerView() {
        pokemonAdapter = PokemonAdapter(arrayListOf()) {
            selectedPokemon = it
        }
        binding.recyclerViewPokemons.adapter = pokemonAdapter
    }
}