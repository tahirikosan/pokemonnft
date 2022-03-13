package com.tahirikosan.pokemonnft.ui.menu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tahirikosan.pokemonnft.data.remote.Resource
import com.tahirikosan.pokemonnft.data.repository.NtfRepository
import com.tahirikosan.pokemonnft.data.response.mint.MintPokemonResponse
import com.tahirikosan.pokemonnft.data.response.ownerpokemons.GetOwnerPokemonsResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameMenuViewModel @Inject constructor(
    private val repository: NtfRepository
): ViewModel() {

    private var _ownerPokemonsResponse = MutableLiveData<Resource<GetOwnerPokemonsResponse>>()
    val ownerPokemonsResponse: LiveData<Resource<GetOwnerPokemonsResponse>> get() = _ownerPokemonsResponse

    private var _mintPokemonResponse = MutableLiveData<Resource<MintPokemonResponse>>()
    val mintPokemonResponse: LiveData<Resource<MintPokemonResponse>> get() = _mintPokemonResponse


    fun getOwnerPokemons(publicKey: String) = viewModelScope.launch {
        _ownerPokemonsResponse.value = Resource.Loading
        _ownerPokemonsResponse.value = repository.getOwnerPokemons(publicKey)
    }

    fun mintPokemon(privateKey: String) = viewModelScope.launch {
        _mintPokemonResponse.value = Resource.Loading
        _mintPokemonResponse.value = repository.mintPokemon(privateKey)
    }
}