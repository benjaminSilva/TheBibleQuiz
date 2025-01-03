package com.bsoftwares.thebiblequiz.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bsoftwares.thebiblequiz.data.models.QuestionStatsDataCalculated
import com.bsoftwares.thebiblequiz.data.models.Session
import com.bsoftwares.thebiblequiz.data.models.WordleDataCalculated
import com.bsoftwares.thebiblequiz.data.models.state.ConnectivityStatus
import com.bsoftwares.thebiblequiz.data.models.state.DialogType
import com.bsoftwares.thebiblequiz.data.models.state.FeedbackMessage
import com.bsoftwares.thebiblequiz.data.models.state.ResultOf
import com.bsoftwares.thebiblequiz.data.repositories.BaseRepository
import com.bsoftwares.thebiblequiz.ui.theme.initialValue
import com.bsoftwares.thebiblequiz.ui.theme.jobTimeOut
import com.google.android.gms.ads.interstitial.InterstitialAd
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

open class BaseViewModel(private val repo: BaseRepository, private val initialize: Boolean = true) : ViewModel() {

    private val _feedbackMessage = MutableStateFlow<FeedbackMessage>(FeedbackMessage.NoMessage)
    val feedbackMessage = _feedbackMessage.asStateFlow()

    private val _localSession = MutableStateFlow(Session())
    val localSession = _localSession.asStateFlow()

    private val _signedInUserId = MutableStateFlow(initialValue)
    val signedInUserId = _signedInUserId.asStateFlow()

    private val _day = MutableStateFlow(-1)
    val day = _day.asStateFlow()

    private val _isNewDay = MutableStateFlow(false)
    val isNewDay = _isNewDay.asStateFlow()

    private val _calculatedQuizData = MutableStateFlow(QuestionStatsDataCalculated())
    val calculatedQuizData = _calculatedQuizData.asStateFlow()

    private val _calculatedWordleData = MutableStateFlow(WordleDataCalculated())
    val calculatedWordleData = _calculatedWordleData.asStateFlow()

    private val _displayDialog = MutableStateFlow<DialogType>(DialogType.EmptyValue)
    val displayDialog = _displayDialog.asStateFlow()

    private val _ad = MutableStateFlow<InterstitialAd?>(null)
    val ad = _ad.asStateFlow()

    private val _remainingTimeForNextDay = MutableStateFlow("")
    val remainingTimeForNextDay = _remainingTimeForNextDay.asStateFlow()

    private var dayFlow: Job? = null

    private val viewModelJob by lazy {
        Job()
    }

    protected val backGroundScope by lazy {
        CoroutineScope(Dispatchers.IO + viewModelJob)
    }

    protected val mainScope by lazy {
        CoroutineScope(Dispatchers.Default + viewModelJob)
    }

    private var delayedActinJob: Job? = null

    init {
        startCountdown()
        backGroundScope.launch {
            collectConnectivityStatus()
        }
    }

    private fun collectConnectivityStatus() = backGroundScope.launch {
        repo.getConnectivityStatus().collectLatest {
            when (it) {
                ConnectivityStatus.AVAILABLE -> {
                    _signedInUserId.emit(repo.getSignedInUserId())
                    if (initialize) {
                        dayFlow = collectDay()
                        repo.loadToken()
                    }
                }
                ConnectivityStatus.LOST -> {
                    dayFlow?.cancel()
                }
                ConnectivityStatus.UNAVAILABLE -> {
                    dayFlow?.cancel()
                }
            }
        }
    }

    fun updateDialog(dialogType: DialogType = DialogType.EmptyValue) =
        mainScope.launch {
            _displayDialog.emit(dialogType)
        }

    private fun collectDay() = backGroundScope.launch {
        repo.getDay().collectLatest {
            it.handleSuccessAndFailure { (day, isNewDay) ->
                _day.emit(value = day)
                collectSession()
                if (isNewDay) {
                    _isNewDay.emit(value = isNewDay)
                    delay(500)
                    _isNewDay.emit(false)
                    emitFeedbackMessage(FeedbackMessage.NewDay.apply {
                        extraData = arrayOf(day)
                    })
                }
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

    suspend fun <T> ResultOf<T>.handleSuccessAndFailure(
        failureAction: suspend () -> Unit = {},
        logAction: suspend () -> Unit = {},
        action: suspend (value: T) -> Unit
    ) =
        when (this) {
            is ResultOf.Success -> action(value)
            is ResultOf.Failure -> {
                emitFeedbackMessage(errorMessage)
                failureAction()
            }
            is ResultOf.LogMessage -> {
                Log.i("Bible Quiz App ~ ${reference.message}", errorMessage)
                logAction()
            }
        }

    fun emitFeedbackMessage(
        feedbackMessage: FeedbackMessage,
        isAutoDelete: Boolean = true,
        isFastDelete: Boolean = false
    ) =
        viewModelScope.launch {
            if (_feedbackMessage.value == FeedbackMessage.NoMessage) {
                _feedbackMessage.emit(feedbackMessage)
                if (isAutoDelete) {
                    delay(if (isFastDelete) 50 else 4000)
                    _feedbackMessage.emit(FeedbackMessage.NoMessage)
                }
            }
        }

    fun calculateQuizData(session: Session = localSession.value) = backGroundScope.launch {
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


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun CoroutineScope.autoCancellable(code: suspend () -> Unit) {
        launch {
            withTimeout(jobTimeOut) {
                code()
            }
        }
    }

    suspend fun <T> Flow<T>.collectLatestAndApplyOnMain(action: suspend (value: T) -> Unit) {
        collectLatest {
            action(it)
        }
    }

    fun updateAd(interstitialAd: InterstitialAd) = backGroundScope.launch {
        _ad.emit(interstitialAd)
    }

    private fun startCountdown() = backGroundScope.launch {
        while (isActive) {
            _remainingTimeForNextDay.emit(getTimeRemainingUntil5PMEST())
            delay(1000) // Update every second
        }
    }

    private fun getTimeRemainingUntil5PMEST(): String {
        // Set up EST TimeZone
        val estTimeZone = TimeZone.getTimeZone("America/New_York")
        val now = Calendar.getInstance(estTimeZone)

        // Set target time to 5 PM EST today
        val targetTime = Calendar.getInstance(estTimeZone).apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // If the current time is after 5 PM, move to 5 PM the next day
        if (now.after(targetTime)) {
            targetTime.add(Calendar.DAY_OF_YEAR, 1)
        }

        // Calculate the difference in milliseconds
        val millisUntilTarget = targetTime.timeInMillis - now.timeInMillis

        // Convert to hours, minutes, and seconds
        val hours = millisUntilTarget / (1000 * 60 * 60)
        val minutes = (millisUntilTarget / (1000 * 60)) % 60
        val seconds = (millisUntilTarget / 1000) % 60

        return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
    }

    fun delayedAction(function: () -> Unit) {
        delayedActinJob = backGroundScope.launch {
            delay(1000)
            function()
        }
    }

    fun cancelDelayedAction() {
        delayedActinJob?.cancel()
    }

}