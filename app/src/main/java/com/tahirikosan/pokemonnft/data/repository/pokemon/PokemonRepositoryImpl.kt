package com.tahirikosan.pokemonnft.data.repository.pokemon

import com.tahirikosan.pokemonnft.data.remote.Resource
import com.tahirikosan.pokemonnft.data.remote.api.PokemonApi
import com.tahirikosan.pokemonnft.data.repository.BaseRepository
import com.tahirikosan.pokemonnft.data.response.ownerpokemons.OwnerPokemonsResponse
import com.tahirikosan.pokemonnft.model.PokemonModel
import javax.inject.Inject

class PokemonRepositoryImpl @Inject constructor(
    private val pokemonApi: PokemonApi
): BaseRepository(), PokemonRepository {

    override suspend fun getOwnerPokemonsByOffset(userId:String,offset: Int): Resource<OwnerPokemonsResponse> = safeApiCall {
        pokemonApi.getOwnerPokemonsByOffset(userId,offset)
    }
}