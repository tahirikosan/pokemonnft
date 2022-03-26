package com.tahirikosan.pokemonnft.data.repository

import com.tahirikosan.pokemonnft.data.remote.Resource
import com.tahirikosan.pokemonnft.data.remote.api.FirestoreDatabase
import com.tahirikosan.pokemonnft.data.response.ownerpokemons.NFTPokemon
import javax.inject.Inject

class FirestoreRepository @Inject constructor(
    private val firestoreDatabase: FirestoreDatabase
) : BaseRepository() {

    suspend fun getPokemonByHash(hash: String): Resource<NFTPokemon> = safeApiCall {
        firestoreDatabase.getPokemonByHash(hash)
    }

    suspend fun addUserToFirestore(userId: String): Resource<Boolean> = safeApiCall {
        firestoreDatabase.addUserToFirestore(userId)
    }

    suspend fun buyPokemon(pokemonPrice: Int, pokemonId: Int): Resource<Boolean> = safeApiCall {
        firestoreDatabase.buyPokemon(pokemonPrice, pokemonId)
    }

    suspend fun isAlreadyHavePokemon(pokemonId: Int): Resource<Boolean> = safeApiCall {
        firestoreDatabase.isAlreadyHavePokemon(pokemonId)
    }

    suspend fun getUserPokemonIds(): Resource<List<Int>> = safeApiCall {
        firestoreDatabase.getUserPokemonIds()
    }
}