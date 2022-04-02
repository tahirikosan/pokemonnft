package com.tahirikosan.pokemonnft.ui.shop

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tahirikosan.pokemonnft.data.remote.Resource
import com.tahirikosan.pokemonnft.data.repository.FirestoreRepository
import com.tahirikosan.pokemonnft.data.repository.PokedexRepository
import com.tahirikosan.pokemonnft.data.response.pokedex.PokedexByTypeResponse
import com.tahirikosan.pokemonnft.data.response.pokedex.pokemondetail.PokedexPokemonResponse
import com.tahirikosan.pokemonnft.data.response.user.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShoppingViewModel @Inject constructor(
    private val repository: PokedexRepository,
    private val firestoreRepository: FirestoreRepository
) : ViewModel() {

    private var _pokedexTypeResponse = MutableLiveData<Resource<PokedexByTypeResponse>>()
    val pokedexTypeResponse: LiveData<Resource<PokedexByTypeResponse>> get() = _pokedexTypeResponse

    private var _pokedexPokemonResponse = MutableLiveData<Resource<PokedexPokemonResponse>>()
    val pokedexPokemonResponse: LiveData<Resource<PokedexPokemonResponse>> get() = _pokedexPokemonResponse

    private var _buyPokemonResponse = MutableLiveData<Resource<Boolean>>()
    val buyPokemonResponse: LiveData<Resource<Boolean>> get() = _buyPokemonResponse

    private var _isUserHavePokemon = MutableLiveData<Resource<Boolean>>()
    val isUserHavePokemon: LiveData<Resource<Boolean>> get() = _isUserHavePokemon

    private var _userInfo = MutableLiveData<Resource<User>>()
    val userInfo: LiveData<Resource<User>> get() = _userInfo

    fun getPokedexByType(type: Int) = viewModelScope.launch {
        _pokedexTypeResponse.value = Resource.Loading
        _pokedexTypeResponse.value = repository.getPokedexByType(type)
    }

    fun getPokemonDetailById(id: Int) = viewModelScope.launch {
        _pokedexPokemonResponse.value = Resource.Loading
        _pokedexPokemonResponse.value = repository.getPokemonDetailById(id)
    }

    fun buyPokemon(pokemonPrice: Int, pokemonId: Int) = viewModelScope.launch {
        _buyPokemonResponse.value = Resource.Loading
        _buyPokemonResponse.value = firestoreRepository.buyPokemon(pokemonPrice, pokemonId)
    }

    fun isAlreadyHavePokemon(pokemonId: Int) = viewModelScope.launch {
        _isUserHavePokemon.value = Resource.Loading
        _isUserHavePokemon.value = firestoreRepository.isAlreadyHavePokemon(pokemonId)
    }

    fun getUserInfo() = viewModelScope.launch {
        _userInfo.value = Resource.Loading
        _userInfo.value = firestoreRepository.getUser()
    }
}