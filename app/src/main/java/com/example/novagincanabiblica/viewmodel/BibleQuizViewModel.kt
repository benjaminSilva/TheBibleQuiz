package com.example.novagincanabiblica.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.novagincanabiblica.data.models.quiz.Answer
import com.example.novagincanabiblica.data.models.quiz.Question
import com.example.novagincanabiblica.data.models.state.QuestionAnswerState
import com.example.novagincanabiblica.data.repositories.Repository
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
class BibleQuizViewModel @Inject constructor(
    private val repo: Repository
) : BaseViewModel(repo) {

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

    init {
        collectDay(onlyOnce = true)
        initBibleQuiz()
    }

    private fun initBibleQuiz() = viewModelScope.launch {
        day.collectLatest {
            if (it != -1) {
                listenToQuestion(it)
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
        repo.updateStats(currentQuestion.value, false, localSession.value).collectLatest {
            emitFeedbackMessage(it)
        }
    }

    fun verifyAnswer(selectedAnswer: Answer) = viewModelScope.launch {
        _screenClickable.emit(false)
        selectedAnswer.selected = true
        handleAnswerEffects(selectedAnswer.correct)
    }

    private fun handleAnswerEffects(isCorrect: Boolean) = viewModelScope.launch {
        updateQuestionResult(isCorrect = isCorrect)
        delay(500)
        _startSecondAnimation.emit(true)
        _currentQuestion.value.answerState = if (isCorrect) QuestionAnswerState.ANSWERED_CORRECTLY else QuestionAnswerState.ANSWERED_WRONGLY
        delay(2000)
        _nextDestination.emit(true)
    }

    fun updateQuestionResult(isCorrect: Boolean) = viewModelScope.launch {
        repo.updateStats(currentQuestion.value, isCorrect, localSession.value).collectLatest {
            emitFeedbackMessage(it)
        }
    }

    fun updateSession() = viewModelScope.launch {
        repo.updateHasPlayedBibleQuiz()
    }

}