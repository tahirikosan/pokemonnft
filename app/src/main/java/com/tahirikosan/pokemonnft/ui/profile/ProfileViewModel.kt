package com.tahirikosan.pokemonnft.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.tahirikosan.pokemonnft.data.remote.Resource
import com.tahirikosan.pokemonnft.data.repository.AuthRepository
import com.tahirikosan.pokemonnft.data.repository.FirestoreRepository
import com.tahirikosan.pokemonnft.data.response.user.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    val repository: AuthRepository,
    val repositoryFirestore: FirestoreRepository,
) : ViewModel() {

    private var _userInfo = MutableLiveData<Resource<User>>()
    val userInfo: LiveData<Resource<User>> get() = _userInfo

    fun sigout() {
        repository.signOut()
    }

    fun getCurrentUser() = viewModelScope.launch {
        _userInfo.value = Resource.Loading
        _userInfo.value = repositoryFirestore.getUser()
    }
}