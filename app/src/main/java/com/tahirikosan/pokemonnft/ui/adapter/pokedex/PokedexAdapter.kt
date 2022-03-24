package com.tahirikosan.pokemonnft.ui.adapter.pokedex

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tahirikosan.pokemonnft.databinding.ItemPokedexBinding

class PokedexAdapter(val pokedex: ArrayList<PokedexItemUIModel>, val pokemonClickListener: (Int)->Unit) :
    RecyclerView.Adapter<PokedexAdapter.PokedexViewHolder>() {

    private lateinit var binding: ItemPokedexBinding
    private lateinit var context: Context

    inner class PokedexViewHolder(binding: ItemPokedexBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val pokemonImage = binding.pokemonImage
        val pokemonName = binding.pokemonName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokedexViewHolder {
        binding = ItemPokedexBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        context = parent.context
        return PokedexViewHolder(
            binding
        )
    }

    override fun onBindViewHolder(holder: PokedexViewHolder, position: Int) {
        val pokemon = pokedex[position]
        holder.pokemonName.text = pokemon.name
        Glide.with(context).load(pokemon.imageUrl).into(holder.pokemonImage)
        holder.itemView.setOnClickListener {
            pokemonClickListener.invoke(position+1)
        }
    }

    override fun getItemCount(): Int = pokedex.size
}