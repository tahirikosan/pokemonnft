package com.tahirikosan.pokemonnft.data.response.pokedex.pokemondetail

import com.tahirikosan.pokemonnft.data.response.ownerpokemons.Attribute
import com.tahirikosan.pokemonnft.data.response.ownerpokemons.NFTPokemon.Companion.toPokemonModel
import com.tahirikosan.pokemonnft.enum.PokemonStatEnum
import com.tahirikosan.pokemonnft.model.PokemonModel
import com.tahirikosan.pokemonnft.utils.Utils

data class PokedexPokemonResponse(
    val id: Int,
    val name: String,
    val stats: List<Stat>,
    val types: List<Type>,
) {
    companion object {

        fun PokedexPokemonResponse.toPokemonModel(): PokemonModel {
            val attributes = ArrayList<Attribute>()
            stats.forEach {
                attributes.add(
                    Attribute(
                        trait_type = it.stat.name,
                        value = it.base_stat
                    )
                )
            }
            return PokemonModel(
                id = this.id,
                attributes = attributes,
                image = Utils.pokemonImageUrlGenerateById(this.id),
                name = this.name,
                isNft = false
            )
        }

        private const val TAG = "NORMAL_POKEMON"
    }
}
