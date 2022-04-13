package com.tahirikosan.pokemonnft.di

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.tahirikosan.pokemonnft.data.local.UserPreferences
import com.tahirikosan.pokemonnft.data.remote.FirebaseAuthenticatorImpl
import com.tahirikosan.pokemonnft.data.remote.FirestoreDatabaseImpl
import com.tahirikosan.pokemonnft.data.remote.RemoteDataSource
import com.tahirikosan.pokemonnft.data.remote.api.*
import com.tahirikosan.pokemonnft.data.repository.*
import com.tahirikosan.pokemonnft.data.repository.pokemon.PokemonRepository
import com.tahirikosan.pokemonnft.data.repository.pokemon.PokemonRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    /**All of our application dependencies shall be provided here*/

    @Singleton
    @Provides
    fun provideUserPreferences(@ApplicationContext context: Context): UserPreferences =
        UserPreferences(context)

    @Singleton
    @Provides
    fun provideNftApi(
        remoteDataSource: RemoteDataSource,
        @ApplicationContext context: Context
    ): NftApi = remoteDataSource.buildApi(NftApi::class.java, context)

    @Singleton
    @Provides
    fun provideNftRepository(api: NftApi): NFTRepository = NFTRepository(api)

    @Singleton
    @Provides
    fun providePokedexApi(
        remoteDataSource: RemoteDataSource,
        @ApplicationContext context: Context
    ): PokedexApi = remoteDataSource.buildPokedexApi(PokedexApi::class.java, context)

    @Singleton
    @Provides
    fun providePokedexRepository(
        api: PokedexApi
    ): PokedexRepository = PokedexRepository(api)

    @Singleton
    @Provides
    fun providePokemonApi(
        remoteDataSource: RemoteDataSource,
        @ApplicationContext context: Context
    ): PokemonApi = remoteDataSource.buildApi(PokemonApi::class.java, context)


    @Singleton
    @Provides
    fun providePokemonRepository(
        pokemonApi: PokemonApi
    ): PokemonRepository = PokemonRepositoryImpl(pokemonApi)

    //this means that anytime we need an authenticator Dagger will provide a Firebase authenticator.
    //in future if you want to swap out Firebase authentication for your own custom authenticator
    //you will simply come and swap here.
    @Singleton
    @Provides
    fun provideAuthenticator(): FirebaseAuthenticator {
        return FirebaseAuthenticatorImpl()
    }

    //this just takes the same idea as the authenticator. If we create another repository class
    //we can simply just swap here
    @Singleton
    @Provides
    fun provideRepository(authenticator: FirebaseAuthenticator): BaseAuthRepository {
        return AuthRepository(authenticator)
    }

    @Provides
    @Singleton
    fun provideDBInstance(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Singleton
    @Provides
    fun provideFirestoreDatabase(): FirestoreDatabase {
        return FirestoreDatabaseImpl(firestore = FirebaseFirestore.getInstance())
    }

    //this just takes the same idea as the authenticator. If we create another repository class
    //we can simply just swap here
    @Singleton
    @Provides
    fun provideFirestoreRepository(firestoreDatabase: FirestoreDatabase): FirestoreRepository {
        return FirestoreRepository(firestoreDatabase)
    }

}