package com.tahirikosan.pokemonnft.ui.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.tahirikosan.pokemonnft.data.remote.Resource
import com.tahirikosan.pokemonnft.data.repository.BaseAuthRepository
import com.tahirikosan.pokemonnft.data.repository.FirestoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: BaseAuthRepository,
    private val repositoryFireStore: FirestoreRepository,
) : ViewModel() {


    private val TAG = "MainViewModel"

    /**This is a ViewModel class and is responsible for the logic of all ui.
     * It shall be shared with the three fragments.
     * Only share ViewModels when the fragments share a feature or functionality */

    //create the auth state livedata object that will be passed to
    //the home fragment and shall be used to control the ui i.e show authentication state
    //control behaviour of sign in and sign up button
    private val _firebaseUser = MutableLiveData<Resource<FirebaseUser>>()
    val currentUser get() = _firebaseUser

    // After sigup auth then add user to firestore.
    private val _isUserAddedFirestore = MutableLiveData<Resource<Boolean>>()
    val isUserAddedFirestore get() = _isUserAddedFirestore

    // Store result of password reseting.
    private val _passwordResetSuccess = MutableLiveData<Resource<Boolean>>()
    val passwordResetSuccess get() = _passwordResetSuccess

    //validate all fields first before performing any sign in operations
    fun signInUser(email: String, password: String) = viewModelScope.launch {
        _firebaseUser.value = Resource.Loading
        _firebaseUser.value = repository.signInWithEmailPassword(email, password)
    }

    //validate all fields before performing any sign up operations
    fun signUpUser(email: String, password: String) = viewModelScope.launch {
        _firebaseUser.value = Resource.Loading
        _firebaseUser.value = repository.signUpWithEmailPassword(email, password)
    }

    fun addUserToFirestore(userId: String) = viewModelScope.launch {
        _isUserAddedFirestore.value = Resource.Loading
        _isUserAddedFirestore.value = repositoryFireStore.addUserToFirestore(userId)
    }

    fun verifySendPasswordReset(email: String) = viewModelScope.launch {
        _passwordResetSuccess.value = Resource.Loading
        _passwordResetSuccess.value = repository.sendResetPassword(email)
    }

    fun signOut() = viewModelScope.launch {
        try {
            val user = repository.signOut()

        } catch (e: Exception) {
            val error = e.toString().split(":").toTypedArray()
        }
    }

    fun getCurrentUser() = viewModelScope.launch {
        _firebaseUser.value = Resource.Loading
        _firebaseUser.value = repository.getCurrentUser()
    }
}