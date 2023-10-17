package com.example.novagincanabiblica.data

data class Question(
    val question: String,
    val correctAnswer: String,
    val wrongAnswerOne: String,
    val wrongAnswerTwo: String,
    val wrongAnswerThree: String,
    val bibleVerse: String,
    val answeredCorrectly: Boolean
)
