package com.tahirikosan.pokemonnft.data.response.fight

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Room(
    var roomId: String? = null,
    var round: Int? = null,
    var turn: String? = null,
    var playerOne: String?=null,
    var playerTwo: String?=null,
    var players: ArrayList<String>? = null,
    var health: Map<String,Int>?=null
) : Parcelable