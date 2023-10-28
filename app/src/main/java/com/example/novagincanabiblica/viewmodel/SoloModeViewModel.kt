package com.example.novagincanabiblica.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.novagincanabiblica.data.models.Answer
import com.example.novagincanabiblica.data.models.Question
import com.example.novagincanabiblica.data.models.Session
import com.example.novagincanabiblica.data.models.SessionCache
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
import kotlin.random.Random

@HiltViewModel
class SoloModeViewModel @Inject constructor(
    private val repo: SoloModeRepo,
    private val session: SessionCache
) : ViewModel() {

    private val _questions = mutableStateListOf<Question>()
    val questions = _questions

    private val _nextDestination = MutableSharedFlow<AnswerDestinationState>()
    val nextDestination = _nextDestination.asSharedFlow()

    private val _currentQuestion = MutableStateFlow(Question())
    val currentQuestion = _currentQuestion.asStateFlow()

    private val _remainingTime = MutableStateFlow("30")
    val remainingTime = _remainingTime.asStateFlow()

    private val _screenClickable = MutableStateFlow(true)
    val screenClickable = _screenClickable.asStateFlow()

    private val _startSecondAnimation = MutableStateFlow(false)
    val startSecondAnimation = _startSecondAnimation.asStateFlow()

    private val _sessionState = MutableStateFlow(Session())
    val sessionState = _sessionState.asStateFlow()

    init {
        _questions.addAll(repo.loadLocalQuestions())
        setupNewQuestion()
        emitSession()
    }

    private fun emitSession() = viewModelScope.launch {
        session.getActiveSession()?.apply {
            _sessionState.emit(this)
        }
    }

    fun startClock() = viewModelScope.launch {
        var remainingTime = 30
        delay(500)
        while (remainingTime >= 0) {
            if (currentQuestion.value.answerState != QuestionAnswerState.NOT_ANSWERED) {
                return@launch
            }
            _remainingTime.emit(remainingTime.toString())
            remainingTime -= 1
            delay(1000)
        }
        _screenClickable.emit(false)
        _nextDestination.emit(AnswerDestinationState.RESULTS)
    }

    private fun setupNewQuestion() = viewModelScope.launch {
        _screenClickable.emit(true)
        _nextDestination.emit(AnswerDestinationState.STAY)
        _currentQuestion.emit(
            questions[Random.nextInt(0, 3)].apply {
                listOfAnswers = listOfAnswers.shuffled()
            }
        )
    }

    fun verifyAnswer(selectedAnswer: Answer) = viewModelScope.launch {
        _screenClickable.emit(false)
        selectedAnswer.selected = true

        if (selectedAnswer.isCorrect) {
            delay(1000)
            _startSecondAnimation.emit(true)
            _currentQuestion.value.answerState =
                QuestionAnswerState.ANSWERED_CORRECTLY
            delay(2000)
            _nextDestination.emit(AnswerDestinationState.RESULTS)

        } else {
            delay(1000)
            _startSecondAnimation.emit(true)
            _currentQuestion.value.answerState =
                QuestionAnswerState.ANSWERED_WRONGLY
            delay(2000)
            _nextDestination.emit(AnswerDestinationState.RESULTS)
        }
    }

}