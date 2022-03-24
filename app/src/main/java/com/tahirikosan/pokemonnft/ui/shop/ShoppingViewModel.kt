package com.tahirikosan.pokemonnft.ui.shop

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tahirikosan.pokemonnft.data.remote.Resource
import com.tahirikosan.pokemonnft.data.repository.PokedexRepository
import com.tahirikosan.pokemonnft.data.response.pokedex.PokedexByTypeResponse
import com.tahirikosan.pokemonnft.data.response.pokedex.pokemondetail.PokedexPokemonResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShoppingViewModel @Inject constructor(
    private val repository: PokedexRepository
): ViewModel() {

    private var _pokedexTypeResponse = MutableLiveData<Resource<PokedexByTypeResponse>>()
    val pokedexTypeResponse: LiveData<Resource<PokedexByTypeResponse>> get() = _pokedexTypeResponse

    private var _pokedexPokemonResponse = MutableLiveData<Resource<PokedexPokemonResponse>>()
    val pokedexPokemonResponse: LiveData<Resource<PokedexPokemonResponse>> get() = _pokedexPokemonResponse

    fun getPokedexByType(type: Int) = viewModelScope.launch {
        _pokedexTypeResponse.value = Resource.Loading
        _pokedexTypeResponse.value = repository.getPokedexByType(type)
    }

    fun getPokemonDetailById(id: Int) = viewModelScope.launch {
        _pokedexPokemonResponse.value = Resource.Loading
        _pokedexPokemonResponse.value = repository.getPokemonDetailById(id)
    }

}