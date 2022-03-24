package com.tahirikosan.pokemonnft.ui.adapter.pokemon

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tahirikosan.pokemonnft.R
import com.tahirikosan.pokemonnft.data.response.ownerpokemons.NFTPokemon

class PokemonAdapter(private val NFTPokemons: List<NFTPokemon>) :
    RecyclerView.Adapter<PokemonAdapter.PokemonViewHolder>() {

    private lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder {
        context = parent.context
       return  PokemonViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_pokemon_card, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        holder.bindViews(NFTPokemons[position])
    }

    override fun getItemCount(): Int = NFTPokemons.size

    inner class PokemonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindViews(NFTPokemon: NFTPokemon) {
            val pokemonCard = itemView.findViewById<ImageView>(R.id.pokemonCard)
            val pokemonImage = itemView.findViewById<ImageView>(R.id.pokemonImage)
            val hpText = itemView.findViewById<TextView>(R.id.hpText)
            val atkText = itemView.findViewById<TextView>(R.id.atkText)
            val defText = itemView.findViewById<TextView>(R.id.defText)
            val spText = itemView.findViewById<TextView>(R.id.spdText)

            // Set pokemon image.
            Glide.with(context).load(NFTPokemon.image).into(pokemonImage)
            // Set pokemon stats.
            hpText.text = "HP: ${NFTPokemon.attributes!![0].value}"
            atkText.text = "AP: ${NFTPokemon.attributes!![1].value}"
            defText.text = "DP: ${NFTPokemon.attributes!![2].value}"
            spText.text = "SP: ${NFTPokemon.attributes!![3].value}"
        }
    }
}
