package com.tahirikosan.pokemonnft.data.remote.api

import com.tahirikosan.pokemonnft.data.response.ownerpokemons.NFTPokemon
import com.tahirikosan.pokemonnft.data.response.user.User

interface FirestoreDatabase {

    suspend fun addUserToFirestore(userId: String): Boolean
    suspend fun buyPokemon(pokemonPrice: Int, pokemonId: Int): Boolean
    suspend fun isAlreadyHavePokemon(pokemonId: Int): Boolean
    suspend fun getPokemonByHash(hash: String): NFTPokemon
    suspend fun getUserPokemonIds(): List<Int>
    suspend fun getUser(): User
    // Check if target wallet public key exist in occupied wallets in firestore.
    suspend fun isWalletOccupied(publicKeyStr: String): Boolean
    // adds public key to firestore occupied wallets array.
    suspend fun addPublicKeyToFirestore(publicKeyStr: String): Boolean
    suspend fun removePublicKeyToFirestore(publicKeyStr: String): Boolean
}