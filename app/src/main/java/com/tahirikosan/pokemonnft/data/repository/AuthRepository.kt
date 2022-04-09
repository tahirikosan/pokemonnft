package com.tahirikosan.pokemonnft.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.tahirikosan.pokemonnft.data.remote.Resource
import com.tahirikosan.pokemonnft.data.remote.api.FirebaseAuthenticator
import com.tahirikosan.pokemonnft.utils.Utils
import com.tahirikosan.pokemonnft.utils.Utils.trimmedText
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authenticator: FirebaseAuthenticator
) : BaseAuthRepository, BaseRepository() {

    override suspend fun signInWithEmailPassword(
        email: String,
        password: String
    ): Resource<FirebaseUser> = safeApiCall {
        authenticator.signInWithEmailPassword(email, password)
    }

    override suspend fun signUpWithEmailPassword(
        email: String,
        password: String
    ): Resource<FirebaseUser> =
        safeApiCall {
            authenticator.signUpWithEmailPassword(email, password)
        }

    override fun signOut() {
        authenticator.signOut()
    }

    override suspend fun getCurrentUser(): Resource<FirebaseUser> = safeApiCall {
        authenticator.getUser()
    }

    override suspend fun sendResetPassword(email: String): Resource<Boolean> = safeApiCall {
        authenticator.sendPasswordReset(email)
    }
}