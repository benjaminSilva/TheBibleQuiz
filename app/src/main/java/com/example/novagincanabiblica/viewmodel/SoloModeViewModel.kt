package com.example.novagincanabiblica.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.novagincanabiblica.data.models.Question
import com.example.novagincanabiblica.data.models.SoloGameMode
import com.example.novagincanabiblica.data.repositories.SoloModeRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SoloModeViewModel : ViewModel() {

    val repo = SoloModeRepo()
    val game = SoloGameMode()

    private val _questions = MutableStateFlow(listOf<Question>())
    val questions = _questions.asStateFlow()

    private val _currentQuestion = MutableStateFlow(Question())
    val currentQuestion = _currentQuestion.asStateFlow()

    private val _currentQuestionNumber = MutableStateFlow(1)
    val currentQuestionNumber = _currentQuestionNumber.asStateFlow()

    fun loadQuestionsForSoloMode(jsonString: String) {
        viewModelScope.launch {
            _questions.value = repo.loadLocalQuestions(jsonString).questions.apply {
                game.questions = this
            }
        }
    }

    fun setupNewQuestion() {
        _currentQuestion.value = questions.value[currentQuestionNumber.value - 1].apply {
            listOfAnswers = listOf(correctAnswer, wrongAnswerThree, wrongAnswerTwo, wrongAnswerOne).shuffled()
        }
    }

    fun updateCurrentQuestionNumber() {
        _currentQuestionNumber.value = currentQuestionNumber.value + 1
    }
}