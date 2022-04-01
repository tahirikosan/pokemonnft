package com.tahirikosan.pokemonnft.model

import android.os.Parcelable
import com.tahirikosan.pokemonnft.data.response.ownerpokemons.Attribute
import com.tahirikosan.pokemonnft.data.response.pokedex.pokemondetail.Type
import kotlinx.parcelize.Parcelize

@Parcelize
data class PokemonModel(
    var id: Int? = null,
    var attributes: List<Attribute>? = null,
    var image: String? = null,
    var name: String? = null,
    var isNft: Boolean = false,
    var types:  List<Type>? = null
) : Parcelable
