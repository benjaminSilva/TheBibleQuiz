package com.example.novagincanabiblica.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.content.res.AssetManager
import com.example.novagincanabiblica.client.GoogleAuthUiClient
import com.example.novagincanabiblica.client.WordleService
import com.example.novagincanabiblica.data.models.SessionCache
import com.example.novagincanabiblica.data.models.SessionCacheImpl
import com.example.novagincanabiblica.data.repositories.SoloModeRepo
import com.example.novagincanabiblica.data.repositories.SoloModeRepoImpl
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun getGoogleAuthClient(@ApplicationContext appContext: Context) = GoogleAuthUiClient(
        context = appContext,
        oneTapClient = Identity.getSignInClient(appContext)
    )

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("session_prefs", MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideSessionCache(sharedPreferences: SharedPreferences): SessionCache {
        return SessionCacheImpl(sharedPreferences = sharedPreferences)
    }

    @Singleton
    @Provides
    fun getSoloModeRepo(
        googleAuthUiClient: GoogleAuthUiClient,
        sessionCache: SessionCache,
        firebaseDatabase: FirebaseDatabase,
        wordleService: WordleService
    ): SoloModeRepo = SoloModeRepoImpl(
        googleAuthUiClient = googleAuthUiClient,
        sessionCache = sessionCache,
        firebaseDatabase = firebaseDatabase,
        wordleService = wordleService
    )

    @Singleton
    @Provides
    fun getFirebaseRealTimeDatabase() = FirebaseDatabase.getInstance()

    @Singleton
    @Provides
    fun getWordleService() = Retrofit.Builder()
        .baseUrl("https://api.dictionaryapi.dev/api/v2/entries/en/")
        .addConverterFactory(GsonConverterFactory.create())
        .build().create(WordleService::class.java)

}