package com.tahirikosan.pokemonnft.data.response.pokedex.pokemondetail

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TypeX(
    val name: String,
    val url: String
): Parcelable