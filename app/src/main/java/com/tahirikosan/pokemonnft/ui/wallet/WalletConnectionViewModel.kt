package com.tahirikosan.pokemonnft.ui.wallet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tahirikosan.pokemonnft.data.remote.Resource
import com.tahirikosan.pokemonnft.data.repository.NtfRepository
import com.tahirikosan.pokemonnft.data.response.wallet.WalletResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WalletConnectionViewModel @Inject constructor(private val repository: NtfRepository) :
    ViewModel() {

    private val TEST_MNEMONIC = ""
    // Stores new wallet information.
    private var _walletResponse = MutableLiveData<Resource<WalletResponse>>()
    val walletResponse: LiveData<Resource<WalletResponse>> get() = _walletResponse

    // Creates a new wallet for user.
    fun createWallet() = viewModelScope.launch {
        _walletResponse.value = Resource.Loading
        _walletResponse.value = repository.createWallet()
    }

    fun connectWallet() = viewModelScope.launch{
        _walletResponse.value = Resource.Loading
        _walletResponse.value = repository.connectWallet(TEST_MNEMONIC)
    }

}