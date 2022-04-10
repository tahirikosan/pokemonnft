package com.tahirikosan.pokemonnft.data.response.wallet

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Mnemonic(
    val locale: String,
    val path: String,
    val phrase: String
): Parcelable