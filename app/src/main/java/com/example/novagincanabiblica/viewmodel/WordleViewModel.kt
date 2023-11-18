package com.example.novagincanabiblica.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.viewModelScope
import com.example.novagincanabiblica.data.models.state.FeedbackMessage
import com.example.novagincanabiblica.data.models.wordle.KeyboardLetter
import com.example.novagincanabiblica.data.models.wordle.LetterState
import com.example.novagincanabiblica.data.models.wordle.Wordle
import com.example.novagincanabiblica.data.models.wordle.WordleAttempState
import com.example.novagincanabiblica.data.models.wordle.WordleAttempt
import com.example.novagincanabiblica.data.models.wordle.generateStartWordleAttemptList
import com.example.novagincanabiblica.data.models.wordle.initiateKeyboardState
import com.example.novagincanabiblica.data.repositories.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WordleViewModel @Inject constructor(
    private val repo: Repository
) : BaseViewModel(repo = repo) {

    private val _wordle = MutableStateFlow(Wordle())
    val wordle = _wordle.asStateFlow()

    private val _attemps = MutableStateFlow(generateStartWordleAttemptList())
    val attemps = _attemps.asStateFlow()

    private val _attempsString = MutableStateFlow("")
    val attempsString = _attempsString.asStateFlow()

    private val _navigateToResults = MutableStateFlow(false)
    val navigateToResults = _navigateToResults.asStateFlow()

    private val _listOfKeyboardStates = mutableStateListOf<KeyboardLetter>()
    val keyboardState = _listOfKeyboardStates

    init {
        collectDay(onlyOnce = true)
        initWordle()
    }

    private fun initWordle() = viewModelScope.launch {
        initiateKeyboardStateList()
        day.collectLatest {
            if (it != -1) {
                listenToWordle(it)
                getAttempts()
            }
        }
    }

    private fun initiateKeyboardStateList() {
        _listOfKeyboardStates.addAll(initiateKeyboardState())
    }

    private fun getAttempts() = viewModelScope.launch {
        localSession.collectLatest { session ->
            repo.getAttemps(session).collectLatest {
                it.handleSuccessAndFailure { attemps ->
                    _attemps.emit(attemps)
                    attemps.updateKeyboard()
                }
            }
        }
    }

    private fun List<WordleAttempt>.updateKeyboard() {
        forEach { wordleAttempt ->
            wordleAttempt.word.forEachIndexed { index, char ->
                char.updateLetterStatus(wordleAttempt.listOfLetterStates[index])
            }
        }
    }

    private fun listenToWordle(day: Int) = viewModelScope.launch {
        repo.getWordle(day = day).collectLatest {
            it.handleSuccessAndFailure { wordle ->
                _wordle.emit(wordle)
            }
        }
    }

    fun checkWord() = viewModelScope.launch {
        if (attempsString.value.length != wordle.value.word.length) {
            emitFeedbackMessage(FeedbackMessage.WordNotLongEnough(length = wordle.value.word.length))
            return@launch
        }
        if (attempsString.value.isRepeatedWord(attemps.value)) {
            emitFeedbackMessage(FeedbackMessage.RepeatedWord)
            return@launch
        }

        if (attempsString.value == wordle.value.word) {
            handleEndOfGame(userFoundTheWord = true)
            updateAttemps(attempsString.value)
            return@launch
        }

        repo.checkWordV2(attempsString.value).collectLatest {
            it.handleSuccessAndFailure { validWord ->
                updateAttemps(validWord)
            }
        }

    }

    private fun String.isRepeatedWord(list: List<WordleAttempt>): Boolean =
        list.find { it.word == this } != null


    private fun handleEndOfGame(userFoundTheWord: Boolean = false) = viewModelScope.launch {
        _wordle.update {
            it.copy(isFinished = true, hasUserFoundTheWord = userFoundTheWord)
        }
        repo.updateWordleStats(userFoundTheWord, localSession.value, attemps.value).collectLatest {
            emitFeedbackMessage(it)
        }
        delay(2000)
        _navigateToResults.emit(true)
    }

    private fun updateAttemps(validWord: String) = viewModelScope.launch {
        _attempsString.emit("")
        _attemps.update {
            it.apply {
                first { attempt ->
                    attempt.attemptState == WordleAttempState.USER_IS_CURRENTLY_HERE
                }.let { currentWordleAttempt ->
                    currentWordleAttempt.word = validWord
                    currentWordleAttempt.listOfLetterStates = generateLetterStates(validWord)
                    currentWordleAttempt.attemptState = WordleAttempState.USER_HAS_TRIED
                }
                firstOrNull { attempt ->
                    attempt.attemptState == WordleAttempState.USER_WILL_STILL_TRY
                }.let { nextWordleAttemp ->
                    if (nextWordleAttemp == null) {
                        handleEndOfGame()
                    } else {
                        nextWordleAttemp.attemptState = WordleAttempState.USER_IS_CURRENTLY_HERE
                    }
                }
            }
        }
        repo.updateWordleList(session = localSession.value, attemptList = attemps.value)
            .collectLatest {
                emitFeedbackMessage(it)
            }
    }

    private fun generateLetterStates(word: String): List<LetterState> {
        var localWordleCopy = wordle.value.word
        return word.mapIndexed { index, letter ->
            val wordleletter = wordle.value.word[index]
            when {
                letter == wordleletter -> {
                    letter.updateLetterStatus(LetterState.LETTER_CORRECT_PLACE)
                    localWordleCopy = localWordleCopy.replaceFirst(letter.toString(), "")
                    LetterState.LETTER_CORRECT_PLACE
                }

                localWordleCopy.contains(letter) && letter.verifyIfThisLetterIsAlreadyCorrect(
                    word,
                    localWordleCopy,
                    index
                ) -> {
                    letter.updateLetterStatus(LetterState.LETTER_WRONG_PLACE)
                    localWordleCopy = localWordleCopy.replaceFirst(letter.toString(), "")
                    LetterState.LETTER_WRONG_PLACE
                }

                else -> {
                    letter.updateLetterStatus(LetterState.LETTER_NOT_IN_WORD)
                    LetterState.LETTER_NOT_IN_WORD
                }
            }
        }
    }

    private fun Char.verifyIfThisLetterIsAlreadyCorrect(
        word: String,
        wordleWord: String,
        index: Int
    ): Boolean {
        if (word.count { it == this } < 2 || wordleWord.count { it == this } > 1) {
            return true
        }
        val correctWordle = wordle.value.word
        for (i in index + 1 until correctWordle.length) {
            if (word[i] == this && correctWordle[i] == this) {
                return false
            }
        }
        return true
    }

    private fun Char.updateLetterStatus(status: LetterState) {
        _listOfKeyboardStates.find { it.letter == this.toString() && (it.letterState == LetterState.LETTER_NOT_CHECKED || status == LetterState.LETTER_CORRECT_PLACE) }?.letterState =
            status
    }


    fun updateAttemptString(updatedString: String) = viewModelScope.launch {
        _attempsString.emit(updatedString)
    }

}
