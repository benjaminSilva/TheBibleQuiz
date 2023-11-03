package com.example.novagincanabiblica

import android.app.Application
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }
}