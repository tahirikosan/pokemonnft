package com.tahirikosan.pokemonnft.ui.menu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tahirikosan.pokemonnft.data.remote.Resource
import com.tahirikosan.pokemonnft.data.repository.FirestoreRepository
import com.tahirikosan.pokemonnft.data.repository.NFTRepository
import com.tahirikosan.pokemonnft.data.repository.PokedexRepository
import com.tahirikosan.pokemonnft.data.response.ownerpokemons.GetOwnerNFTPokemonsResponse
import com.tahirikosan.pokemonnft.data.response.pokedex.pokemondetail.PokedexPokemonResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameMenuViewModel @Inject constructor(
    private val repository: NFTRepository,
    private val firestoreRepository: FirestoreRepository,
    private val pokedexRepository: PokedexRepository
) : ViewModel() {

    // These are nft pokemons.Comes from my service.
    private var _ownerPokemonsResponse = MutableLiveData<Resource<GetOwnerNFTPokemonsResponse>>()
    val ownerNFTPokemonsResponse: LiveData<Resource<GetOwnerNFTPokemonsResponse>> get() = _ownerPokemonsResponse

    private var _userPokemonIds = MutableLiveData<Resource<List<Int>>?>()
    val userPokemonIds: LiveData<Resource<List<Int>>?> get() = _userPokemonIds

    // Pokemon detail from pokedex api.
    private var _pokedexPokemonResponse = MutableLiveData<Resource<PokedexPokemonResponse>>()
    val pokedexPokemonResponse: LiveData<Resource<PokedexPokemonResponse>> get() = _pokedexPokemonResponse


    fun getOwnerNFTPokemons(publicKey: String) = viewModelScope.launch {
        _ownerPokemonsResponse.value = Resource.Loading
        _ownerPokemonsResponse.value = repository.getOwnerNFTPokemons(publicKey)
    }

    fun getUserPokemonIds() = viewModelScope.launch {
        _userPokemonIds.value = Resource.Loading
        _userPokemonIds.value = firestoreRepository.getUserPokemonIds()
        // To prevent get multiple pokemons.
        _userPokemonIds.value = null
    }

    fun getPokemonsByIds(pokemonIds: List<Int>) = viewModelScope.launch {
        pokemonIds.forEach {
            _pokedexPokemonResponse.value = pokedexRepository.getPokemonDetailById(it)
        }
    }

}