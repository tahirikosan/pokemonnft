package com.tahirikosan.pokemonnft.data.remote

import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.json.JSONObject
import retrofit2.HttpException

interface SafeApiCall {
    suspend fun <T> safeApiCall(
        apiCall: suspend () -> T
    ): Resource<T> {
        return withContext(Dispatchers.IO) {
            try {
                Resource.Success(apiCall.invoke())
            } catch (throwable: Throwable) {
                when (throwable) {
                    is HttpException -> {
                        Resource.Failure(false, throwable.code(), throwable.response()?.errorBody())
                    }
                    is FirebaseFirestoreException -> {
                        Resource.Failure(
                            false,
                            throwable.code.value(),
                            createResponseBody(throwable.message.toString())
                        )
                    }
                    else -> {
                        Resource.Failure(true, null, null)
                    }
                }
            }
        }
    }
}

fun createResponseBody(responseObject: Any): ResponseBody {
    val jsonObject = JSONObject(Gson().toJson(responseObject))
    return jsonObject.toString()
        .toResponseBody("application/json; charset=utf-8".toMediaTypeOrNull())
}