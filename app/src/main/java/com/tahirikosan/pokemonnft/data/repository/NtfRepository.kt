package com.tahirikosan.pokemonnft.data.repository

import com.tahirikosan.pokemonnft.data.remote.Resource
import com.tahirikosan.pokemonnft.data.remote.api.NftApi
import com.tahirikosan.pokemonnft.data.response.mint.MintPokemonResponse
import com.tahirikosan.pokemonnft.data.response.ownerpokemons.GetOwnerPokemonsResponse
import com.tahirikosan.pokemonnft.data.response.wallet.WalletResponse
import javax.inject.Inject

class NtfRepository @Inject constructor(
    private val api: NftApi
): BaseRepository(){

    suspend fun connectWallet(mnemonic: String): Resource<WalletResponse> = safeApiCall {
        api.connectWallet(mnemonic)
    }

    suspend fun createWallet(): Resource<WalletResponse> = safeApiCall {
        api.createWallet()
    }

    suspend fun getOwnerPokemons(publicKey: String): Resource<GetOwnerPokemonsResponse> = safeApiCall {
        api.getOwnerPokemons(publicKey)
    }

    suspend fun mintPokemon(privateKey: String):Resource<MintPokemonResponse> = safeApiCall {
        api.mintPokemon(privateKey)
    }
}