package com.tahirikosan.pokemonnft.data.remote

import com.google.firebase.auth.FirebaseAuthException
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
                        Resource.Failure(
                            errorCode = throwable.code(),
                            errorBody = throwable.response()?.errorBody()
                        )
                    }
                    is FirebaseFirestoreException -> {
                        Resource.Failure(
                            isFirebaseError = true,
                            errorMessage = throwable.message
                        )
                    }
                    is FirebaseAuthException -> {
                        Resource.Failure(
                            isFirebaseError = true,
                            errorMessage = throwable.message
                        )
                    }
                    else -> {
                        Resource.Failure(
                            isNetworkError = true,
                        )
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