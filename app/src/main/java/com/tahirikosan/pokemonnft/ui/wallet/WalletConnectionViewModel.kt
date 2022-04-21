package com.tahirikosan.pokemonnft.ui.wallet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tahirikosan.pokemonnft.data.remote.Resource
import com.tahirikosan.pokemonnft.data.repository.FirestoreRepository
import com.tahirikosan.pokemonnft.data.repository.NFTRepository
import com.tahirikosan.pokemonnft.data.response.wallet.WalletResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WalletConnectionViewModel @Inject constructor(
    private val repository: NFTRepository,
    private val firestoreRepository: FirestoreRepository
) :
    ViewModel() {

    // Stores new wallet information.
    private var _walletCreateResponse = MutableLiveData<Resource<WalletResponse>>()
    val walletCreateResponse: LiveData<Resource<WalletResponse>> get() = _walletCreateResponse

    private var _walletConnectResponse = MutableLiveData<Resource<WalletResponse>>()
    val walletConnectResponse: LiveData<Resource<WalletResponse>> get() = _walletConnectResponse

    private var _walletOccupiedBool = MutableLiveData<Resource<Boolean>>()
    val walletOccupiedBool: LiveData<Resource<Boolean>> get() = _walletOccupiedBool

    private var _walletAddToOccupiedListBool = MutableLiveData<Resource<Boolean>>()
    val walletAddToOccupiedListBool: LiveData<Resource<Boolean>> get() = _walletAddToOccupiedListBool

    private var _walletRemoveToOccupiedListBool = MutableLiveData<Resource<Boolean>>()
    val walletRemoveToOccupiedListBool: LiveData<Resource<Boolean>> get() = _walletRemoveToOccupiedListBool

    // Creates a new wallet for user.
    fun createWallet() = viewModelScope.launch {
        _walletCreateResponse.value = Resource.Loading
        _walletCreateResponse.value = repository.createWallet()
    }

    fun connectWallet(mnemonic: String) = viewModelScope.launch {
        _walletConnectResponse.value = Resource.Loading
        _walletConnectResponse.value = repository.connectWallet(mnemonic)
    }


    fun isWalletOccupied(publicKeyStr: String) = viewModelScope.launch {
        _walletOccupiedBool.value = Resource.Loading
        _walletOccupiedBool.value = firestoreRepository.isWalletOccupied(publicKeyStr)
    }

    fun addPublicKeyToFirestore(publicKeyStr: String) = viewModelScope.launch {
        _walletAddToOccupiedListBool.value = Resource.Loading
        _walletAddToOccupiedListBool.value =
            firestoreRepository.addPublicKeyToFirestore(publicKeyStr)
    }

    fun removePublicKeyToFirestore(publicKeyStr: String) = viewModelScope.launch {
        _walletRemoveToOccupiedListBool.value = Resource.Loading
        _walletRemoveToOccupiedListBool.value =
            firestoreRepository.removePublicKeyToFirestore(publicKeyStr)
    }

}