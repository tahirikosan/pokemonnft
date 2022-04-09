package com.tahirikosan.pokemonnft.data.remote

import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tahirikosan.pokemonnft.data.remote.api.FirebaseAuthenticator
import kotlinx.coroutines.tasks.await

class FirebaseAuthenticatorImpl : FirebaseAuthenticator {
    override suspend fun signUpWithEmailPassword(email: String, password: String): FirebaseUser {
        try {
            Firebase.auth.createUserWithEmailAndPassword(email, password).await()
            return Firebase.auth.currentUser!!
        } catch (e: Exception) {
            e.printStackTrace()
            throw  e
        }
    }

    override suspend fun signInWithEmailPassword(email: String, password: String): FirebaseUser {
        try {
            Firebase.auth.signInWithEmailAndPassword(email, password).await()
            return Firebase.auth.currentUser!!
        } catch (e: Exception) {
            e.printStackTrace()
            throw  e
        }
    }

    override fun signOut() {
        Firebase.auth.signOut()
    }

    override suspend fun getUser(): FirebaseUser {
        try {
            return Firebase.auth.currentUser!!
        } catch (e: Exception) {
            e.printStackTrace()
            throw  e
        }
    }

    override suspend fun sendPasswordReset(email: String): Boolean {
        try {
            Firebase.auth.sendPasswordResetEmail(email).await()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }
}
