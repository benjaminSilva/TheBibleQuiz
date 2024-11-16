package com.bsoftwares.thebiblequiz.data.models.wordle

data class Wordle(
    val word: String = "",
    val verse: String = "",
    val reference: String = "",
    val isFinished: Boolean = false,
    val hasUserFoundTheWord: Boolean = false
)

fun initiateKeyboardState() = listOf(
    KeyboardLetter("Q"),
    KeyboardLetter("W"),
    KeyboardLetter("E"),
    KeyboardLetter("R"),
    KeyboardLetter("T"),
    KeyboardLetter("Y"),
    KeyboardLetter("U"),
    KeyboardLetter("I"),
    KeyboardLetter("O"),
    KeyboardLetter("P"),
    KeyboardLetter("A"),
    KeyboardLetter("S"),
    KeyboardLetter("D"),
    KeyboardLetter("F"),
    KeyboardLetter("G"),
    KeyboardLetter("H"),
    KeyboardLetter("J"),
    KeyboardLetter("K"),
    KeyboardLetter("L"),
    KeyboardLetter("Z"),
    KeyboardLetter("X"),
    KeyboardLetter("C"),
    KeyboardLetter("V"),
    KeyboardLetter("B"),
    KeyboardLetter("N"),
    KeyboardLetter("M")
)

data class KeyboardLetter(val letter: String, var letterState: LetterState = LetterState.LETTER_NOT_CHECKED)