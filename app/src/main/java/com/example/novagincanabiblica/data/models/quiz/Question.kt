package com.example.novagincanabiblica.data.models.quiz

import com.example.novagincanabiblica.data.models.state.QuestionAnswerState

data class Question(
    val question: String = "",
    var listOfAnswers: List<Answer> = listOf(),
    val bibleVerse: String = "",
    var answerState: QuestionAnswerState = QuestionAnswerState.NOT_ANSWERED,
    val difficulty: QuestionDifficulty = QuestionDifficulty.EASY,
    val createdBy: String = ""
)

data class Answer(
    val answerText: String = "",
    val correct: Boolean = false,
    var selected: Boolean = false
)