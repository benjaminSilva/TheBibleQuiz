package com.example.novagincanabiblica.ui.screens

sealed class Routes(val value: String) {
    object Root: Routes(value = "root")
    object Home: Routes(value = "home_screen")
    object Start: Routes(value = "start")
    object QuizMode: Routes(value = "quiz_mode")
    object PreQuiz: Routes(value = "pre_quiz")
    object Quiz: Routes(value = "quiz")
    object QuizResults: Routes(value = "quiz_results")
    object Profile: Routes(value = "profile")
    object WordleMode: Routes(value = "wordle_mode")
    object Wordle: Routes(value = "wordle")
    object WordleResults: Routes(value = "wordle_results")
}
