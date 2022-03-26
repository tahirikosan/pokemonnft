package com.tahirikosan.pokemonnft.ui.adapter.pokemon

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tahirikosan.pokemonnft.R
import com.tahirikosan.pokemonnft.data.response.ownerpokemons.NFTPokemon

class NFTPokemonAdapter(
    private val NFTPokemons: List<NFTPokemon>,
    val selectNFTPokemon: (NFTPokemon) -> Unit
) :
    RecyclerView.Adapter<NFTPokemonAdapter.PokemonViewHolder>() {

    private var selectedPokemonPosition: Int = -1

    private lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder {
        context = parent.context
        return PokemonViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_pokemon_card, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        holder.bindViews(NFTPokemons[position])
    }

    override fun getItemCount(): Int = NFTPokemons.size

    fun removeSelection() {
        val tmpPosition = selectedPokemonPosition
        selectedPokemonPosition = -1
        notifyItemChanged(tmpPosition)
    }

    inner class PokemonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindViews(NFTPokemon: NFTPokemon) {
            val pokemonCard = itemView.findViewById<ImageView>(R.id.pokemonCard)
            val pokemonImage = itemView.findViewById<ImageView>(R.id.pokemonImage)
            val hpText = itemView.findViewById<TextView>(R.id.hpText)
            val atkText = itemView.findViewById<TextView>(R.id.atkText)
            val defText = itemView.findViewById<TextView>(R.id.defText)
            val spText = itemView.findViewById<TextView>(R.id.spdText)
            val cardLayout = itemView.findViewById<ConstraintLayout>(R.id.card_layout)

            // Set pokemon image.
            Glide.with(context).load(NFTPokemon.image).into(pokemonImage)
            // Set pokemon stats.
            hpText.text = "HP: ${NFTPokemon.attributes!![0].value}"
            atkText.text = "AP: ${NFTPokemon.attributes!![1].value}"
            defText.text = "DP: ${NFTPokemon.attributes!![2].value}"
            spText.text = "SP: ${NFTPokemon.attributes!![3].value}"


            if (adapterPosition == selectedPokemonPosition) {
                cardLayout.setBackgroundColor(context.getColor(R.color.red))
            } else {
                cardLayout.setBackgroundColor(context.getColor(R.color.teal_200))
            }


            itemView.setOnClickListener {
                // Select pokemon and deselect old.
                val oldSelectedPokemonPosition = selectedPokemonPosition
                selectedPokemonPosition = adapterPosition
                notifyItemChanged(oldSelectedPokemonPosition)
                notifyItemChanged(selectedPokemonPosition)
                //
                selectNFTPokemon.invoke(NFTPokemon)
            }
        }
    }
}
