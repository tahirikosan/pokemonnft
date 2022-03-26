package com.tahirikosan.pokemonnft.model

import android.os.Parcelable
import com.tahirikosan.pokemonnft.data.response.ownerpokemons.Attribute
import kotlinx.parcelize.Parcelize

@Parcelize
data class PokemonModel(
    var attributes: List<Attribute>? = null,
    var image: String? = null,
    var name: String? = null,
    var isNft: Boolean = false,
): Parcelable
