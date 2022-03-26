package com.tahirikosan.pokemonnft.data.repository

import com.tahirikosan.pokemonnft.data.remote.Resource
import com.tahirikosan.pokemonnft.data.remote.api.PokedexApi
import com.tahirikosan.pokemonnft.data.response.pokedex.pokemondetail.PokedexPokemonResponse
import javax.inject.Inject


class PokedexRepository @Inject constructor(
    private val api: PokedexApi
): BaseRepository() {

    suspend fun getPokedexByType(type: Int) = safeApiCall {
        api.getPokedexByType(type)
    }

    suspend fun getPokemonDetailById(id: Int): Resource<PokedexPokemonResponse> = safeApiCall {
        api.getPokemonDetailById(id)
    }
}