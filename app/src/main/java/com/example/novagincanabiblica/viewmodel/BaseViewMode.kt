package com.example.novagincanabiblica.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.novagincanabiblica.data.models.QuestionStatsDataCalculated
import com.example.novagincanabiblica.data.models.Session
import com.example.novagincanabiblica.data.models.WordleDataCalculated
import com.example.novagincanabiblica.data.models.state.FeedbackMessage
import com.example.novagincanabiblica.data.models.state.ResultOf
import com.example.novagincanabiblica.data.repositories.Repository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

open class BaseViewModel(private val repo: Repository) : ViewModel() {

    private val _feedbackMessage = MutableStateFlow<FeedbackMessage>(FeedbackMessage.NoMessage)
    val feedbackMessage = _feedbackMessage.asStateFlow()

    protected val _localSession = MutableStateFlow(Session())
    val localSession = _localSession.asStateFlow()

    private val _day = MutableStateFlow(-1)
    val day = _day.asStateFlow()

    private val _calculatedQuizData = MutableStateFlow(QuestionStatsDataCalculated())
    val calculatedQuizData = _calculatedQuizData.asStateFlow()

    private val _calculatedWordleData = MutableStateFlow(WordleDataCalculated())
    val calculatedWordleData = _calculatedWordleData.asStateFlow()

    init {
        collectDay(onlyOnce = false)
    }

    fun collectDay(onlyOnce: Boolean) = viewModelScope.launch {
        repo.getDay(onlyOnce = onlyOnce).collectLatest {
            it.handleSuccessAndFailure { day ->
                _day.emit(value = day)
                collectSession()
            }
        }
    }

    fun resetErrorMessage() = viewModelScope.launch {
        _feedbackMessage.emit(value = FeedbackMessage.NoMessage)
    }

    private fun collectSession() = viewModelScope.launch {
        repo.getSession().collectLatest {
            it.handleSuccessAndFailure { session ->
                _localSession.emit(value = session)
            }
        }
    }

    suspend fun <T> ResultOf<T>.handleSuccessAndFailure(action: suspend (value: T) -> Unit) =
        when (this) {
            is ResultOf.Success -> action(value)
            is ResultOf.Failure -> {
                emitFeedbackMessage(errorMessage)
                Unit
            }
        }

    fun emitFeedbackMessage(feedbackMessage: FeedbackMessage, isAutoDelete: Boolean = true) = viewModelScope.launch {
        if (_feedbackMessage.value == FeedbackMessage.NoMessage) {
            _feedbackMessage.emit(feedbackMessage)
            if (isAutoDelete) {
                delay(4000)
                _feedbackMessage.emit(FeedbackMessage.NoMessage)
            }
        }
    }

    fun calculateQuizData(session: Session = localSession.value) = viewModelScope.launch {
        val questionData = session.quizStats
        _calculatedQuizData.emit(
            QuestionStatsDataCalculated(
                easyFloat = getAlphaValueToAnimate(
                    questionData.easyCorrect,
                    questionData.easyWrong
                ),
                mediumFloat = getAlphaValueToAnimate(
                    questionData.mediumCorrect,
                    questionData.mediumWrong
                ),
                hardFLoat = getAlphaValueToAnimate(
                    questionData.hardCorrect,
                    questionData.hardWrong
                ),
                impossibleFloat = getAlphaValueToAnimate(
                    questionData.impossibleCorrect,
                    questionData.impossibleWrong
                ),
            ).apply {
                easyInt = easyFloat.times(100).toInt()
                mediumInt = mediumFloat.times(100).toInt()
                hardInt = hardFLoat.times(100).toInt()
                impossibleInt = impossibleFloat.times(100).toInt()
            }
        )
    }

    private fun getAlphaValueToAnimate(correct: Int, wrong: Int): Float = if (itDoesntBreak(
            correct,
            wrong
        )
    ) (correct.toFloat() / (correct + wrong)) else 0f

    //Checks if we are not dividing zero or by zero.
    private fun itDoesntBreak(correct: Int, wrong: Int) = !(correct == 0 || correct + wrong == 0)

    fun calculateWordleData(session: Session = localSession.value) = viewModelScope.launch {
        val wordleData = session.wordle.wordleStats
        val max = wordleData.getMax()
        _calculatedWordleData.emit(
            WordleDataCalculated(
                firstTryFloat = if (checkIfItDoesntBreak(
                        wordleData.winOnFirst,
                        max
                    )
                ) wordleData.winOnFirst.toFloat() / max else 0f,
                secondTryFloat = if (checkIfItDoesntBreak(
                        wordleData.winOnSecond,
                        max
                    )
                ) wordleData.winOnSecond.toFloat() / max else 0f,
                thirdTryFloat = if (checkIfItDoesntBreak(
                        wordleData.winOnThird,
                        max
                    )
                ) wordleData.winOnThird.toFloat() / max else 0f,
                forthTryFloat = if (checkIfItDoesntBreak(
                        wordleData.winOnForth,
                        max
                    )
                ) wordleData.winOnForth.toFloat() / max else 0f,
                firthTryFloat = if (checkIfItDoesntBreak(
                        wordleData.winOnFirth,
                        max
                    )
                ) wordleData.winOnFirth.toFloat() / max else 0f,
                sixthTryFloat = if (checkIfItDoesntBreak(
                        wordleData.winOnSixth,
                        max
                    )
                ) wordleData.winOnSixth.toFloat() / max else 0f,
                lostFloat = if (checkIfItDoesntBreak(
                        wordleData.lost,
                        max
                    )
                ) wordleData.lost.toFloat() / max else 0f,
            )
        )
    }

    private fun checkIfItDoesntBreak(cantBeZeroOne: Int, cantBeZeroTwo: Int) =
        cantBeZeroOne > 0 && cantBeZeroTwo > 0

}