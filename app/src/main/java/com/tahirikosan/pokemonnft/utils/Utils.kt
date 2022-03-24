package com.tahirikosan.pokemonnft.utils

import android.content.Context
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import com.tahirikosan.pokemonnft.MainActivity

object Utils {
    fun showToastShort(context: Context, message: String){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun showToastLong(context: Context, message: String){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    // Enables view or not.
    fun View.enable(enabled: Boolean) {
        isEnabled = enabled
        alpha = if (enabled) 1f else 0.5f
    }

    // Makes view visible or gone
    fun View.visible(isVisible: Boolean) {
        visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    fun EditText.trimmedText() = this.text.trim().toString()

    fun Fragment.onBackPressed(onBackPressed: ()->Unit){
        this.requireActivity().onBackPressedDispatcher.addCallback(this) {
            onBackPressed.invoke()
        }
    }

    fun pokemonImageUrlGenerateById(pokemonId: Int) = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/${pokemonId}.png"
}