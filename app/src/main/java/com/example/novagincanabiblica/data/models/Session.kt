package com.example.novagincanabiblica.data.models

import com.example.novagincanabiblica.data.models.wordle.WordleAttempt
import com.example.novagincanabiblica.data.models.wordle.generateStartWordleAttemptList

data class Session(
    val userInfo: UserData? = UserData(),
    val errorMessage: String? = "",
    val hasPlayedQuizGame: Boolean = false,
    val hasPlayerWordleGame: Boolean = false,
    val quizStats: QuestionStatsData = QuestionStatsData(),
    val wordle: WordleGame = WordleGame(),
    val friendList: List<String> = listOf(),
    val friendRequests: List<String> = listOf(),
    val fcmToken: String = ""
)

data class WordleGame(
    val listOfAttemps: List<WordleAttempt> = generateStartWordleAttemptList(),
    val wordleStats: WordleData = WordleData()
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

data class QuestionStatsDataCalculated(
    var easyFloat: Float = 0f,
    var mediumFloat: Float = 0f,
    var hardFLoat: Float = 0f,
    var impossibleFloat: Float = 0f,
    var easyInt: Int = 0,
    var mediumInt: Int = 0,
    var hardInt: Int = 0,
    var impossibleInt: Int = 0
)

data class WordleData(
    var winOnFirst: Int = 0,
    var winOnSecond: Int = 0,
    var winOnThird: Int = 0,
    var winOnForth: Int = 0,
    var winOnFirth: Int = 0,
    var winOnSixth: Int = 0,
    var lost: Int = 0,
    var streak: Int = 0
) {
    fun getMax() =
        maxOf(winOnFirst, winOnSecond, winOnThird, winOnForth, winOnFirth, winOnSixth, lost)
}

data class WordleDataCalculated(
    val firstTryFloat: Float = 0f,
    val secondTryFloat: Float = 0f,
    val thirdTryFloat: Float = 0f,
    val forthTryFloat: Float = 0f,
    val firthTryFloat: Float = 0f,
    val sixthTryFloat: Float = 0f,
    val lostFloat: Float = 0f,
)