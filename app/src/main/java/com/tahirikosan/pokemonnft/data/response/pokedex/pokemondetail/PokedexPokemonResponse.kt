package com.tahirikosan.pokemonnft.data.response.pokedex.pokemondetail

data class PokedexPokemonResponse(
    val id: Int,
    val name: String,
    val stats: List<Stat>,
    val types: List<Type>,
)
