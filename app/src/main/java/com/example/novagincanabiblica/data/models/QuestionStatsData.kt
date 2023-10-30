package com.example.novagincanabiblica.data.models

data class QuestionStatsData(
    val easyCorrect: Int,
    val easyWrong: Int,
    val mediumCorrect: Int,
    val mediumWrong: Int,
    val hardCorrect: Int,
    val hardWrong: Int,
    val impossibleCorrect: Int,
    val impossibleWrong: Int,
    val streak: Int,
    val totalEasy: Int = easyCorrect + easyWrong,
    val totalMedium: Int = mediumCorrect + mediumWrong,
    val totalHard: Int = hardCorrect + hardWrong,
    val totalImpossible: Int = impossibleCorrect + impossibleWrong,
    val totalQuestionsAnswered: Int = totalEasy + totalMedium + totalHard + totalImpossible
)
