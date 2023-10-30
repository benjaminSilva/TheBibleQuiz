package com.example.novagincanabiblica.data.models

import android.content.SharedPreferences
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import javax.inject.Inject

data class Session(
    val data: UserData? = UserData(),
    val errorMessage: String? = "",
    val hasPlayedQuizGame: Boolean = false,
    val hasPlayerWordleGame: Boolean = false,
    val dayReset: Boolean = false
)

data class UserData (
    val userId: String? = "",
    val userName: String? = "guest",
    val profilePictureUrl: String? = ""
)

class SessionCacheImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences
): SessionCache {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    private val adapter = moshi.adapter(Session::class.java)

    override fun saveSession(session: Session) {
        sharedPreferences.edit()
            .putString("session", adapter.toJson(session))
            .apply()
    }

    override fun getActiveSession(): Session? {
        val json = sharedPreferences.getString("session", null) ?: return Session()
        return adapter.fromJson(json)
    }

    override fun clearSession() {
        sharedPreferences.edit().remove("session").apply()
    }
}