package com.tahirikosan.pokemonnft.ui.menu

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tahirikosan.pokemonnft.base.BaseFragment
import com.tahirikosan.pokemonnft.data.remote.Resource
import com.tahirikosan.pokemonnft.data.response.ownerpokemons.NFTPokemon
import com.tahirikosan.pokemonnft.data.response.ownerpokemons.NFTPokemon.Companion.toPokemonModel
import com.tahirikosan.pokemonnft.data.response.pokedex.pokemondetail.PokedexPokemonResponse.Companion.toPokemonModel
import com.tahirikosan.pokemonnft.databinding.FragmentGameMenuBinding
import com.tahirikosan.pokemonnft.model.PokemonModel
import com.tahirikosan.pokemonnft.ui.adapter.pokemon.NFTPokemonAdapter
import com.tahirikosan.pokemonnft.ui.adapter.pokemon.PokemonAdapter
import com.tahirikosan.pokemonnft.utils.Utils
import com.tahirikosan.pokemonnft.utils.Utils.enable
import com.tahirikosan.pokemonnft.utils.Utils.showToastShort
import com.tahirikosan.pokemonnft.utils.Utils.visible
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import android.widget.Toast





@AndroidEntryPoint
class GameMenuFragment : BaseFragment<FragmentGameMenuBinding>(FragmentGameMenuBinding::inflate) {

    private lateinit var nftPokemons: List<NFTPokemon>
    private var selectedPokemon: PokemonModel? = null
    private lateinit var pokemonAdapter: PokemonAdapter
    private var nftPokemonAdapter: NFTPokemonAdapter? = null
    private val viewModel: GameMenuViewModel by viewModels()

    private var pokemonOffset = 0
    private lateinit var userId: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleClicks()
        setNormalPokemonsAdapter()
        listenNestedScrollView()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observe()
    }

    override fun onStart() {
        super.onStart()
        viewModel.getOwnerNFTPokemons("0xD33f5362E537A7e74547DD6a6C4601c98834b330")
        viewModel.getCurrentUser()
    }


    private fun handleClicks() {
        with(binding) {
            ivGoToMinting.setOnClickListener {
                findNavController().navigate(GameMenuFragmentDirections.actionGameMenuFragmentToMintingFragment())
            }

            ivGoToShopping.setOnClickListener {
                findNavController().navigate(GameMenuFragmentDirections.actionGameMenuFragmentToShoppingFragment())
            }

            ivFight.setOnClickListener {
                if (selectedPokemon != null) {
                    findNavController().navigate(
                        GameMenuFragmentDirections.actionGameMenuFragmentToQueueFragment(
                            selectedPokemon!!
                        )
                    )
                } else {
                    showToastShort(requireContext(), "Please select a pokemon to fight!")
                }
            }

            ivGoToProfile.setOnClickListener {
                findNavController().navigate(GameMenuFragmentDirections.actionGameMenuFragmentToProfileFragment())
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
                    // Set warning message visibility
                    binding.tvNftPokemonsNotFound.visible(nftPokemons.isEmpty())
                    setNFTPokemonAdapter()
                    binding.ivFight.visible(true)
                }
                is Resource.Failure -> {
                    Utils.showToastShort(requireContext(), it.errorBody.toString())
                }
            }
        })

        viewModel.currentUser.observe(this, {
            binding.viewLoading.visible(it is Resource.Loading)
            when (it) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    Timber.d("Userid: " + it.value.toString())
                    userId = it.value!!.uid
                    viewModel.getOwnerPokemonsByOffset(it.value!!.uid, pokemonOffset)
                }
                is Resource.Failure -> {
                    Utils.showToastShort(requireContext(), it.errorBody.toString())
                }
            }
        })

        // Listen one pokemon detail and add it too pokemon list.
        viewModel.ownerPokemonsResponse.observe(this, {
            when (it) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    // Set warning message visibility
                    binding.tvPokemonsNotFound.visible(it.value.pokemons.isEmpty())
                    // Update offset
                    pokemonOffset = it.value.offset
                    pokemonAdapter.addNewPokemons(it.value.pokemons)
                }
                is Resource.Failure -> {
                    Utils.showToastShort(requireContext(), it.errorBody.toString())
                }
            }
        })
    }

    private fun setNFTPokemonAdapter() {
        nftPokemonAdapter = NFTPokemonAdapter(nftPokemons) { nftPokemon ->
            // Remove selection from normal pokemon adapter.
            pokemonAdapter.removeSelection()
            selectedPokemon = nftPokemon.toPokemonModel()
            binding.ivFight.enable(true)
        }
        binding.recyclerViewNftPokemons.adapter = nftPokemonAdapter
    }

    private fun setNormalPokemonsAdapter() {
        pokemonAdapter = PokemonAdapter(arrayListOf()) {
            // Remove nft pokemon selection.
            nftPokemonAdapter?.removeSelection()
            selectedPokemon = it
            binding.ivFight.enable(true)
        }
        binding.recyclerViewPokemons.adapter = pokemonAdapter
    }

    // Listen if user is at the end of nested scroll view to load new pokemons.
    private fun listenNestedScrollView(){
        binding.recyclerViewPokemons.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollHorizontally(1)) {
                    viewModel.getOwnerPokemonsByOffset(userId, pokemonOffset)
                }
            }
        })
    }
}