package com.tahirikosan.pokemonnft.data.remote.api

import com.tahirikosan.pokemonnft.data.response.ownerpokemons.Pokemon

interface FirestoreDatabase {

    suspend fun getPokemonByHash(hash: String): Pokemon
}