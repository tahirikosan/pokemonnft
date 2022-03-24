package com.tahirikosan.pokemonnft.ui.mint

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tahirikosan.pokemonnft.data.remote.Resource
import com.tahirikosan.pokemonnft.data.repository.FirestoreRepository
import com.tahirikosan.pokemonnft.data.repository.NFTRepository
import com.tahirikosan.pokemonnft.data.response.mint.MintPokemonResponse
import com.tahirikosan.pokemonnft.data.response.ownerpokemons.NFTPokemon
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MintViewModel @Inject constructor(
    private val repository: NFTRepository,
    private val repositoryFireStore: FirestoreRepository,
) : ViewModel() {

    private var _mintPokemonResponse = MutableLiveData<Resource<MintPokemonResponse>>()
    val mintPokemonResponse: LiveData<Resource<MintPokemonResponse>> get() = _mintPokemonResponse

    private var _mintedPokemon = MutableLiveData<Resource<NFTPokemon>>()
    val mintedNFTPokemon: LiveData<Resource<NFTPokemon>> get() = _mintedPokemon


    fun mintPokemon(privateKey: String) = viewModelScope.launch {
        _mintPokemonResponse.value = Resource.Loading
        _mintPokemonResponse.value = repository.mintPokemon(privateKey)
    }

    fun getMintedPokemon(hash: String) = viewModelScope.launch {
        _mintedPokemon.value = Resource.Loading
        _mintedPokemon.value = repositoryFireStore.getPokemonByHash(hash)
    }
}