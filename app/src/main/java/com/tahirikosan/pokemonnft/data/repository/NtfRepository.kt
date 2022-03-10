package com.tahirikosan.pokemonnft.data.repository

import com.tahirikosan.pokemonnft.data.remote.Resource
import com.tahirikosan.pokemonnft.data.remote.api.NftApi
import com.tahirikosan.pokemonnft.data.response.mint.MintPokemonResponse
import com.tahirikosan.pokemonnft.data.response.ownerpokemons.GetOwnerPokemonsResponse
import com.tahirikosan.pokemonnft.data.response.wallet.CreateWalletResponse
import javax.inject.Inject

class NtfRepository @Inject constructor(
    private val api: NftApi
): BaseRepository(){

    suspend fun createWallet(): Resource<CreateWalletResponse> = safeApiCall {
        api.createWallet()
    }

    suspend fun getOwnerPokemons(publicKey: String): Resource<GetOwnerPokemonsResponse> = safeApiCall {
        api.getOwnerPokemons(publicKey)
    }

    suspend fun mintPokemon(privateKey: String):Resource<MintPokemonResponse> = safeApiCall {
        api.mintPokemon(privateKey)
    }
}