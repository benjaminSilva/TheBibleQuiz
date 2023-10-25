package com.example.novagincanabiblica.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.novagincanabiblica.data.models.Answer
import com.example.novagincanabiblica.data.models.Question
import com.example.novagincanabiblica.data.models.AnswerDestinationState
import com.example.novagincanabiblica.data.models.QuestionAnswerState
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

    private val _questions = mutableStateListOf<Question>()
    val questions = _questions

    private val _currentQuestionNumber = MutableStateFlow(1)
    val currentQuestionNumber = _currentQuestionNumber.asStateFlow()

    private val _nextDestination = MutableStateFlow(AnswerDestinationState.STAY)
    val nextDestination = _nextDestination.asStateFlow()

    init {
        viewModelScope.launch {
            _questions.addAll(repo.loadLocalQuestions())
            _questions
        }
    }

    private val _currentQuestion = MutableStateFlow(questions[0])
    val currentQuestion = _currentQuestion.asStateFlow()

    fun setupNewQuestion() = viewModelScope.launch {
        _nextDestination.emit(AnswerDestinationState.STAY)
        _currentQuestion.emit(
            questions[currentQuestionNumber.value - 1].apply {
                listOfAnswers = listOfAnswers.shuffled()
            }
        )
    }

    private fun updateQuestionNumber() = viewModelScope.launch {
        _currentQuestionNumber.emit(currentQuestionNumber.value + 1)
    }

    fun verifyAnswer(answer: Answer) = viewModelScope.launch {
        answer.selected = true
        if (answer.isCorrect) {
            _questions[currentQuestionNumber.value - 1].answerState = QuestionAnswerState.ANSWERED_CORRECTLY
            if(currentQuestionNumber.value < questions.size) {
                updateQuestionNumber()
                _nextDestination.emit(AnswerDestinationState.NEXT_QUESTION)
            } else {
                _nextDestination.emit(AnswerDestinationState.RESULTS)
            }
        } else {
            _questions[currentQuestionNumber.value - 1].answerState = QuestionAnswerState.ANSWERED_WRONGLY
            _nextDestination.emit(AnswerDestinationState.RESULTS)
        }
    }

    fun getAnsweredQuestions(): List<Question> = questions.filter { it.answerState != QuestionAnswerState.NOT_ANSWERED }.reversed()

    fun getCorrectAnswerQuestionSize(): Int = questions.filter { it.answerState == QuestionAnswerState.ANSWERED_CORRECTLY }.size

}