package com.example.novagincanabiblica.data.models

data class Question(
    val question: String = "",
    val correctAnswer: String = "",
    val wrongAnswerOne: String = "",
    val wrongAnswerTwo: String = "",
    val wrongAnswerThree: String = "",
    val bibleVerse: String = "",
    var listOfAnswers: List<String> = listOf("","","",""),
    val answeredCorrectly: Boolean = false,
    val difficulty: QuestionDifficulty = QuestionDifficulty.EASY
)