package com.tahirikosan.pokemonnft.data.remote

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.tahirikosan.pokemonnft.data.remote.api.FirestoreDatabase
import com.tahirikosan.pokemonnft.data.response.ownerpokemons.NFTPokemon
import com.tahirikosan.pokemonnft.data.response.ownerpokemons.NFTPokemon.Companion.toPokemon
import com.tahirikosan.pokemonnft.data.response.user.User
import com.tahirikosan.pokemonnft.data.response.user.User.Companion.toUser
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import javax.inject.Inject

class FirestoreDatabaseImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : FirestoreDatabase {

    override suspend fun getUser(): User {
        return try {
            firestore.collection("users")
                .document(FirebaseAuth.getInstance().currentUser?.uid!!)
                .get()
                .await().toUser()!!
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    override suspend fun getPokemonByHash(hash: String): NFTPokemon {
        lateinit var NFTPokemon: NFTPokemon
        return try {
            firestore
                .collection("metadata")
                .whereEqualTo("hash", hash)
                .limit(1)
                .get()
                .await()
                .documents
                .mapNotNull {
                    it.toPokemon()
                }.first()
        } catch (e: Exception) {
            Log.w("main", "Error getting documents.", e)
            e.printStackTrace()
            throw  e
        }
    }

    override suspend fun buyPokemon(pokemonPrice: Int, pokemonId: Int): Boolean {
        try {
            val minusPokemonPrice = -pokemonPrice
            // Get user coin amount.
            val user = firestore.collection("users")
                .document(FirebaseAuth.getInstance().currentUser?.uid!!)
                .get()
                .await().toUser()
            // Check if user have enough coin to buy the pokemon.
            if (user!!.coin!! >= pokemonPrice) {
                firestore.collection("users")
                    .document(FirebaseAuth.getInstance().currentUser?.uid!!)
                    .update(
                        "coin", FieldValue.increment(minusPokemonPrice.toDouble()),
                        "pokemons", FieldValue.arrayUnion(pokemonId)
                    )
                    .await()
            } else {
                return false
            }

            return true
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    override suspend fun isAlreadyHavePokemon(pokemonId: Int): Boolean {
        try {
            val user = firestore
                .collection("users")
                .document(FirebaseAuth.getInstance().currentUser?.uid!!)
                .get()
                .await().toUser()
            if (user!!.pokemons.isNullOrEmpty()) {
                return false
            }
            return user.pokemons!!.contains(pokemonId)
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    override suspend fun getUserPokemonIds(): List<Int> {
        return try {
            firestore.collection("users")
                .document(FirebaseAuth.getInstance().currentUser?.uid!!)
                .get()
                .await().toUser()!!.pokemons!!
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    override suspend fun addUserToFirestore(userId: String): Boolean {
        val user = hashMapOf(
            "userId" to userId,
            "coin" to 0,
            "pvp" to 0,
            "pokemons" to arrayListOf<Int>()
        )
        return try {
            firestore
                .collection("users")
                .document(userId)
                .set(user)
                .await()
            true
        } catch (e: Exception) {
            throw e
        }
    }
}