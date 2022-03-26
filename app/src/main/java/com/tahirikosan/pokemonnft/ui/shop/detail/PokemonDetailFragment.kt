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
import com.tahirikosan.pokemonnft.enum.PokemonStatEnum
import com.tahirikosan.pokemonnft.ui.shop.ShoppingViewModel
import com.tahirikosan.pokemonnft.utils.Utils
import com.tahirikosan.pokemonnft.utils.Utils.enable
import com.tahirikosan.pokemonnft.utils.Utils.pokemonImageUrlGenerateById
import com.tahirikosan.pokemonnft.utils.Utils.showToastLong
import com.tahirikosan.pokemonnft.utils.Utils.visible
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class PokemonDetailFragment :
    BaseFragment<FragmentPokemonDetailBuyBinding>(FragmentPokemonDetailBuyBinding::inflate) {

    private val BASE_PRICE = 500
    private var pokemonPrice: Int = 0
    private val viewModel: ShoppingViewModel by viewModels()
    private val args: PokemonDetailFragmentArgs by navArgs()

    private val pokemonId by lazy {
        args.pokemonId
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getPokemonDetailById(pokemonId)
        viewModel.isAlreadyHavePokemon(pokemonId)
        observe()
        handleClicks()
    }

    fun observe() {
        viewModel.pokedexPokemonResponse.observe(this, {
            binding.viewLoading.visible(it is Resource.Loading)
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

        viewModel.isUserHavePokemon.observe(this, {
            binding.viewLoading.visible(it is Resource.Loading)
            when (it) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    Timber.d("Pokemons by type : " + it.value.toString())
                    binding.buyPokemonButton.enable(it.value.not())
                }
                is Resource.Failure -> {
                    Utils.showToastShort(requireContext(), it.errorBody.toString())
                }
            }
        })

        viewModel.buyPokemonResponse.observe(this, {
            binding.viewLoading.visible(it is Resource.Loading)
          // binding.buyPokemonButton.enable(it is Resource.Success || it is Resource.Failure)
            when (it) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    Timber.d("Pokemons by type : " + it.value.toString())
                    if (it.value) {
                        showToastLong(requireContext(), "Pokemon bought")
                        viewModel.isAlreadyHavePokemon(pokemonId)
                    } else {
                        showToastLong(requireContext(), "Not enought coin.")
                    }
                }
                is Resource.Failure -> {
                    Utils.showToastShort(requireContext(), it.errorBody.toString())
                }
            }
        })
    }


    private fun handleClicks() {
        with(binding) {
            buyPokemonButton.setOnClickListener {
                buyPokemonButton.enable(false)
                viewModel.buyPokemon(pokemonPrice, pokemonId)
            }
        }
    }

    private fun setViews(pokemonDetails: PokedexPokemonResponse) {
        with(binding) {
            tvPokemonName.text = pokemonDetails.name
            val hp = pokemonDetails.stats[PokemonStatEnum.HEALTH_STAT.index].base_stat
            val ap = pokemonDetails.stats[PokemonStatEnum.ATTACK_STAT.index].base_stat
            val dp = pokemonDetails.stats[PokemonStatEnum.DEFENCE_STAT.index].base_stat
            val sp = pokemonDetails.stats[PokemonStatEnum.SPEED_STAT.index].base_stat
            tvPokemonHp.text = hp.toString()
            tvPokemonAp.text = ap.toString()
            tvPokemonDp.text = dp.toString()
            tvPokemonSp.text = sp.toString()
            tvPokemonPrice.text = calculatePokemonPrice(hp, ap, dp, sp).toString()
            Glide.with(requireContext()).load(pokemonImageUrlGenerateById(pokemonDetails.id))
                .into(pokemonImage)
        }
    }

    private fun calculatePokemonPrice(hp: Int, ap: Int, dp: Int, sp: Int): Int {
        pokemonPrice = (hp + ap + dp + sp + BASE_PRICE)
        return pokemonPrice
    }
}