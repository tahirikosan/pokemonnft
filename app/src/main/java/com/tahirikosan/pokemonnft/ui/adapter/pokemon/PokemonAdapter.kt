package com.tahirikosan.pokemonnft.ui.adapter.pokemon

import android.annotation.SuppressLint
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
import com.tahirikosan.pokemonnft.model.PokemonModel
import com.tahirikosan.pokemonnft.utils.Utils.visible

class PokemonAdapter(
    private val pokemonModels: ArrayList<PokemonModel>,
    val selectedPokemon: (PokemonModel) -> Unit
) :
    RecyclerView.Adapter<PokemonAdapter.PokemonViewHolder>() {

    private var selectedPokemonPosition: Int = -1
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

    fun removeSelection() {
        val tmpPosition = selectedPokemonPosition
        selectedPokemonPosition = -1
        notifyItemChanged(tmpPosition)
    }

    inner class PokemonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun bindViews(pokemonModel: PokemonModel) {
            val pokemonCard = itemView.findViewById<ImageView>(R.id.iv_pokemon_card)
            val pokemonImage = itemView.findViewById<ImageView>(R.id.iv_pokemon_image)
            val tvPokemonName = itemView.findViewById<TextView>(R.id.tv_pokemon_name)
            val hpText = itemView.findViewById<TextView>(R.id.tv_hp)
            val atkText = itemView.findViewById<TextView>(R.id.tv_ap)
            val defText = itemView.findViewById<TextView>(R.id.tv_dp)
            val spText = itemView.findViewById<TextView>(R.id.tv_sp)
            val cardLayout = itemView.findViewById<ConstraintLayout>(R.id.card_layout)
            val ivSelected = itemView.findViewById<ImageView>(R.id.iv_selected)


            tvPokemonName.text = pokemonModel.name
            // Set pokemon image.
            Glide.with(context).load(pokemonModel.image).into(pokemonImage)
            // Set pokemon stats.
            hpText.text = "${pokemonModel.attributes!![0].value}"
            atkText.text = "${pokemonModel.attributes!![1].value}"
            defText.text = "${pokemonModel.attributes!![2].value}"
            spText.text = "${pokemonModel.attributes!![3].value}"

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
                selectedPokemon.invoke(pokemonModel)
            }
        }
    }
}
