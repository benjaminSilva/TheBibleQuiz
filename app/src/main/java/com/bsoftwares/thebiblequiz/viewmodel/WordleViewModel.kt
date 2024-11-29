package com.bsoftwares.thebiblequiz.viewmodel

import androidx.compose.runtime.mutableStateListOf
import com.bsoftwares.thebiblequiz.data.models.state.FeedbackMessage
import com.bsoftwares.thebiblequiz.data.models.wordle.KeyboardLetter
import com.bsoftwares.thebiblequiz.data.models.wordle.LetterState
import com.bsoftwares.thebiblequiz.data.models.wordle.Wordle
import com.bsoftwares.thebiblequiz.data.models.wordle.WordleAttemptState
import com.bsoftwares.thebiblequiz.data.models.wordle.WordleAttempt
import com.bsoftwares.thebiblequiz.data.models.wordle.generateStartWordleAttemptList
import com.bsoftwares.thebiblequiz.data.models.wordle.initiateKeyboardState
import com.bsoftwares.thebiblequiz.data.repositories.BaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class WordleViewModel @Inject constructor(
    private val repo: BaseRepository
) : BaseViewModel(repo = repo) {

    private val _wordle = MutableStateFlow(Wordle())
    val wordle = _wordle.asStateFlow()

    private val _attempts = MutableStateFlow(generateStartWordleAttemptList())
    val attempts = _attempts.asStateFlow()

    private val _attemptsString = MutableStateFlow("")
    val attemptsString = _attemptsString.asStateFlow()

    private val _navigateToResults = MutableStateFlow(false)
    val navigateToResults = _navigateToResults.asStateFlow()

    private val _listOfKeyboardStates by lazy {
        mutableStateListOf<KeyboardLetter>().apply {
            addAll(initiateKeyboardState())
        }
    }
    val keyboardState = _listOfKeyboardStates

    init {
        initWordle()
    }

    private fun initWordle() = mainScope.launch {
        withContext(Dispatchers.IO) {
            day.collectLatestAndApplyOnMain {
                if (it != -1) {
                    listenToWordle(it)
                    getAttempts()
                }
            }
        }
    }

    private fun getAttempts() = backGroundScope.launch {
        autoCancellable {
            localSession.collectLatest { session ->
                repo.getAttempts(session).collectLatest {
                    withContext(Dispatchers.Main) {
                        it.handleSuccessAndFailure { attemps ->
                            _attempts.emit(attemps)
                            attemps.updateKeyboard()
                        }
                    }
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

    private fun listenToWordle(day: Int) = backGroundScope.launch {
        repo.getWordle(day = day).collectLatest {
            it.handleSuccessAndFailure { wordle ->
                _wordle.emit(wordle)
            }
        }
    }

    fun checkWord() = mainScope.launch {
        if (attemptsString.value == wordle.value.word) {
            handleEndOfGame(userFoundTheWord = true)
            updateAttempts(attemptsString.value)
            return@launch
        }

        if (attemptsString.value.length != wordle.value.word.length) {
            emitFeedbackMessage(FeedbackMessage.WordNotLongEnough(length = wordle.value.word.length))
            return@launch
        }
        if (attemptsString.value.isRepeatedWord(attempts.value)) {
            emitFeedbackMessage(FeedbackMessage.RepeatedWord)
            return@launch
        }

        withContext(Dispatchers.IO) {
            autoCancellable {
                repo.checkWord(attemptsString.value).collectLatestAndApplyOnMain {
                    it.handleSuccessAndFailure { validWord ->
                        updateAttempts(validWord)
                    }
                }
            }
        }
    }

    private fun String.isRepeatedWord(list: List<WordleAttempt>): Boolean =
        list.find { it.word == this } != null


    private fun handleEndOfGame(userFoundTheWord: Boolean = false) = mainScope.launch {
        _wordle.update {
            it.copy(isFinished = true, hasUserFoundTheWord = userFoundTheWord)
        }
        delay(2000)
        _navigateToResults.emit(true)
        withContext(Dispatchers.IO) {
            autoCancellable {
                repo.updateWordleStats(userFoundTheWord, localSession.value, attempts.value)
                    .collectLatestAndApplyOnMain {
                        emitFeedbackMessage(it)
                    }
            }
        }
    }

    private fun updateAttempts(validWord: String) = mainScope.launch {
        _attempts.update {
            it.apply {
                for(i in indices) {
                    val current = get(i)
                    val hasNext = i + 1 < size
                    if (current.attemptState == WordleAttemptState.USER_IS_CURRENTLY_HERE) {
                        current.let { currentWordleAttempt ->
                            currentWordleAttempt.word = validWord
                            currentWordleAttempt.listOfLetterStates = generateLetterStates(validWord)
                            currentWordleAttempt.attemptState = WordleAttemptState.USER_HAS_TRIED
                        }
                        if (hasNext) {
                            get(i+1).attemptState = WordleAttemptState.USER_IS_CURRENTLY_HERE
                        } else {
                            handleEndOfGame()
                        }
                        break
                    }
                }
            }
        }
        _attemptsString.emit("")
        withContext(Dispatchers.IO) {
            autoCancellable {
                repo.updateWordleList(session = localSession.value, attemptList = attempts.value)
                    .collectLatest {
                        emitFeedbackMessage(it)
                    }
            }
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


    fun updateAttemptString(updatedString: String) = mainScope.launch {
        _attemptsString.emit(updatedString)
    }

}
