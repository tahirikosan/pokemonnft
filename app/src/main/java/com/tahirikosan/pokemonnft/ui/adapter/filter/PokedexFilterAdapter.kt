package com.tahirikosan.pokemonnft.ui.adapter.filter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tahirikosan.pokemonnft.R
import com.tahirikosan.pokemonnft.databinding.ItemPokedexFilterBinding

class PokedexFilterAdapter(val onFilterClickListener: (Int) -> Unit) :
    RecyclerView.Adapter<PokedexFilterAdapter.FilterViewHolder>() {
    private lateinit var binding: ItemPokedexFilterBinding
    private var selectedPosition = 0
    private lateinit var context: Context
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
        val tvPokemonType = binding.tvPokemonType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        binding = ItemPokedexFilterBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        context = parent.context
        return FilterViewHolder(
            binding
        )
    }

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        val filter = types[position]
        holder.tvPokemonType.text = filter

        holder.itemView.setOnClickListener {
            // Change selected pokemon position.
            notifyItemChanged(selectedPosition)
            selectedPosition = position
            notifyItemChanged(position)

            onFilterClickListener.invoke(position + 1)
        }

        // Handle selection style.
        if(selectedPosition == position){
            holder.tvPokemonType.setBackgroundResource(R.drawable.bg_filter_selected)
            holder.tvPokemonType.setTextColor(context.getColor(R.color.primaryColor))
        }else{
            holder.tvPokemonType.setBackgroundResource(R.drawable.bg_filter_unselected)
            holder.tvPokemonType.setTextColor(context.getColor(R.color.componentColor))
        }
    }

    override fun getItemCount(): Int = types.size


}