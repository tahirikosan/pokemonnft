package com.tahirikosan.pokemonnft.data.response.fight

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlayerPokemon(
    var hp: Int?=null,
    var ap: Int?=null,
    var dp: Int?=null,
    var sp: Int?=null,
    var imageUrl: String?=null,
): Parcelable
