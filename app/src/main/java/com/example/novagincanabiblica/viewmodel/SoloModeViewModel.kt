package com.example.novagincanabiblica.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.novagincanabiblica.data.models.Answer
import com.example.novagincanabiblica.data.models.Question
import com.example.novagincanabiblica.data.models.state.AnswerDestinationState
import com.example.novagincanabiblica.data.models.state.QuestionAnswerState
import com.example.novagincanabiblica.data.repositories.SoloModeRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SoloModeViewModel @Inject constructor(
    private val repo: SoloModeRepo
) : ViewModel() {

    private val _questions = mutableStateListOf<Question>()
    val questions = _questions

    private val _currentQuestionNumber = MutableStateFlow(1)
    val currentQuestionNumber = _currentQuestionNumber.asStateFlow()

    private val _nextDestination = MutableSharedFlow<AnswerDestinationState>()
    val nextDestination = _nextDestination.asSharedFlow()

    private val _currentQuestion = MutableStateFlow(Question())
    val currentQuestion = _currentQuestion.asStateFlow()

    init {
        _questions.addAll(repo.loadLocalQuestions())
        setupNewQuestion()
    }

    private fun setupNewQuestion() = viewModelScope.launch {
        _nextDestination.emit(AnswerDestinationState.STAY)
        _currentQuestion.emit(
            questions[currentQuestionNumber.value - 1].apply {
                listOfAnswers = listOfAnswers.shuffled()
            }
        )
    }

    private fun updateQuestionNumber() {
        _currentQuestionNumber.value = (currentQuestionNumber.value + 1)
    }

    fun verifyAnswer(answer: Answer) = viewModelScope.launch {
        answer.selected = true
        if (answer.isCorrect) {
            _questions[currentQuestionNumber.value - 1].answerState =
                QuestionAnswerState.ANSWERED_CORRECTLY
            if (currentQuestionNumber.value < questions.size) {
                _nextDestination.emit(AnswerDestinationState.NEXT_QUESTION)
                delay(200)
                updateQuestionNumber()
                setupNewQuestion()
            } else {
                _nextDestination.emit(AnswerDestinationState.RESULTS)
            }
        } else {
            _questions[currentQuestionNumber.value - 1].answerState =
                QuestionAnswerState.ANSWERED_WRONGLY
            _nextDestination.emit(AnswerDestinationState.RESULTS)
        }
    }

    fun getAnsweredQuestions(): List<Question> =
        questions.filter { it.answerState != QuestionAnswerState.NOT_ANSWERED }.reversed()

    fun getCorrectAnswerQuestionSize(): Int =
        questions.filter { it.answerState == QuestionAnswerState.ANSWERED_CORRECTLY }.size

}