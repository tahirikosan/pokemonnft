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
import com.tahirikosan.pokemonnft.utils.Utils.visible

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
        fun bindViews(nftPokemon: NFTPokemon) {
            val pokemonCard = itemView.findViewById<ImageView>(R.id.iv_pokemon_card)
            val pokemonImage = itemView.findViewById<ImageView>(R.id.iv_pokemon_image)
            val tvPokemonName = itemView.findViewById<TextView>(R.id.tv_pokemon_name)
            val hpText = itemView.findViewById<TextView>(R.id.tv_hp)
            val atkText = itemView.findViewById<TextView>(R.id.tv_ap)
            val defText = itemView.findViewById<TextView>(R.id.tv_dp)
            val spText = itemView.findViewById<TextView>(R.id.tv_sp)
            val cardLayout = itemView.findViewById<ConstraintLayout>(R.id.card_layout)
            val ivSelected = itemView.findViewById<ImageView>(R.id.iv_selected)

            tvPokemonName.text = nftPokemon.name
            // Set pokemon image.
            Glide.with(context).load(nftPokemon.image).into(pokemonImage)
            // Set pokemon stats.
            hpText.text = "${nftPokemon.attributes!![0].value}"
            atkText.text = "${nftPokemon.attributes!![1].value}"
            defText.text = "${nftPokemon.attributes!![2].value}"
            spText.text = "${nftPokemon.attributes!![3].value}"


            if (adapterPosition == selectedPokemonPosition) {
                ivSelected.visible(true)
                pokemonCard.setImageResource(R.drawable.pokemon_card_selected)
            } else {
                ivSelected.visible(false)
                pokemonCard.setImageResource(R.drawable.pokemon_card_unselected)
            }


            itemView.setOnClickListener {
                // Select pokemon and deselect old.
                val oldSelectedPokemonPosition = selectedPokemonPosition
                selectedPokemonPosition = adapterPosition
                notifyItemChanged(oldSelectedPokemonPosition)
                notifyItemChanged(selectedPokemonPosition)
                //
                selectNFTPokemon.invoke(nftPokemon)
            }
        }
    }
}
