package com.tahirikosan.pokemonnft.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import android.content.SharedPreferences

import androidx.security.crypto.EncryptedSharedPreferences

import androidx.security.crypto.MasterKeys
import java.security.PrivateKey


class UserPreferences @Inject constructor(@ApplicationContext context: Context) {

    private val appContext = context.applicationContext

    var masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    var sharedPreferences = EncryptedSharedPreferences.create(
        "secret_shared_prefs",
        masterKeyAlias,
        appContext,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    // use the shared preferences and editor as you normally would
    var editor = sharedPreferences.edit()

    val privateKey: String?
        get() = sharedPreferences.getString(PRIVATE_KEY, null)

    val publicKey: String?
        get() = sharedPreferences.getString(PUBLIC_KEY, null)

    val accessToken: String?
        get() = sharedPreferences.getString(ACCESS_TOKEN, null)

    val refreshToken: String?
        get() = sharedPreferences.getString(REFRESH_TOKEN, null)


    fun savePrivateKey(privateKey: String) {
        editor.putString(PRIVATE_KEY, privateKey)
        editor.apply()
    }

    fun savePublicKey(publicKey: String) {
        editor.putString(PUBLIC_KEY, publicKey)
        editor.apply()
    }

    fun saveAccessTokens(accessToken: String?, refreshToken: String?) {
        editor.putString(ACCESS_TOKEN, accessToken)
        editor.putString(REFRESH_TOKEN, refreshToken)
        editor.apply()
    }

    fun isPrivateKeyExist(): Boolean = sharedPreferences.contains(PRIVATE_KEY)



    fun clear() {
    }

    companion object {
        private val ACCESS_TOKEN = "key_access_token"
        private val REFRESH_TOKEN = "key_refresh_token"
        private val USER_TYPE = "key_user_type"
        private val IS_ACCOUNT_VERIFIED = "is_account_verified"
        private val PRIVATE_KEY = "private_key"
        private val PUBLIC_KEY = "public_key"
    }
}