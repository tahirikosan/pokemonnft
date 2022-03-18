package com.tahirikosan.pokemonnft.ui.fight

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tahirikosan.pokemonnft.base.BaseFragment
import com.tahirikosan.pokemonnft.databinding.FragmentFightResultBinding
import com.tahirikosan.pokemonnft.utils.Utils.onBackPressed

class FightResultFragment :
    BaseFragment<FragmentFightResultBinding>(FragmentFightResultBinding::inflate) {
    private val args: FightResultFragmentArgs by navArgs()

    private val isWon by lazy {
        args.isWon
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(isWon){
            binding.resultText.text = "YOU HAVE WON!"
        }else{
            binding.resultText.text = "YOU HAVE LOST!"

        }

        onBackPressed {
            routeToGameMenu()
        }
    }

    private fun routeToGameMenu() {
        findNavController().navigate(FightResultFragmentDirections.actionFightResultFragmentToGameMenuFragment())
    }
}