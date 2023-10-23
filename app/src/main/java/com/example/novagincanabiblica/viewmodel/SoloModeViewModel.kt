package com.example.novagincanabiblica.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.novagincanabiblica.data.models.Question
import com.example.novagincanabiblica.data.models.QuestionAnswerState
import com.example.novagincanabiblica.data.repositories.SoloModeRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SoloModeViewModel @Inject constructor(
    private val repo : SoloModeRepo
) : ViewModel() {

    private val _questions = MutableStateFlow(listOf<Question>())
    private val questions = _questions.asStateFlow()

    private val _currentQuestion = MutableStateFlow(Question())
    val currentQuestion = _currentQuestion.asStateFlow()

    private val _currentQuestionNumber = MutableStateFlow(1)
    val currentQuestionNumber = _currentQuestionNumber.asStateFlow()

    private val _answerState = MutableStateFlow(QuestionAnswerState.NOT_ANSWERED)
    val answerState = _answerState.asStateFlow()

    fun loadQuestionsForSoloMode() {
        viewModelScope.launch {
            _questions.value = repo.loadLocalQuestions()
        }
    }

    fun setupNewQuestion() = viewModelScope.launch {
        _answerState.emit(QuestionAnswerState.NOT_ANSWERED)
        _currentQuestion.emit(
            questions.value[currentQuestionNumber.value - 1].apply {
                listOfAnswers = listOfAnswers.shuffled()
            }
        )
    }

    private fun updateQuestionNumber() = viewModelScope.launch {
        _currentQuestionNumber.emit(currentQuestionNumber.value + 1)
    }

    fun verifyAnswer(verify: Boolean) = if (verify) {
        updateQuestionNumber()
        _answerState.value = QuestionAnswerState.CORRECT
    } else {
        _answerState.value = QuestionAnswerState.WRONG
    }
}