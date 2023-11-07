package com.example.novagincanabiblica.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.viewModelScope
import com.example.novagincanabiblica.data.models.wordle.KeyboardLetter
import com.example.novagincanabiblica.data.models.wordle.LetterState
import com.example.novagincanabiblica.data.models.wordle.Wordle
import com.example.novagincanabiblica.data.models.wordle.WordleAttempState
import com.example.novagincanabiblica.data.models.wordle.WordleAttempts
import com.example.novagincanabiblica.data.models.wordle.initiateKeyboardState
import com.example.novagincanabiblica.data.repositories.SoloModeRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WordleViewModel @Inject constructor(
    private val repo: SoloModeRepo
) : BaseViewModel(repo = repo) {

    private val _wordle = MutableStateFlow(Wordle())
    val wordle = _wordle.asStateFlow()

    private val _attemps = MutableStateFlow(WordleAttempts())
    val attemps = _attemps.asStateFlow()

    private val _attempsString = MutableStateFlow("")
    val attempsString = _attempsString.asStateFlow()

    private val _navigateToResults = MutableStateFlow(false)
    val navigateToResults = _navigateToResults.asStateFlow()

    private val _listOfKeyboardStates = mutableStateListOf<KeyboardLetter>()
    val keyboardState = _listOfKeyboardStates

    private val _startWordAnimation = MutableStateFlow(true)
    val startWordAnimation = _startWordAnimation.asStateFlow()

    init {
        initiateKeyboardStateList()
        getDay()
    }

    private fun initiateKeyboardStateList() {
        _listOfKeyboardStates.addAll(initiateKeyboardState())
    }

    private fun getDay() = viewModelScope.launch {
        repo.getDay().collectLatest { day ->
            day.handleSuccessAndFailure {
                listenToWordle(it)
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
        if (attempsString.value.length == wordle.value.word.length) {
            repo.checkWord(attempsString.value).collectLatest {
                it.handleSuccessAndFailure { validWord ->
                    if (wordle.value.word == validWord) {
                        updateAttemps(validWord)
                        handleEndOfGame(userFoundTheWord = true)
                    } else {
                        updateAttemps(validWord)
                    }
                }
            }
        }
    }

    private fun handleEndOfGame(userFoundTheWord: Boolean = false) = viewModelScope.launch {
        _wordle.update {
            it.copy(isFinished = true, hasUserFoundTheWord = userFoundTheWord)
        }
        delay(2000)
        _navigateToResults.emit(true)
        //TODO update online and on sharedPref
    }

    private fun updateAttemps(validWord: String) = viewModelScope.launch {
        _startWordAnimation.emit(true)
        _attempsString.emit("")
        _attemps.update {
            it.copy(listOfAttempts = it.listOfAttempts.apply {
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
            })
        }
    }

    private fun generateLetterStates(word: String): List<LetterState> {
        var localWordleCopy = wordle.value.word
        return word.mapIndexed { index, letter ->
            val wordleletter = wordle.value.word[index]
            when {
                letter == wordleletter -> {
                    letter.updateLetterStatus(LetterState.LETTER_CORRECT_PLACE)
                    localWordleCopy = localWordleCopy.replaceFirst(letter.toString(),"")
                    LetterState.LETTER_CORRECT_PLACE
                }
                localWordleCopy.contains(letter) && letter.verifyIfThisLetterIsAlreadyCorrect(word, localWordleCopy, index) -> {
                    letter.updateLetterStatus(LetterState.LETTER_WRONG_PLACE)
                    localWordleCopy = localWordleCopy.replaceFirst(letter.toString(),"")
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
        for (i in index + 1 until wordleWord.length) {
            if (word[i] == this && wordleWord[i] == this) {
                return false
            }
        }
        return true
    }

    private fun Char.updateLetterStatus(status: LetterState) {
        _listOfKeyboardStates.find { it.letter == this.toString() && it.letterState == LetterState.LETTER_NOT_CHECKED}?.letterState = status
    }


    fun updateAttemptString(updatedString: String) = viewModelScope.launch {
        _attempsString.emit(updatedString)
    }

}