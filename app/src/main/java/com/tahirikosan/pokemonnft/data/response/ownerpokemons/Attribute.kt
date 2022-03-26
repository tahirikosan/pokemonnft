package com.tahirikosan.pokemonnft.data.response.ownerpokemons

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

// Pokemon attribute
@Parcelize
data class Attribute(
    var trait_type: String?=null,
    var value: Int?=null,
): Parcelable