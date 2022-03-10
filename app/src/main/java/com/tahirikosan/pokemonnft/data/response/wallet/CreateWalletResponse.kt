package com.tahirikosan.pokemonnft.data.response.wallet

data class CreateWalletResponse(
    val address: String,
    val mnemonic: Mnemonic,
    val privateKey: String
)