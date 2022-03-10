package com.tahirikosan.pokemonnft.data.response.mint

data class Pokemon(
    val chainId: Int,
    val confirmations: Int,
    val `data`: String,
    val from: String,
    val gasLimit: GasLimit,
    val gasPrice: GasPrice,
    val hash: String,
    val nonce: Int,
    val r: String,
    val s: String,
    val to: String,
    val type: Any,
    val v: Int,
    val value: Value
)