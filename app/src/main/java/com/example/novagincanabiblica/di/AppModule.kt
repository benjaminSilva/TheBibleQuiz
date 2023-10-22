package com.example.novagincanabiblica.di

import android.content.Context
import android.content.res.AssetManager
import com.example.novagincanabiblica.data.repositories.SoloModeRepo
import com.example.novagincanabiblica.data.repositories.SoloModeRepoImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideAssetManager(@ApplicationContext appContext: Context) : AssetManager = appContext.assets

    @Singleton
    @Provides
    fun getSoloModeRepo(assetManager: AssetManager) : SoloModeRepo = SoloModeRepoImpl(assetManager)

}