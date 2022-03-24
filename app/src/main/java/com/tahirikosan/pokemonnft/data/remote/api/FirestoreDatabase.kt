package com.tahirikosan.pokemonnft.data.remote.api

import com.tahirikosan.pokemonnft.data.response.ownerpokemons.NFTPokemon

interface FirestoreDatabase {

    suspend fun addUserToFirestore(userId: String): Boolean
    suspend fun getPokemonByHash(hash: String): NFTPokemon
}