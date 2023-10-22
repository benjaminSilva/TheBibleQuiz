package com.example.novagincanabiblica.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.novagincanabiblica.data.models.Question
import com.example.novagincanabiblica.data.repositories.SoloModeRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SoloModeViewModel @Inject constructor(
    private val repo : SoloModeRepo
) : ViewModel() {

    private val _questions = MutableStateFlow(listOf<Question>())
    val questions = _questions.asStateFlow()

    private val _currentQuestion = MutableStateFlow(Question())
    val currentQuestion = _currentQuestion.asStateFlow()

    private val _currentQuestionNumber = MutableStateFlow(1)
    val currentQuestionNumber = _currentQuestionNumber.asStateFlow()

    fun loadQuestionsForSoloMode() {
        viewModelScope.launch {
            _questions.value = repo.loadLocalQuestions()
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