package com.bsoftwares.thebiblequiz.data.models.wordle

fun generateStartWordleAttemptList(): List<WordleAttempt> = listOf(
    WordleAttempt(
        word = "",
        attemptState = WordleAttemptState.USER_IS_CURRENTLY_HERE
    ),
    WordleAttempt(
        word = "",
        attemptState = WordleAttemptState.USER_WILL_STILL_TRY
    ),
    WordleAttempt(
        word = "",
        attemptState = WordleAttemptState.USER_WILL_STILL_TRY
    ),
    WordleAttempt(
        word = "",
        attemptState = WordleAttemptState.USER_WILL_STILL_TRY
    ),
    WordleAttempt(
        word = "",
        attemptState = WordleAttemptState.USER_WILL_STILL_TRY
    ),
    WordleAttempt(
        word = "",
        attemptState = WordleAttemptState.USER_WILL_STILL_TRY
    )
)

data class WordleAttempt(
    var word: String = "",
    var attemptState: WordleAttemptState = WordleAttemptState.USER_WILL_STILL_TRY,
    var listOfLetterStates: List<LetterState> = listOf(
        LetterState.LETTER_NOT_CHECKED,
        LetterState.LETTER_NOT_CHECKED,
        LetterState.LETTER_NOT_CHECKED,
        LetterState.LETTER_NOT_CHECKED,
        LetterState.LETTER_NOT_CHECKED,
        LetterState.LETTER_NOT_CHECKED,
        LetterState.LETTER_NOT_CHECKED,
        LetterState.LETTER_NOT_CHECKED
    )
)

enum class LetterState {
    LETTER_CORRECT_PLACE,
    LETTER_NOT_IN_WORD,
    LETTER_WRONG_PLACE,
    LETTER_NOT_CHECKED
}

enum class WordleAttemptState {
    USER_IS_CURRENTLY_HERE,
    USER_HAS_TRIED,
    USER_WILL_STILL_TRY
}