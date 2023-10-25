package com.example.novagincanabiblica.data.models

import androidx.compose.ui.graphics.Color
import com.example.novagincanabiblica.ui.theme.Purple40
import com.example.novagincanabiblica.ui.theme.PurpleGrey40
import com.example.novagincanabiblica.ui.theme.PurpleGrey80

sealed class ButtonAnswerState(val value: Color) {
    object CorrectAnswer: ButtonAnswerState(Purple40)
    object WrongAnswer: ButtonAnswerState(PurpleGrey40)
    object WrongAnswerSelected: ButtonAnswerState(PurpleGrey80)
}