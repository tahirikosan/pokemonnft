package com.tahirikosan.pokemonnft.data.response.ownerpokemons

import android.os.Parcelable
import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.parcelize.Parcelize

@Parcelize
data class NFTPokemon(
    val attributes: List<Attribute>?=null,
    val description: String?=null,
    val dna: String?=null,
    val docId: String?=null,
    val hash: String?=null,
    val id: Int?=null,
    val image: String?=null,
    val name: String?=null,
    val rarity: String?=null,
    val tokenId: String?=null,
) : Parcelable {
    companion object {
        fun DocumentSnapshot.toPokemon(): NFTPokemon? {
            return try {
                this.toObject(NFTPokemon::class.java)
            } catch (e: Exception) {
                Log.e(TAG, "Error converting user profile", e)
                /* FirebaseCrashlytics.getInstance().log("Error converting user profile")
                        FirebaseCrashlytics.getInstance().setCustomKey("userId", id)
                        FirebaseCrashlytics.getInstance().recordException(e)*/
                null
            }
        }

        private const val TAG = "POKEMON"
    }
}