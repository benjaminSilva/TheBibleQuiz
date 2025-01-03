package com.bsoftwares.thebiblequiz.ui.screens


sealed class Routes(val value: String) {
    object Root: Routes(value = "root")
    object Home: Routes(value = "home_screen")
    object Start: Routes(value = "start")
    object QuizMode: Routes(value = "quiz_mode")
    object PreQuiz: Routes(value = "pre_quiz")
    object Quiz: Routes(value = "quiz")
    object QuizResults: Routes(value = "quiz_results")
    object SuggestQuestion: Routes(value = "quiz_suggest")
    object Profile: Routes(value = "profile/{instanceId}") {
        fun withParameter(userId: String) = "profile/${userId}"
    }
    object WordleMode: Routes(value = "wordle_mode")
    object Wordle: Routes(value = "wordle")
    object WordleResults: Routes(value = "wordle_results")
    object LeagueScreen: Routes(value = "leagues")
    object EditLeague: Routes(value = "edit_league")
    object AdScreen: Routes(value = "ad_screen")
    object LoginScreen: Routes(value = "login")
    object Leaderboards: Routes(value = "leaderboards")
}
