package com.tahirikosan.pokemonnft.ui.adapter.pokemon

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tahirikosan.pokemonnft.R
import com.tahirikosan.pokemonnft.model.PokemonModel

class PokemonAdapter(
    private val pokemonModels: ArrayList<PokemonModel>,
    val selectedPokemon: (PokemonModel) -> Unit
) :
    RecyclerView.Adapter<PokemonAdapter.PokemonViewHolder>() {

    private lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder {
        context = parent.context
        return PokemonViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_pokemon_card, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        holder.bindViews(pokemonModels[position])
    }

    override fun getItemCount(): Int = pokemonModels.size

    fun addPokemon(pokemonModel: PokemonModel) {
        pokemonModels.add(pokemonModel)
        notifyItemInserted(pokemonModels.size - 1)
    }

    fun clear() {
        pokemonModels.clear()
        notifyDataSetChanged()
    }

    inner class PokemonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun bindViews(pokemonModel: PokemonModel) {
            val pokemonCard = itemView.findViewById<ImageView>(R.id.pokemonCard)
            val pokemonImage = itemView.findViewById<ImageView>(R.id.pokemonImage)
            val hpText = itemView.findViewById<TextView>(R.id.hpText)
            val atkText = itemView.findViewById<TextView>(R.id.atkText)
            val defText = itemView.findViewById<TextView>(R.id.defText)
            val spText = itemView.findViewById<TextView>(R.id.spdText)

            // Set pokemon image.
            Glide.with(context).load(pokemonModel.image).into(pokemonImage)
            // Set pokemon stats.
            hpText.text = "HP: ${pokemonModel.attributes!![0].value}"
            atkText.text = "AP: ${pokemonModel.attributes!![1].value}"
            defText.text = "DP: ${pokemonModel.attributes!![2].value}"
            spText.text = "SP: ${pokemonModel.attributes!![3].value}"

            itemView.setOnClickListener {
                selectedPokemon.invoke(pokemonModel)
            }
        }
    }
}
