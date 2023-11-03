package com.example.novagincanabiblica.data.models

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
    var streak: Int = 0
) {
    fun getTotalEasy(): Int = easyWrong + easyCorrect
    fun getTotalMedium(): Int = mediumWrong + mediumCorrect
    fun getTotalHard(): Int = hardWrong + hardCorrect
    fun getTotalImpossible(): Int = impossibleWrong + impossibleCorrect
}