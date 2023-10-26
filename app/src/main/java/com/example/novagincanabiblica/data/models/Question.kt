package com.example.novagincanabiblica.data.models

import com.example.novagincanabiblica.data.models.state.QuestionAnswerState

data class Question(
    val question: String = "",
    var listOfAnswers: List<Answer> = listOf(),
    val bibleVerse: String = "",
    var answerState: QuestionAnswerState = QuestionAnswerState.NOT_ANSWERED,
    val difficulty: QuestionDifficulty = QuestionDifficulty.EASY
)

data class Answer(
    val answerText: String = "",
    val isCorrect: Boolean = false,
    var selected: Boolean = false
)