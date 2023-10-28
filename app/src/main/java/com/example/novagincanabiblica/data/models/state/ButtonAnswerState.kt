package com.example.novagincanabiblica.data.models.state

import androidx.compose.ui.graphics.Color
import com.example.novagincanabiblica.ui.theme.correctAnswer
import com.example.novagincanabiblica.ui.theme.wrongAnswer
import com.example.novagincanabiblica.ui.theme.wrongAnswerSelected

sealed class ButtonAnswerState(val value: Color) {
    object CorrectAnswer: ButtonAnswerState(correctAnswer)
    object WrongAnswer: ButtonAnswerState(wrongAnswer)
    object WrongAnswerSelected: ButtonAnswerState(wrongAnswerSelected)
}