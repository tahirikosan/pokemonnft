package com.tahirikosan.pokemonnft.ui.fight

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.tahirikosan.pokemonnft.base.BaseFragment
import com.tahirikosan.pokemonnft.databinding.FragmentFightResultBinding
import com.tahirikosan.pokemonnft.utils.Utils.onBackPressed

class FightResultFragment:BaseFragment<FragmentFightResultBinding>(FragmentFightResultBinding::inflate) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onBackPressed {
            routeToGameMenu()
        }
    }

    private fun routeToGameMenu(){
        findNavController().navigate(FightResultFragmentDirections.actionFightResultFragmentToGameMenuFragment())
    }
}