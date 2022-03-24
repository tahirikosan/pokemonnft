package com.tahirikosan.pokemonnft.data.remote.api

import com.tahirikosan.pokemonnft.data.response.pokedex.PokedexByTypeResponse
import com.tahirikosan.pokemonnft.data.response.pokedex.pokemondetail.PokedexPokemonResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface PokedexApi {

    @GET("type/{type}")
    suspend fun getPokedexByType(
        @Path("type") type: Int
    ): PokedexByTypeResponse

    @GET("pokemon/{id}")
    suspend fun getPokemonDetailById(
        @Path("id") id: Int
    ): PokedexPokemonResponse
}