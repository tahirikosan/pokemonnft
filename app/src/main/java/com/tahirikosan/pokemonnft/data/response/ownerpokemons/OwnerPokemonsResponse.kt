package com.tahirikosan.pokemonnft.data.response.ownerpokemons

import com.tahirikosan.pokemonnft.model.PokemonModel

data class OwnerPokemonsResponse(
    val pokemons: List<PokemonModel>,
    val offset: Int
)