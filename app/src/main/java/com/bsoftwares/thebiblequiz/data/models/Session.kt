package com.bsoftwares.thebiblequiz.data.models

import com.bsoftwares.thebiblequiz.data.models.wordle.WordleAttempt
import com.bsoftwares.thebiblequiz.data.models.wordle.generateStartWordleAttemptList
import com.google.firebase.database.Exclude

data class Session(
    val userInfo: UserData = UserData(),
    val hasPlayedQuizGame: Boolean = false,
    val hasPlayerWordleGame: Boolean = false,
    val quizStats: QuestionStatsData = QuestionStatsData(),
    val wordle: WordleGame = WordleGame(),
    @Exclude
    val localFriendList: List<String> = listOf(),
    @Exclude
    val localFriendRequestList: List<String> = listOf(),
    @Exclude
    val localListLeagues: List<String> = listOf(),
    @Exclude
    val localLeagueRequestList: List<String> = listOf(),
    val fcmToken: String = "",
    val premium: Boolean = false
)

data class WordleGame(
    val listOfAttempts: List<WordleAttempt> = generateStartWordleAttemptList(),
    val wordleStats: WordleData = WordleData()
)

data class UserData(
    val userId: String = "",
    val userName: String = "guest",
    val profilePictureUrl: String = ""
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
    @Exclude
    fun getTotalEasy(): Int = easyWrong + easyCorrect
    @Exclude
    fun getTotalMedium(): Int = mediumWrong + mediumCorrect
    @Exclude
    fun getTotalHard(): Int = hardWrong + hardCorrect
    @Exclude
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

data class SessionInLeague(
    val userId: String = "",
    val profileImage: String = "",
    val adminUser: Boolean = false,
    val userName: String = "",
    val pointsForQuiz: Int = 0,
    val pointsForWordle: Int = 0,
    val title: String = ""
) {
    @get:Exclude
    val totalPoints: Int
        get() {
            return pointsForQuiz + pointsForWordle
        }
}