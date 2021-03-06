package com.tahirikosan.pokemonnft.data.remote.api

import com.tahirikosan.pokemonnft.data.response.mint.MintPokemonResponse
import com.tahirikosan.pokemonnft.data.response.ownerpokemons.GetOwnerNFTPokemonsResponse
import com.tahirikosan.pokemonnft.data.response.wallet.WalletResponse
import retrofit2.http.*

interface NftApi {
    @GET("/create-wallet")
    suspend fun createWallet(): WalletResponse

    @FormUrlEncoded
    @POST("/connect-wallet")
    suspend fun connectWallet(
        @Field("mnemonic") mnemonic: String
    ): WalletResponse

    @FormUrlEncoded
    @POST("/get-owner-pokemons")
    suspend fun getOwnerNFTPokemons(
        @Field("publicKey") publicKey: String
    ): GetOwnerNFTPokemonsResponse

    @FormUrlEncoded
    @POST("/mint-pokemon")
    suspend fun mintPokemon(
        @Field("privateKey") privateKey: String,
        @Field("userId") userId: String
    ): MintPokemonResponse
}