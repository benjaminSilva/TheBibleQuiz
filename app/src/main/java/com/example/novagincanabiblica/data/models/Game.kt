package com.example.novagincanabiblica.data.models

data class SoloGameMode(
    var questions: List<Question> = listOf(),
    val points: Int = 0,
    val player: Player = Player(),
    val currentQuestionAnswers: List<String> = listOf(),
    val currentQuestionNumber: Int = 0
)