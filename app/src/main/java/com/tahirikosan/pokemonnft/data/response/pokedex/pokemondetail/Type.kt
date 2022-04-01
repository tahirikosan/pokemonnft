package com.tahirikosan.pokemonnft.data.response.pokedex.pokemondetail

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Type(
    val slot: Int,
    val type: TypeX
): Parcelable