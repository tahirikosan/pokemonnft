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
    private val viewModel: ShoppingViewModel by viewModels()
    private val args: PokemonDetailFragmentArgs by navArgs()
    private var userBalance: Int = 0
    private var pokemonPrice: Int = 0

    private val pokemonId by lazy {
        args.pokemonId
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getUserInfo()
        observe()
        handleClicks()
    }

    private fun observe() {
        viewModel.userInfo.observe(this, {
            binding.progressCircular.visible(it is Resource.Loading)
            when (it) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    // Set balance
                    userBalance = it.value.coin!!
                    // Check is user has pokemon already.
                    if (it.value.pokemons?.contains(pokemonId) == true) {
                        binding.btnBuy.visible(false)
                        binding.tvPrice.visible(false)
                    }
                    binding.tvBalance.text = "Balance: ${it.value.coin.toString()}"

                    viewModel.getPokemonDetailById(pokemonId)
                }
                is Resource.Failure -> {
                    Utils.showToastShort(requireContext(), it.errorBody.toString())
                }
            }
        })


        viewModel.pokedexPokemonResponse.observe(this, {
            binding.progressCircular.visible(it is Resource.Loading)
            when (it) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    Timber.d("Pokemons by type : " + it.value.toString())
                    setPokemonViews(it.value)
                }
                is Resource.Failure -> {
                    Utils.showToastShort(requireContext(), it.errorBody.toString())
                }
            }
        })

        /*    viewModel.isUserHavePokemon.observe(this, {
                binding.progressCircular.visible(it is Resource.Loading)
                when (it) {
                    is Resource.Loading -> {
                    }
                    is Resource.Success -> {
                        Timber.d("Pokemons by type : " + it.value.toString())
                        binding.btnBuy.enable(it.value.not())
                    }
                    is Resource.Failure -> {
                        Utils.showToastShort(requireContext(), it.errorBody.toString())
                    }
                }
            })*/

        viewModel.buyPokemonResponse.observe(this, {
            binding.progressCircular.visible(it is Resource.Loading)
            // binding.btnBuy.enable(it is Resource.Success || it is Resource.Failure)
            when (it) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    Timber.d("Pokemons by type : " + it.value.toString())
                    if (it.value) {
                        userBalance -= pokemonPrice
                        showToastLong(requireContext(), "Pokemon bought")
                        viewModel.isAlreadyHavePokemon(pokemonId)
                        binding.btnBuy.visible(false)
                        binding.tvPrice.visible(false)
                        binding.tvBalance.text = userBalance.toString()
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
            btnBuy.setOnClickListener {
                btnBuy.enable(false)
                viewModel.buyPokemon(pokemonPrice, pokemonId)
            }
        }
    }

    private fun setPokemonViews(pokemonDetails: PokedexPokemonResponse) {
        with(binding) {
            tvPokemonName.text = pokemonDetails.name
            val hp = pokemonDetails.stats[PokemonStatEnum.HEALTH_STAT.index].base_stat
            val ap = pokemonDetails.stats[PokemonStatEnum.ATTACK_STAT.index].base_stat
            val dp = pokemonDetails.stats[PokemonStatEnum.DEFENCE_STAT.index].base_stat
            val sp = pokemonDetails.stats[PokemonStatEnum.SPEED_STAT.index].base_stat
            tvHp.text = hp.toString()
            tvAp.text = ap.toString()
            tvDp.text = dp.toString()
            tvSp.text = sp.toString()
            tvPrice.text = "$ ${calculatePokemonPrice(hp, ap, dp, sp)}"
            Glide.with(requireContext()).load(pokemonImageUrlGenerateById(pokemonDetails.id))
                .into(ivPokemonImage)
            checkIfBalanceEnough()
        }
    }

    // Checks user balance if enough to buy the pokemon.
    private fun checkIfBalanceEnough() {
        binding.btnBuy.enable(userBalance >= pokemonPrice)
    }

    private fun calculatePokemonPrice(hp: Int, ap: Int, dp: Int, sp: Int): Int {
        pokemonPrice = (hp + ap + dp + sp + BASE_PRICE)
        return pokemonPrice
    }
}