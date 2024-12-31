package com.bsoftwares.thebiblequiz.data.models

data class RankingData(
    var name: String = "",
    var id: String = "",
    var profilePicture: String = "",
    var title: UserTitle? = UserTitle.NEW_BELIEVER,
    var pointsToDisplay: Int = 0
)

enum class LeaderboardType(val path: String) {
    MONTHLY_QUIZ("quizStats/quizTotalPointsForTheMonths"),
    MONTHLY_WORDLE("wordle/wordleStats/wordleTotalPointsForTheMonth"),
    MONTHLY_QUIZ_WORDLE("leaderboardTotalMonthly"),
    ALL_TIME_QUIZ("quizStats/quizTotalPointsAllTime"),
    ALL_TIME_WORDLE("wordle/wordleStats/wordleTotalPointsAllTime"),
    ALL_TIME_QUIZ_WORDLE("leaderboardTotalAllTime")
}

enum class LeaderboardDuration {
    MONTHLY,
    ALL_TIME
}

fun LeaderboardDuration.getString() = when (this) {
        LeaderboardDuration.MONTHLY -> "Monthly"
        LeaderboardDuration.ALL_TIME -> "All Time"
    }

enum class LeaderboardFilter {
    WORDLE,
    ALL,
    QUIZ
}

fun LeaderboardFilter.getString(): String = when (this) {
    LeaderboardFilter.ALL -> "Both"
    LeaderboardFilter.WORDLE -> "Wordle"
    LeaderboardFilter.QUIZ -> "Quiz"
}


fun List<Session>.toRankedData(leaderBoardType: LeaderboardType) = map {
    RankingData(
        name = it.userInfo.userName,
        id = it.userInfo.userId,
        profilePicture = it.userInfo.profilePictureUrl,
        title = null,
        pointsToDisplay = when (leaderBoardType) {
            LeaderboardType.MONTHLY_QUIZ -> it.quizStats.quizTotalPointsForTheMonths
            LeaderboardType.MONTHLY_WORDLE -> it.wordle.wordleStats.wordleTotalPointsForTheMonth
            LeaderboardType.MONTHLY_QUIZ_WORDLE -> it.leaderboardTotalMonthly
            LeaderboardType.ALL_TIME_QUIZ -> it.quizStats.quizTotalPointsAllTime
            LeaderboardType.ALL_TIME_WORDLE -> it.wordle.wordleStats.wordleTotalPointsAllTime
            LeaderboardType.ALL_TIME_QUIZ_WORDLE -> it.leaderboardTotalAllTime
        }
    )
}