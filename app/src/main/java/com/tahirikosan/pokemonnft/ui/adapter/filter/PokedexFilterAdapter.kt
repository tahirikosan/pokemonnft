package com.tahirikosan.pokemonnft.ui.adapter.filter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tahirikosan.pokemonnft.databinding.ItemPokedexFilterBinding

class PokedexFilterAdapter(val onFilterClickListener: (Int) -> Unit) :
    RecyclerView.Adapter<PokedexFilterAdapter.FilterViewHolder>() {
    private lateinit var binding: ItemPokedexFilterBinding
    private val types = listOf<String>(
        "normal",
        "fighting",
        "flying",
        "poison",
        "ground",
        "rock",
        "bug",
        "ghost",
        "steel",
        "fire",
        "water",
        "grass",
        "electric",
        "psychic",
        "ice",
        "dragon",
        "dark",
        "fairy",
    )


    inner class FilterViewHolder(binding: ItemPokedexFilterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val pokemonType = binding.pokemonType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        binding = ItemPokedexFilterBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FilterViewHolder(
            binding
        )
    }

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        val filter = types[position]
        holder.pokemonType.text = filter

        holder.itemView.setOnClickListener {
            onFilterClickListener.invoke(position + 1)
        }
    }

    override fun getItemCount(): Int = types.size


}