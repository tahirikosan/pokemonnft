package com.tahirikosan.pokemonnft.ui.wallet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tahirikosan.pokemonnft.data.remote.Resource
import com.tahirikosan.pokemonnft.data.repository.NFTRepository
import com.tahirikosan.pokemonnft.data.response.wallet.WalletResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WalletConnectionViewModel @Inject constructor(private val repository: NFTRepository) :
    ViewModel() {

    // Stores new wallet information.
    private var _walletCreateResponse = MutableLiveData<Resource<WalletResponse>>()
    val walletCreateResponse: LiveData<Resource<WalletResponse>> get() = _walletCreateResponse

    private var _walletConnectResponse = MutableLiveData<Resource<WalletResponse>>()
    val walletConnectResponse: LiveData<Resource<WalletResponse>> get() = _walletConnectResponse

    // Creates a new wallet for user.
    fun createWallet() = viewModelScope.launch {
        _walletCreateResponse.value = Resource.Loading
        _walletCreateResponse.value = repository.createWallet()
    }

    fun connectWallet(mnemonic: String) = viewModelScope.launch{
        _walletConnectResponse.value = Resource.Loading
        _walletConnectResponse.value = repository.connectWallet(mnemonic)
    }

}