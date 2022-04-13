package com.tahirikosan.pokemonnft.data.repository.pokemon

import com.tahirikosan.pokemonnft.data.remote.Resource
import com.tahirikosan.pokemonnft.data.response.ownerpokemons.OwnerPokemonsResponse

interface  PokemonRepository{
    suspend fun getOwnerPokemonsByOffset(userId: String,offset: Int): Resource<OwnerPokemonsResponse>
}