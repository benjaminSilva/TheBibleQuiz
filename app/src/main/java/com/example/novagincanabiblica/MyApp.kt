package com.example.novagincanabiblica

import android.app.Application
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import javax.inject.Named

@HiltAndroidApp
class MyApp : Application() {

    @Inject
    @Named("baseDatabase")
    lateinit var firebaseDatabase : FirebaseDatabase

    @Inject
    @Named("usersDatabase")
    lateinit var usersDatabase : FirebaseDatabase

    @Inject
    @Named("dailyVerseDatabase")
    lateinit var dailyVerseDatabase : FirebaseDatabase

    @Inject
    @Named("wordleDatabase")
    lateinit var wordleDatabase : FirebaseDatabase

    @Inject
    @Named("quizDatabase")
    lateinit var quizDatabase : FirebaseDatabase

    override fun onCreate() {
        super.onCreate()
        firebaseDatabase.setPersistenceEnabled(true)
        usersDatabase.setPersistenceEnabled(true)
        dailyVerseDatabase.setPersistenceEnabled(true)
        wordleDatabase.setPersistenceEnabled(true)
        quizDatabase.setPersistenceEnabled(true)
    }
}