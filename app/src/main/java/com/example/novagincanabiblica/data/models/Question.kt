package com.example.novagincanabiblica.data.models

data class Question(
    val question: String = "",
    var listOfAnswers: List<Answer> = listOf(),
    val bibleVerse: String = "",
    val answeredCorrectly: Boolean = false,
    val difficulty: QuestionDifficulty = QuestionDifficulty.EASY
)

data class Answer(
    val answerText: String = "",
    val isCorrect: Boolean = false
)