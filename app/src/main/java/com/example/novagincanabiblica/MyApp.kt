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

    override fun onCreate() {
        super.onCreate()
        firebaseDatabase.setPersistenceEnabled(true)
    }
}