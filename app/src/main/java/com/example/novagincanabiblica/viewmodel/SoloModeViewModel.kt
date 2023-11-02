package com.example.novagincanabiblica.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.novagincanabiblica.data.models.Answer
import com.example.novagincanabiblica.data.models.Question
import com.example.novagincanabiblica.data.models.Session
import com.example.novagincanabiblica.data.models.state.QuestionAnswerState
import com.example.novagincanabiblica.data.models.state.ResultOf
import com.example.novagincanabiblica.data.repositories.SoloModeRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SoloModeViewModel @Inject constructor(
    private val repo: SoloModeRepo
) : BaseViewModel() {

    private val _nextDestination = MutableSharedFlow<Boolean>()
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
        getDay()
    }

    private fun getDay() = viewModelScope.launch {
        repo.getDay().collectLatest { day ->
            day.handleSuccessAndFailure {
                listenToQuestion(it)
            }
            when (day) {
                is ResultOf.Success -> {
                    repo.loadDailyQuestion(day.value).collectLatest { questionResult ->
                        when (questionResult) {
                            is ResultOf.Success -> {
                                _currentQuestion.emit(questionResult.value)
                            }

                            is ResultOf.Failure -> {
                                _errorMessage.emit(questionResult.errorMessage)
                            }
                        }
                    }
                }

                is ResultOf.Failure -> {
                    _errorMessage.emit(day.errorMessage)
                }
            }
        }
    }

    private fun listenToQuestion(day: Int) = viewModelScope.launch {
        repo.loadDailyQuestion(day).collectLatest { questionResult ->
            questionResult.handleSuccessAndFailure {
                _currentQuestion.emit(it.copy(listOfAnswers = it.listOfAnswers.shuffled()))
            }
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
        _nextDestination.emit(true)
        repo.updateStats(currentQuestion.value, false).collectLatest {
            _errorMessage.emit(it)
        }
    }

    fun verifyAnswer(selectedAnswer: Answer) = viewModelScope.launch {
        _screenClickable.emit(false)
        selectedAnswer.selected = true
        if (selectedAnswer.correct) {
            delay(500)
            _startSecondAnimation.emit(true)
            _currentQuestion.value.answerState =
                QuestionAnswerState.ANSWERED_CORRECTLY
            delay(2000)
            repo.updateStats(currentQuestion.value, true).collectLatest {
                _errorMessage.emit(it)
            }
        } else {
            delay(500)
            _startSecondAnimation.emit(true)
            _currentQuestion.value.answerState =
                QuestionAnswerState.ANSWERED_WRONGLY
            delay(2000)
            /*repo.updateStats(currentQuestion.value, true).collectLatest {
                _errorMessage.emit(it)
            }*/
        }
        _nextDestination.emit(true)

    }

    fun updateSession() = viewModelScope.launch {
        repo.updateHasPlayedBibleQuiz()
    }

}