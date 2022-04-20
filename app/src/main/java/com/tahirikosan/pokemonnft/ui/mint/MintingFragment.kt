package com.tahirikosan.pokemonnft.ui.mint

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.tahirikosan.pokemonnft.base.BaseFragment
import com.tahirikosan.pokemonnft.data.remote.Resource
import com.tahirikosan.pokemonnft.data.response.user.User
import com.tahirikosan.pokemonnft.databinding.FragmentMintingBinding
import com.tahirikosan.pokemonnft.utils.Utils
import com.tahirikosan.pokemonnft.utils.Utils.enable
import com.tahirikosan.pokemonnft.utils.Utils.showToastShort
import com.tahirikosan.pokemonnft.utils.Utils.visible
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import kotlin.math.min

@AndroidEntryPoint
class MintingFragment : BaseFragment<FragmentMintingBinding>(FragmentMintingBinding::inflate) {

    private val viewModel: MintViewModel by viewModels()
    private val mintPrice: Int = 3000
    private lateinit var user: User

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        set()
        observe()
        viewModel.getUserInfo()
        Timber.d(userPreferences.getMnemonic()?.phrase)
        //viewModel.getMintedPokemon("0xd2638055075e9018c27877deff3cfa033abecd41d19cdfd80bd30d8e34f9eb94")
        handleClicks()
    }

    private fun set() {
        with(binding) {
            tvPrice.text = "$ $mintPrice"
        }
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
                    user.coin = user.coin!! - mintPrice
                    binding.tvBalance.text = "Balance: ${user.coin}"
                    binding.btnMint.enable(user.coin!! >= mintPrice)
                    showToastShort(requireContext(), "Pokemon Successfully Minted!")
                    //viewModel.getMintedPokemon(it.value.hash)
                }
                is Resource.Failure -> {
                    Utils.showToastShort(requireContext(), it.errorBody.toString())
                    binding.btnMint.enable(true)
                }
            }
        })

        viewModel.userInfo.observe(this, {
            when (it) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    // Set balance
                    user = it.value
                    // Check is user has pokemon already.
                    if (user.coin!! >= mintPrice) {
                        binding.btnMint.enable(true)
                    }
                    binding.tvBalance.text = "Balance: ${user.coin.toString()}"
                }
                is Resource.Failure -> {
                    Utils.showToastShort(requireContext(), it.errorBody.toString())
                }
            }
        })

        // observe mited pokemon
        /* viewModel.mintedNFTPokemon.observe(this, {
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
         })*/
    }

    private fun handleClicks() {
        with(binding) {
            btnMint.enable(false)
            btnMint.setOnClickListener {
                YoYo.with(Techniques.Wobble)
                    .duration(700)
                    .repeat(2)
                    .playOn(binding.pokeball)
                viewModel.mintPokemon(
                    "bea763921bdc14f0c256744e2268e828a26f2242292db7ff33c29547b43a0fc6",
                    user.userId!!
                )
            }
        }
    }
}