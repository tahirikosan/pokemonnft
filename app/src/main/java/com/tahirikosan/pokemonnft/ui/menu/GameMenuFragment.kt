package com.tahirikosan.pokemonnft.ui.menu

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tahirikosan.pokemonnft.base.BaseFragment
import com.tahirikosan.pokemonnft.data.remote.Resource
import com.tahirikosan.pokemonnft.data.response.ownerpokemons.Pokemon
import com.tahirikosan.pokemonnft.databinding.FragmentGameMenuBinding
import com.tahirikosan.pokemonnft.ui.adapter.pokemon.PokemonAdapter
import com.tahirikosan.pokemonnft.utils.Utils
import com.tahirikosan.pokemonnft.utils.Utils.visible
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class GameMenuFragment : BaseFragment<FragmentGameMenuBinding>(FragmentGameMenuBinding::inflate) {

    private lateinit var pokemons: List<Pokemon>
    private val viewModel: GameMenuViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleClicks()
        observe()
    }

    override fun onStart() {
        super.onStart()
        viewModel.getOwnerPokemons("0xd5207C1da3B83789F7B0b43A9154ca556FB642DA")
    }

    private fun handleClicks() {
        with(binding) {
            btnGoToMinting.setOnClickListener {
                findNavController().navigate(GameMenuFragmentDirections.actionGameMenuFragmentToMintingFragment())
            }

            btnFight.setOnClickListener {
                findNavController().navigate(GameMenuFragmentDirections.actionGameMenuFragmentToQueueFragment(pokemons[0]))
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
                    pokemons = it.value.pokemons
                    binding.recyclerViewCards.adapter = PokemonAdapter(pokemons)
                    binding.btnFight.visible(true)
                }
                is Resource.Failure -> {
                    Utils.showToastShort(requireContext(), it.errorBody.toString())
                }
            }
        })
    }

    private fun routeToMintingPage() {

    }
}