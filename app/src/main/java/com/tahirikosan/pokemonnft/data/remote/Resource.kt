package com.tahirikosan.pokemonnft.data.remote

import okhttp3.ResponseBody

sealed class Resource<out T> {
    data class Success<out T>(val value: T) : Resource<T>()
    data class Failure(
        val isNetworkError: Boolean=false,
        val isFirebaseError: Boolean=false,
        val errorCode: Int?=null,
        val errorBody: ResponseBody?=null,
        val errorMessage: String?=null,
    ) : Resource<Nothing>()
    object Loading : Resource<Nothing>()
}