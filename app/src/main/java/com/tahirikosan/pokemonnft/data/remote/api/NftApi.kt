package com.tahirikosan.pokemonnft.data.remote.api

import com.tahirikosan.pokemonnft.data.response.mint.MintPokemonResponse
import com.tahirikosan.pokemonnft.data.response.ownerpokemons.GetOwnerPokemonsResponse
import com.tahirikosan.pokemonnft.data.response.wallet.CreateWalletResponse
import retrofit2.http.*

interface NftApi {
    @FormUrlEncoded
    @GET("/create-wallet")
    suspend fun createWallet(): CreateWalletResponse

    @FormUrlEncoded
    @POST("/get-owner-pokemons")
    suspend fun getOwnerPokemons(
        @Field("publicKey") publicKey: String
    ): GetOwnerPokemonsResponse

    @FormUrlEncoded
    @POST("/mint-pokemon")
    suspend fun mintPokemon(
        @Field("privateKey") privateKey: String
    ): MintPokemonResponse
}