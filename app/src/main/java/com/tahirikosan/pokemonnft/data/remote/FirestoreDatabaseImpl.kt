package com.tahirikosan.pokemonnft.data.remote

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.tahirikosan.pokemonnft.data.remote.api.FirestoreDatabase
import com.tahirikosan.pokemonnft.data.response.ownerpokemons.Pokemon
import com.tahirikosan.pokemonnft.data.response.ownerpokemons.Pokemon.Companion.toPokemon
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import javax.inject.Inject

class FirestoreDatabaseImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : FirestoreDatabase {

    override suspend fun getPokemonByHash(hash: String): Pokemon {
        lateinit var pokemon: Pokemon
        return try {
            firestore
                .collection("metadata")
                .whereEqualTo("hash", hash)
                .limit(1)
                .get()
                .await()
                .documents
                .mapNotNull { it.toPokemon() }.first()
        } catch (e: Exception) {
            Log.w("main", "Error getting documents.", e)
            e.printStackTrace()
            throw  e
        }


    }
}