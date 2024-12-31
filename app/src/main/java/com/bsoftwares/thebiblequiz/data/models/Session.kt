package com.bsoftwares.thebiblequiz.data.models

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.bsoftwares.thebiblequiz.R
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
    val language: String = "",
    val premium: Boolean = false,
    var leaderboardTotalAllTime: Int = 0,
    var leaderboardTotalMonthly: Int = 0
)

fun Session.isReady() : Boolean = userInfo.userId.isNotEmpty()

data class WordleGame(
    val listOfAttempts: List<WordleAttempt> = generateStartWordleAttemptList(),
    val wordleStats: WordleData = WordleData()
)

data class UserData(
    val userId: String = "",
    val userName: String = "Guest",
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
    var streak: Int = 0,
    var answerSelected: String = "",
    var quizTotalPointsAllTime: Int = 0,
    var quizTotalPointsForTheMonths: Int = 0
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
    var streak: Int = 0,
    var wordleTotalPointsAllTime: Int = 0,
    var wordleTotalPointsForTheMonth: Int = 0,
    var playing: Boolean = false
) {
    fun getMax() =
        maxOf(winOnFirst, winOnSecond, winOnThird, winOnForth, winOnFirth, winOnSixth, lost)
    fun getTotal() = winOnFirst + winOnSecond + winOnThird + winOnForth + winOnFirth + winOnSixth + lost
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
    val title: UserTitle = UserTitle.NEW_BELIEVER
) {
    @get:Exclude
    val totalPoints: Int
        get() {
            return pointsForQuiz + pointsForWordle
        }
}

enum class UserTitle {
    ADMIN,
    UNDERSTANDABLE,
    NEW_BELIEVER,
    MOSES_ARK,
    PROPHET,
    PAUL_WAS_RIGHT,
    STEPHEN,
    DOWN,
    GRACE
}

@Composable
fun UserTitle.getString(): String = stringResource(id = when(this) {
    UserTitle.ADMIN -> R.string.the_boss_around_here
    UserTitle.UNDERSTANDABLE -> R.string.understandable_have_a_great_day
    UserTitle.NEW_BELIEVER -> R.string.i_am_a_new_creature_of_god
    UserTitle.MOSES_ARK -> R.string.the_last_animal_of_moses_ark
    UserTitle.PROPHET -> R.string.i_m_not_a_prophet_but_i_know_i_m_gonna_win
    UserTitle.PAUL_WAS_RIGHT -> R.string.paul_was_right_about_you
    UserTitle.STEPHEN -> R.string.stephen_deserved_a_spin_off
    UserTitle.DOWN ->R.string.how_is_the_view_from_down_there
    UserTitle.GRACE -> R.string.by_the_grace_of_god
})