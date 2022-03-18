package com.tahirikosan.pokemonnft.data.response.fight

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Room(
    var roomId: String? = null,
    var round: Int? = null,
    var turn: String? = null,
    var users: ArrayList<String>? = null,
) : Parcelable