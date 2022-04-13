package com.tahirikosan.pokemonnft.ui.menu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.tahirikosan.pokemonnft.data.remote.Resource
import com.tahirikosan.pokemonnft.data.repository.AuthRepository
import com.tahirikosan.pokemonnft.data.repository.FirestoreRepository
import com.tahirikosan.pokemonnft.data.repository.NFTRepository
import com.tahirikosan.pokemonnft.data.repository.pokemon.PokemonRepository
import com.tahirikosan.pokemonnft.data.response.ownerpokemons.GetOwnerNFTPokemonsResponse
import com.tahirikosan.pokemonnft.data.response.ownerpokemons.OwnerPokemonsResponse
import com.tahirikosan.pokemonnft.model.PokemonModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameMenuViewModel @Inject constructor(
    private val repository: NFTRepository,
    private val firestoreRepository: FirestoreRepository,
    private val pokemonRepository: PokemonRepository,
    private val authRepository: AuthRepository
) : ViewModel() {


    private val _firebaseUser = MutableLiveData<Resource<FirebaseUser?>>()
    val currentUser get() = _firebaseUser

    // These are nft pokemons.Comes from my service.
    private var _ownerNftPokemonsResponse = MutableLiveData<Resource<GetOwnerNFTPokemonsResponse>>()
    val ownerNFTPokemonsResponse: LiveData<Resource<GetOwnerNFTPokemonsResponse>> get() = _ownerNftPokemonsResponse

    private var _userPokemonIds = MutableLiveData<Resource<List<Int>>?>()
    val userPokemonIds: LiveData<Resource<List<Int>>?> get() = _userPokemonIds

    // Pokemon detail from pokedex api.
    private var _ownerPokemonsResponse = MutableLiveData<Resource<OwnerPokemonsResponse>>()
    val ownerPokemonsResponse: LiveData<Resource<OwnerPokemonsResponse>> get() = _ownerPokemonsResponse


    fun getOwnerNFTPokemons(publicKey: String) = viewModelScope.launch {
        _ownerNftPokemonsResponse.value = Resource.Loading
        _ownerNftPokemonsResponse.value = repository.getOwnerNFTPokemons(publicKey)
    }

    fun getOwnerPokemonsByOffset(userId: String, offset: Int) = viewModelScope.launch {
        _ownerPokemonsResponse.value = Resource.Loading
        _ownerPokemonsResponse.value = pokemonRepository.getOwnerPokemonsByOffset(userId, offset)
    }

    fun getCurrentUser() = viewModelScope.launch {
        _firebaseUser.value = Resource.Loading
        _firebaseUser.value = authRepository.getCurrentUser()
    }
}