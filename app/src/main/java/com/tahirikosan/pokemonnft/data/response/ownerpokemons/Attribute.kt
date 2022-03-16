package com.tahirikosan.pokemonnft.data.response.ownerpokemons

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

// Pokemon attribute
@Parcelize
data class Attribute(
    val trait_type: String?=null,
    val value: Int?=null,
): Parcelable