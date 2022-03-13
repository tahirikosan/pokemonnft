package com.tahirikosan.pokemonnft.data.response.wallet

data class WalletResponse(
    val address: String,
    val mnemonic: Mnemonic,
    val privateKey: String
)