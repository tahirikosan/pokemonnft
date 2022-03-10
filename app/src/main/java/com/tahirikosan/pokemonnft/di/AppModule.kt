package com.tahirikosan.pokemonnft.di

import android.content.Context
import com.tahirikosan.pokemonnft.data.remote.RemoteDataSource
import com.tahirikosan.pokemonnft.data.remote.api.NftApi
import com.tahirikosan.pokemonnft.data.repository.NtfRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {


    @Singleton
    @Provides
    fun provideNftApi(
        remoteDataSource: RemoteDataSource,
        @ApplicationContext context: Context
    ): NftApi = remoteDataSource.buildApi(NftApi::class.java, context)


    @Singleton
    @Provides
    fun provideNftRepository(api: NftApi): NtfRepository = NtfRepository(api)
}