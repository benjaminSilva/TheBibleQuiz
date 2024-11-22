package com.bsoftwares.thebiblequiz.viewmodel

import com.bsoftwares.thebiblequiz.data.models.quiz.Answer
import com.bsoftwares.thebiblequiz.data.models.quiz.Question
import com.bsoftwares.thebiblequiz.data.models.state.QuestionAnswerState
import com.bsoftwares.thebiblequiz.data.repositories.BaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class BibleQuizViewModel @Inject constructor(
    private val repo: BaseRepository
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

    private val _correctAnswer = MutableStateFlow(Answer())
    val correctAnswer = _correctAnswer.asStateFlow()

    private val _selectedAnswer = MutableStateFlow(Answer())
    val selectedAnswer = _selectedAnswer.asStateFlow()

    init {
        initBibleQuiz()
    }

    private fun initBibleQuiz() = mainScope.launch {
        day.collectLatest {
            if (it != -1) {
                listenToQuestion(it)
            }
        }
    }

    private fun listenToQuestion(day: Int) = backGroundScope.launch {
        autoCancellable {
            repo.loadDailyQuestion(day).collectLatestAndApplyOnMain { questionResult ->
                questionResult.handleSuccessAndFailure { question ->
                    question.listOfAnswers.find { it.correct }?.apply {
                        _correctAnswer.emit(this)
                    }
                    _currentQuestion.emit(question.copy(listOfAnswers = question.listOfAnswers.shuffled()))
                }
            }
        }
    }

    fun startClock() = mainScope.launch {
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

        withContext(Dispatchers.IO) {
            autoCancellable {
                repo.updateStats(currentQuestion.value, false, localSession.value)
                    .collectLatest {}
            }
        }
    }

    fun verifyAnswer(selectedAnswer: Answer) = mainScope.launch {
        _screenClickable.emit(false)
        selectedAnswer.selected = true
        _selectedAnswer.emit(selectedAnswer)
        handleAnswerEffects(selectedAnswer.correct)
    }

    private fun handleAnswerEffects(isCorrect: Boolean) = mainScope.launch {
        updateQuestionResult(isCorrect = isCorrect)
        delay(500)
        _startSecondAnimation.emit(true)
        _currentQuestion.value.answerState =
            if (isCorrect) QuestionAnswerState.ANSWERED_CORRECTLY else QuestionAnswerState.ANSWERED_WRONGLY
        delay(2000)
        _nextDestination.emit(true)
    }

    fun updateQuestionResult(isCorrect: Boolean) = backGroundScope.launch {
            repo.updateStats(
                currentQuestion.value,
                isCorrect,
                localSession.value,
                selectedAnswer.value.answerText
            ).collectLatest {
                it.handleSuccessAndFailure {}
            }
        }

    fun updateGameAvailability() = backGroundScope.launch {
        repo.updateHasPlayedBibleQuiz()
    }

    fun sendQuestionSuggestion(question: Question) = backGroundScope.launch {
        autoCancellable {
            repo.sendQuestionSuggestion(question).collectLatest {
                it.handleSuccessAndFailure { feedbackMessage ->
                    emitFeedbackMessage(feedbackMessage)
                }
            }
        }
    }

}