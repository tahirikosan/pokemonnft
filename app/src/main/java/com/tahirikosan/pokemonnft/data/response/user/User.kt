package com.tahirikosan.pokemonnft.data.response.user

import android.os.Parcelable
import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    var coin: Int? = null,
    var pvp: Int? = null,
    var userId: String? = null,
    var pokemons: List<Int>?=null,
) : Parcelable {
    companion object {
        fun DocumentSnapshot.toUser(): User? {
            return try {
                this.toObject(User::class.java)
            } catch (e: Exception) {
                Log.e(TAG, "Error converting user profile", e)
                null
            }
        }

        private const val TAG = "USER"
    }
}
