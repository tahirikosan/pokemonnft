package com.tahirikosan.pokemonnft.data.remote.api

import com.tahirikosan.pokemonnft.data.response.ownerpokemons.OwnerPokemonsResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface PokemonApi {
    @GET("/get-owner-normal-pokemons/{userId}/{offset}")
    suspend fun getOwnerPokemonsByOffset(
        @Path("userId") userId: String,
        @Path("offset") offset: Int,
    ): OwnerPokemonsResponse
}