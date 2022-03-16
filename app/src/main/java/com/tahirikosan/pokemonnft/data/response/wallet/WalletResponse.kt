package com.tahirikosan.pokemonnft.data.response.wallet

data class WalletResponse(
    val mnemonic: Mnemonic,
    val privateKey: String,
    val publicKey: String
)