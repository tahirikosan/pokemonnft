package com.tahirikosan.pokemonnft.data.repository

import com.tahirikosan.pokemonnft.data.remote.Resource
import com.tahirikosan.pokemonnft.data.remote.api.FirestoreDatabase
import com.tahirikosan.pokemonnft.data.response.ownerpokemons.Pokemon
import javax.inject.Inject

class FirestoreRepository @Inject constructor(
    private val firestoreDatabase: FirestoreDatabase
) : BaseRepository() {

    suspend fun getPokemonByHash(hash: String): Resource<Pokemon> = safeApiCall {
        firestoreDatabase.getPokemonByHash(hash)
    }
}