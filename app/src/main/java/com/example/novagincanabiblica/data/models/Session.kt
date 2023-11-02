package com.example.novagincanabiblica.data.models

import android.content.SharedPreferences
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import javax.inject.Inject

data class Session(
    val userInfo: UserData? = UserData(),
    val errorMessage: String? = "",
    val hasPlayedQuizGame: Boolean = false,
    val hasPlayerWordleGame: Boolean = false,
    val dayReset: Boolean = false,
    val userStats: QuestionStatsData = QuestionStatsData()
)

data class UserData(
    val userId: String? = "",
    val userName: String? = "guest",
    val profilePictureUrl: String? = ""
)

data class QuestionStatsData(
    var easyCorrect: Int = 0,
    var easyWrong: Int = 0,
    var mediumCorrect: Int = 0,
    var mediumWrong: Int = 0,
    var hardCorrect: Int = 0,
    var hardWrong: Int = 0,
    var impossibleCorrect: Int = 0,
    var impossibleWrong: Int = 0,
    var streak: Int = 0,
    val totalEasy: Int = easyCorrect + easyWrong,
    val totalMedium: Int = mediumCorrect + mediumWrong,
    val totalHard: Int = hardCorrect + hardWrong,
    val totalImpossible: Int = impossibleCorrect + impossibleWrong,
    val totalQuestionsAnswered: Int = totalEasy + totalMedium + totalHard + totalImpossible
)


class SessionCacheImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : SessionCache {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    private val adapter = moshi.adapter(Session::class.java)

    override fun saveSession(session: Session) {
        sharedPreferences.edit()
            .putString("session", adapter.toJson(session))
            .apply()
    }

    override fun getActiveSession(): Session {
        sharedPreferences.getString("session", Session().toString())?.apply {
            adapter.fromJson(this)?.apply {
                return this
            }
        }
        return Session()
    }

    override fun clearSession() {
        sharedPreferences.edit().remove("session").apply()
    }
}