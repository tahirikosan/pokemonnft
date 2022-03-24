package com.tahirikosan.pokemonnft.data.response.pokedex


data class PokedexByTypeResponse(
    // Pokemon type 
    val name: String,
    // pokedex
    val pokemon: List<PokedexPokemonWrapper>
)