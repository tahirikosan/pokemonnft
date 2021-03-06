package com.tahirikosan.pokemonnft.data.repository

import com.tahirikosan.pokemonnft.data.remote.Resource
import com.tahirikosan.pokemonnft.data.remote.api.FirestoreDatabase
import com.tahirikosan.pokemonnft.data.response.ownerpokemons.NFTPokemon
import com.tahirikosan.pokemonnft.data.response.user.User
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

    suspend fun getUser(): Resource<User> = safeApiCall {
        firestoreDatabase.getUser()
    }

    suspend fun isWalletOccupied(publicKeyStr: String): Resource<Boolean> = safeApiCall {
        firestoreDatabase.isWalletOccupied(publicKeyStr)
    }

    suspend fun addPublicKeyToFirestore(publicKeyStr: String): Resource<Boolean> =
        safeApiCall {
            firestoreDatabase.addPublicKeyToFirestore(publicKeyStr)
        }

    suspend fun removePublicKeyToFirestore(publicKeyStr: String): Resource<Boolean> =
        safeApiCall {
            firestoreDatabase.removePublicKeyToFirestore(publicKeyStr)
        }
}