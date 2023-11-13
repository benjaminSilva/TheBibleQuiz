package com.example.novagincanabiblica.ui.screens.games.wordle

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.novagincanabiblica.R
import com.example.novagincanabiblica.data.models.wordle.KeyboardLetter
import com.example.novagincanabiblica.data.models.wordle.WordleAttempState
import com.example.novagincanabiblica.data.models.wordle.WordleAttempt
import com.example.novagincanabiblica.data.models.wordle.generateStartWordleAttemptList
import com.example.novagincanabiblica.data.models.wordle.initiateKeyboardState
import com.example.novagincanabiblica.ui.basicviews.AutoResizeText
import com.example.novagincanabiblica.ui.basicviews.BasicText
import com.example.novagincanabiblica.ui.basicviews.ErrorMessage
import com.example.novagincanabiblica.ui.basicviews.FontSizeRange
import com.example.novagincanabiblica.ui.basicviews.ShakeConfig
import com.example.novagincanabiblica.ui.basicviews.animateAlpha
import com.example.novagincanabiblica.ui.basicviews.animateColor
import com.example.novagincanabiblica.ui.basicviews.rememberShakeController
import com.example.novagincanabiblica.ui.basicviews.shake
import com.example.novagincanabiblica.ui.navigation.navigateWithoutRemembering
import com.example.novagincanabiblica.ui.screens.Routes
import com.example.novagincanabiblica.ui.theme.NovaGincanaBiblicaTheme
import com.example.novagincanabiblica.ui.theme.achivoFontFamily
import com.example.novagincanabiblica.ui.theme.almostWhite
import com.example.novagincanabiblica.ui.theme.closeToBlack
import com.example.novagincanabiblica.ui.theme.lessWhite
import com.example.novagincanabiblica.ui.theme.zillasFontFamily
import com.example.novagincanabiblica.viewmodel.WordleViewModel
import kotlinx.coroutines.delay

@Composable
fun InitializeWordleScreen(navController: NavHostController, viewModel: WordleViewModel) {
    val wordle by viewModel.wordle.collectAsStateWithLifecycle()
    val navigate by viewModel.navigateToResults.collectAsStateWithLifecycle()
    val attempts by viewModel.attemps.collectAsStateWithLifecycle()
    val attempt by viewModel.attempsString.collectAsStateWithLifecycle()
    val listKeyBoardState = viewModel.keyboardState
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    LaunchedEffect(navigate) {
        if (navigate) {
            navController.navigateWithoutRemembering(
                route = Routes.WordleResults,
                baseRoute = Routes.WordleMode
            )
        }
    }

    WordleScreen(
        wordleWord = wordle.word,
        attempt = attempt,
        listWordleAttemps = attempts,
        listOfKeyboardStates = listKeyBoardState,
        errorMessage = errorMessage,
        resetErrorMessage = {
            viewModel.resetErrorMessage()
        },
        updateAttemptString = {
            viewModel.updateAttemptString(it)
        }) {
        viewModel.checkWord()
    }
}

@Composable
fun WordleScreen(
    wordleWord: String,
    attempt: String,
    listWordleAttemps: List<WordleAttempt>,
    listOfKeyboardStates: List<KeyboardLetter>,
    errorMessage: String,
    resetErrorMessage: () -> Unit,
    updateAttemptString: (String) -> Unit,
    checkWord: () -> Unit
) {

    var startPage by remember {
        mutableStateOf(true)
    }

    val alphaAnimation by animateAlpha(condition = startPage)

    LaunchedEffect(Unit) {
        startPage = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .alpha(alphaAnimation),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(0.75f)
        ) {

            if (errorMessage.isNotEmpty()) {
                ErrorMessage(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(16.dp),
                    errorMessage = errorMessage
                )
            }
            Column(
                modifier = Modifier
                    .padding(vertical = 16.dp, horizontal = 32.dp)
                    .align(Alignment.Center),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BasicText(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = "Biblical Wordle",
                    fontSize = 32
                )
                WordleRows(
                    wordleWord = wordleWord,
                    attempt = attempt,
                    listWordleAttemps = listWordleAttemps,
                    errorMessage = errorMessage,
                    resetErrorMessage = resetErrorMessage
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(0.25f)
                .padding(bottom = 8.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f), horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    LetterButton(
                        modifier = Modifier.weight(1f),
                        letter = "Q",
                        listOfKeyboardStates[0]
                    ) {
                        if (attempt.length < wordleWord.length) {
                            updateAttemptString(attempt.plus(it))
                        }
                    }
                    LetterButton(
                        modifier = Modifier.weight(1f),
                        letter = "W",
                        listOfKeyboardStates[1]
                    ) {
                        if (attempt.length < wordleWord.length) {
                            updateAttemptString(attempt.plus(it))
                        }
                    }
                    LetterButton(
                        modifier = Modifier.weight(1f),
                        letter = "E",
                        listOfKeyboardStates[2]
                    ) {
                        if (attempt.length < wordleWord.length) {
                            updateAttemptString(attempt.plus(it))
                        }
                    }
                    LetterButton(
                        modifier = Modifier.weight(1f),
                        letter = "R",
                        listOfKeyboardStates[3]
                    ) { it ->
                        if (attempt.length < wordleWord.length) {
                            updateAttemptString(attempt.plus(it))
                        }
                    }
                    LetterButton(
                        modifier = Modifier.weight(1f),
                        letter = "T",
                        listOfKeyboardStates[4]
                    ) {
                        if (attempt.length < wordleWord.length) {
                            updateAttemptString(attempt.plus(it))
                        }
                    }
                    LetterButton(
                        modifier = Modifier.weight(1f),
                        letter = "Y",
                        listOfKeyboardStates[5]
                    ) {
                        if (attempt.length < wordleWord.length) {
                            updateAttemptString(attempt.plus(it))
                        }
                    }
                    LetterButton(
                        modifier = Modifier.weight(1f),
                        letter = "U",
                        listOfKeyboardStates[6]
                    ) {
                        if (attempt.length < wordleWord.length) {
                            updateAttemptString(attempt.plus(it))
                        }
                    }
                    LetterButton(
                        modifier = Modifier.weight(1f),
                        letter = "I",
                        listOfKeyboardStates[7]
                    ) {
                        if (attempt.length < wordleWord.length) {
                            updateAttemptString(attempt.plus(it))
                        }
                    }
                    LetterButton(
                        modifier = Modifier.weight(1f),
                        letter = "O",
                        listOfKeyboardStates[8]
                    ) {
                        if (attempt.length < wordleWord.length) {
                            updateAttemptString(attempt.plus(it))
                        }
                    }
                    LetterButton(
                        modifier = Modifier.weight(1f),
                        letter = "P",
                        listOfKeyboardStates[9]
                    ) {
                        if (attempt.length < wordleWord.length) {
                            updateAttemptString(attempt.plus(it))
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f), horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    LetterButton(
                        modifier = Modifier.weight(1f),
                        letter = "A",
                        listOfKeyboardStates[10]
                    ) {
                        if (attempt.length < wordleWord.length) {
                            updateAttemptString(attempt.plus(it))
                        }
                    }
                    LetterButton(
                        modifier = Modifier.weight(1f),
                        letter = "S",
                        listOfKeyboardStates[11]
                    ) {
                        if (attempt.length < wordleWord.length) {
                            updateAttemptString(attempt.plus(it))
                        }
                    }
                    LetterButton(
                        modifier = Modifier.weight(1f),
                        letter = "D",
                        listOfKeyboardStates[12]
                    ) {
                        if (attempt.length < wordleWord.length) {
                            updateAttemptString(attempt.plus(it))
                        }
                    }
                    LetterButton(
                        modifier = Modifier.weight(1f),
                        letter = "F",
                        listOfKeyboardStates[13]
                    ) {
                        if (attempt.length < wordleWord.length) {
                            updateAttemptString(attempt.plus(it))
                        }
                    }
                    LetterButton(
                        modifier = Modifier.weight(1f),
                        letter = "G",
                        listOfKeyboardStates[14]
                    ) {
                        if (attempt.length < wordleWord.length) {
                            updateAttemptString(attempt.plus(it))
                        }
                    }
                    LetterButton(
                        modifier = Modifier.weight(1f),
                        letter = "H",
                        listOfKeyboardStates[15]
                    ) {
                        if (attempt.length < wordleWord.length) {
                            updateAttemptString(attempt.plus(it))
                        }
                    }
                    LetterButton(
                        modifier = Modifier.weight(1f),
                        letter = "J",
                        listOfKeyboardStates[16]
                    ) {
                        if (attempt.length < wordleWord.length) {
                            updateAttemptString(attempt.plus(it))
                        }
                    }
                    LetterButton(
                        modifier = Modifier.weight(1f),
                        letter = "K",
                        listOfKeyboardStates[17]
                    ) {
                        if (attempt.length < wordleWord.length) {
                            updateAttemptString(attempt.plus(it))
                        }
                    }
                    LetterButton(
                        modifier = Modifier.weight(1f),
                        letter = "L",
                        listOfKeyboardStates[18]
                    ) {
                        if (attempt.length < wordleWord.length) {
                            updateAttemptString(attempt.plus(it))
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f), horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .weight(1.5f)
                        .fillMaxHeight()
                        .clip(
                            RoundedCornerShape(8.dp)
                        )
                        .background(lessWhite)
                        .clickable {
                            checkWord()
                        }) {
                        AutoResizeText(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(8.dp),
                            text = "ENTER",
                            fontFamily = zillasFontFamily,
                            maxLines = 1,
                            fontSizeRange = FontSizeRange(1.sp, 32.sp)
                        )
                    }
                    LetterButton(
                        modifier = Modifier.weight(1f),
                        letter = "Z",
                        listOfKeyboardStates[19]
                    ) {
                        if (attempt.length < wordleWord.length) {
                            updateAttemptString(attempt.plus(it))
                        }
                    }
                    LetterButton(
                        modifier = Modifier.weight(1f),
                        letter = "X",
                        listOfKeyboardStates[20]
                    ) {
                        if (attempt.length < wordleWord.length) {
                            updateAttemptString(attempt.plus(it))
                        }
                    }
                    LetterButton(
                        modifier = Modifier.weight(1f),
                        letter = "C",
                        listOfKeyboardStates[21]
                    ) {
                        if (attempt.length < wordleWord.length) {
                            updateAttemptString(attempt.plus(it))
                        }
                    }
                    LetterButton(
                        modifier = Modifier.weight(1f),
                        letter = "V",
                        listOfKeyboardStates[22]
                    ) {
                        if (attempt.length < wordleWord.length) {
                            updateAttemptString(attempt.plus(it))
                        }
                    }
                    LetterButton(
                        modifier = Modifier.weight(1f),
                        letter = "B",
                        listOfKeyboardStates[23]
                    ) {
                        if (attempt.length < wordleWord.length) {
                            updateAttemptString(attempt.plus(it))
                        }
                    }
                    LetterButton(
                        modifier = Modifier.weight(1f),
                        letter = "N",
                        listOfKeyboardStates[24]
                    ) {
                        if (attempt.length < wordleWord.length) {
                            updateAttemptString(attempt.plus(it))
                        }
                    }
                    LetterButton(
                        modifier = Modifier.weight(1f),
                        letter = "M",
                        listOfKeyboardStates[25]
                    ) {
                        if (attempt.length < wordleWord.length) {
                            updateAttemptString(attempt.plus(it))
                        }
                    }
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .weight(1.3f)
                        .fillMaxHeight()
                        .clip(
                            RoundedCornerShape(8.dp)
                        )
                        .background(lessWhite)
                        .clickable {
                            updateAttemptString(attempt.dropLast(1))
                        }) {
                        Image(
                            modifier = Modifier.align(Alignment.Center),
                            painter = painterResource(id = R.drawable.backspace_24px),
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WordleRows(
    wordleWord: String,
    attempt: String,
    listWordleAttemps: List<WordleAttempt>,
    errorMessage: String,
    resetErrorMessage: () -> Unit,
    isFromResults: Boolean = false
) {
    RowLetterWordle(
        wordleWord = wordleWord,
        attempt = listWordleAttemps[0].run {
            when (attemptState) {
                WordleAttempState.USER_IS_CURRENTLY_HERE -> attempt
                WordleAttempState.USER_HAS_TRIED -> word
                WordleAttempState.USER_WILL_STILL_TRY -> ""
            }
        },
        letterStates = listWordleAttemps[0],
        errorMessage = errorMessage,
        isFromResults = isFromResults,
        resetErrorMessage = resetErrorMessage
    )

    if (isFromResults) {
        if (listWordleAttemps[1].attemptState == WordleAttempState.USER_HAS_TRIED) {
            RowLetterWordle(
                wordleWord = wordleWord,
                attempt = listWordleAttemps[1].run {
                    when (attemptState) {
                        WordleAttempState.USER_IS_CURRENTLY_HERE -> attempt
                        WordleAttempState.USER_HAS_TRIED -> word
                        WordleAttempState.USER_WILL_STILL_TRY -> ""
                    }
                },
                letterStates = listWordleAttemps[1],
                errorMessage = errorMessage,
                isFromResults = isFromResults,
                resetErrorMessage = resetErrorMessage
            )
        }

        if (listWordleAttemps[2].attemptState == WordleAttempState.USER_HAS_TRIED) {
            RowLetterWordle(
                wordleWord = wordleWord,
                attempt = listWordleAttemps[2].run {
                    when (attemptState) {
                        WordleAttempState.USER_IS_CURRENTLY_HERE -> attempt
                        WordleAttempState.USER_HAS_TRIED -> word
                        WordleAttempState.USER_WILL_STILL_TRY -> ""
                    }
                },
                letterStates = listWordleAttemps[2],
                errorMessage = errorMessage,
                isFromResults = isFromResults,
                resetErrorMessage = resetErrorMessage
            )
        }

        if (listWordleAttemps[3].attemptState == WordleAttempState.USER_HAS_TRIED) {
            RowLetterWordle(
                wordleWord = wordleWord,
                attempt = listWordleAttemps[3].run {
                    when (attemptState) {
                        WordleAttempState.USER_IS_CURRENTLY_HERE -> attempt
                        WordleAttempState.USER_HAS_TRIED -> word
                        WordleAttempState.USER_WILL_STILL_TRY -> ""
                    }
                },
                letterStates = listWordleAttemps[3],
                errorMessage = errorMessage,
                isFromResults = isFromResults,
                resetErrorMessage = resetErrorMessage
            )
        }

        if (listWordleAttemps[4].attemptState == WordleAttempState.USER_HAS_TRIED) {
            RowLetterWordle(
                wordleWord = wordleWord,
                attempt = listWordleAttemps[4].run {
                    when (attemptState) {
                        WordleAttempState.USER_IS_CURRENTLY_HERE -> attempt
                        WordleAttempState.USER_HAS_TRIED -> word
                        WordleAttempState.USER_WILL_STILL_TRY -> ""
                    }
                },
                letterStates = listWordleAttemps[4],
                errorMessage = errorMessage,
                isFromResults = isFromResults,
                resetErrorMessage = resetErrorMessage
            )
        }

        if (listWordleAttemps[5].attemptState == WordleAttempState.USER_HAS_TRIED) {
            RowLetterWordle(
                wordleWord = wordleWord,
                attempt = listWordleAttemps[5].run {
                    when (attemptState) {
                        WordleAttempState.USER_IS_CURRENTLY_HERE -> attempt
                        WordleAttempState.USER_HAS_TRIED -> word
                        WordleAttempState.USER_WILL_STILL_TRY -> ""
                    }
                },
                letterStates = listWordleAttemps[5],
                errorMessage = errorMessage,
                isFromResults = isFromResults,
                resetErrorMessage = resetErrorMessage
            )
        }
    } else {
        RowLetterWordle(
            wordleWord = wordleWord,
            attempt = listWordleAttemps[1].run {
                when (attemptState) {
                    WordleAttempState.USER_IS_CURRENTLY_HERE -> attempt
                    WordleAttempState.USER_HAS_TRIED -> word
                    WordleAttempState.USER_WILL_STILL_TRY -> ""
                }
            },
            letterStates = listWordleAttemps[1],
            errorMessage = errorMessage,
            isFromResults = isFromResults,
            resetErrorMessage = resetErrorMessage
        )

        RowLetterWordle(
            wordleWord = wordleWord,
            attempt = listWordleAttemps[2].run {
                when (attemptState) {
                    WordleAttempState.USER_IS_CURRENTLY_HERE -> attempt
                    WordleAttempState.USER_HAS_TRIED -> word
                    WordleAttempState.USER_WILL_STILL_TRY -> ""
                }
            },
            letterStates = listWordleAttemps[2],
            errorMessage = errorMessage,
            isFromResults = isFromResults,
            resetErrorMessage = resetErrorMessage
        )

        RowLetterWordle(
            wordleWord = wordleWord,
            attempt = listWordleAttemps[3].run {
                when (attemptState) {
                    WordleAttempState.USER_IS_CURRENTLY_HERE -> attempt
                    WordleAttempState.USER_HAS_TRIED -> word
                    WordleAttempState.USER_WILL_STILL_TRY -> ""
                }
            },
            letterStates = listWordleAttemps[3],
            errorMessage = errorMessage,
            isFromResults = isFromResults,
            resetErrorMessage = resetErrorMessage
        )

        RowLetterWordle(
            wordleWord = wordleWord,
            attempt = listWordleAttemps[4].run {
                when (attemptState) {
                    WordleAttempState.USER_IS_CURRENTLY_HERE -> attempt
                    WordleAttempState.USER_HAS_TRIED -> word
                    WordleAttempState.USER_WILL_STILL_TRY -> ""
                }
            },
            letterStates = listWordleAttemps[4],
            errorMessage = errorMessage,
            isFromResults = isFromResults,
            resetErrorMessage = resetErrorMessage
        )

        RowLetterWordle(
            wordleWord = wordleWord,
            attempt = listWordleAttemps[5].run {
                when (attemptState) {
                    WordleAttempState.USER_IS_CURRENTLY_HERE -> attempt
                    WordleAttempState.USER_HAS_TRIED -> word
                    WordleAttempState.USER_WILL_STILL_TRY -> ""
                }
            },
            letterStates = listWordleAttemps[5],
            errorMessage = errorMessage,
            isFromResults = isFromResults,
            resetErrorMessage = resetErrorMessage
        )
    }
}

@Composable
fun RowLetterWordle(
    wordleWord: String,
    attempt: String,
    letterStates: WordleAttempt,
    errorMessage: String,
    isFromResults: Boolean = false,
    resetErrorMessage: () -> Unit = {}
) {

    var startLocalWordAnimation by remember {
        mutableStateOf(true)
    }

    val shakeController = rememberShakeController()

    LaunchedEffect(attempt) {
        if (attempt.isNotEmpty())
            startLocalWordAnimation = false
    }

    LaunchedEffect(errorMessage) {
        if ((errorMessage == "Word is not in our list." || errorMessage == "You have tried this word before" || errorMessage == "It has to be at least ${wordleWord.length} letters") && letterStates.attemptState == WordleAttempState.USER_IS_CURRENTLY_HERE) {
            shakeController.shake(
                ShakeConfig(
                    iterations = 4,
                    intensity = 4_000f,
                    rotateY = 5f,
                    translateX = 20f,
                )
            )
        }
        delay(5000)
        resetErrorMessage()
    }

    LaunchedEffect(letterStates.listOfLetterStates) {
        if (wordleWord == attempt && !isFromResults && letterStates.attemptState == WordleAttempState.USER_HAS_TRIED) {
            shakeController.shake(
                ShakeConfig(
                    iterations = 4,
                    intensity = 1_000f,
                    rotateX = -20f,
                    translateY = 20f,
                    trigger = 300
                )
            )
        }
    }

    val animateColorLetter1 by animateColor(
        condition = startLocalWordAnimation,
        startValue = almostWhite,
        endValue = letterStates.listOfLetterStates[0],
    )
    val animateColorLetter2 by animateColor(
        condition = startLocalWordAnimation,
        startValue = almostWhite,
        endValue = letterStates.listOfLetterStates[1],
        delay = 300
    )
    val animateColorLetter3 by animateColor(
        condition = startLocalWordAnimation,
        startValue = almostWhite,
        endValue = letterStates.listOfLetterStates[2],
        delay = 500
    )
    val animateColorLetter4 by animateColor(
        condition = startLocalWordAnimation,
        startValue = almostWhite,
        endValue = letterStates.listOfLetterStates[3],
        delay = 700
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shake(shakeController)
            .border(2.dp, closeToBlack, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(closeToBlack),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        WordleLetter(modifier = Modifier.weight(1f), animateColorLetter1) {
            if (attempt.isNotEmpty()) {
                AutoResizeText(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(8.dp),
                    text = attempt[0].toString(),
                    fontSizeRange = FontSizeRange(1.sp, 24.sp),
                    fontFamily = achivoFontFamily
                )
            } else {
                Spacer(modifier = Modifier.size(0.dp))
            }
        }
        WordleLetter(modifier = Modifier.weight(1f), animateColorLetter2) {
            if (attempt.length > 1) {
                AutoResizeText(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(8.dp),
                    text = attempt[1].toString(),
                    fontSizeRange = FontSizeRange(1.sp, 24.sp),
                    fontFamily = achivoFontFamily
                )

            } else {
                Spacer(modifier = Modifier.size(0.dp))
            }
        }
        WordleLetter(modifier = Modifier.weight(1f), animateColorLetter3) {
            if (attempt.length > 2) {
                AutoResizeText(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(8.dp),
                    text = attempt[2].toString(),
                    fontSizeRange = FontSizeRange(1.sp, 24.sp),
                    fontFamily = achivoFontFamily
                )

            } else {
                Spacer(modifier = Modifier.size(0.dp))
            }
        }
        WordleLetter(modifier = Modifier.weight(1f), animateColorLetter4) {
            if (attempt.length > 3) {
                AutoResizeText(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(8.dp),
                    text = attempt[3].toString(),
                    fontSizeRange = FontSizeRange(1.sp, 24.sp),
                    fontFamily = achivoFontFamily
                )

            } else {
                Spacer(modifier = Modifier.size(0.dp))
            }
        }
        if (wordleWord.length > 4) {
            val animateColorLetter5 by animateColor(
                condition = startLocalWordAnimation,
                startValue = almostWhite,
                endValue = letterStates.listOfLetterStates[4],
                delay = 900
            )
            WordleLetter(modifier = Modifier.weight(1f), animateColorLetter5) {
                if (attempt.length > 4) {
                    AutoResizeText(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(8.dp),
                        text = attempt[4].toString(),
                        fontSizeRange = FontSizeRange(1.sp, 24.sp),
                        fontFamily = achivoFontFamily
                    )
                } else {
                    Spacer(modifier = Modifier.size(0.dp))
                }
            }
        }
        if (wordleWord.length > 5) {
            val animateColorLetter6 by animateColor(
                condition = startLocalWordAnimation,
                startValue = almostWhite,
                endValue = letterStates.listOfLetterStates[5],
                delay = 1100
            )
            WordleLetter(modifier = Modifier.weight(1f), animateColorLetter6) {
                if (attempt.length > 5) {
                    AutoResizeText(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(8.dp),
                        text = attempt[5].toString(),
                        fontSizeRange = FontSizeRange(1.sp, 24.sp),
                        fontFamily = achivoFontFamily
                    )

                } else {
                    Spacer(modifier = Modifier.size(0.dp))
                }
            }
        }
        if (wordleWord.length > 6) {
            val animateColorLetter7 by animateColor(
                condition = startLocalWordAnimation,
                startValue = almostWhite,
                endValue = letterStates.listOfLetterStates[6],
                delay = 1300
            )
            WordleLetter(modifier = Modifier.weight(1f), animateColorLetter7) {
                if (attempt.length > 6) {
                    AutoResizeText(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(8.dp),
                        text = attempt[6].toString(),
                        fontSizeRange = FontSizeRange(1.sp, 24.sp),
                        fontFamily = achivoFontFamily
                    )
                } else {
                    Spacer(modifier = Modifier.size(0.dp))
                }
            }
        }
        if (wordleWord.length > 7) {
            val animateColorLetter7 by animateColor(
                condition = startLocalWordAnimation,
                startValue = almostWhite,
                endValue = letterStates.listOfLetterStates[7],
                delay = 1300
            )
            WordleLetter(modifier = Modifier.weight(1f), animateColorLetter7) {
                if (attempt.length > 7) {
                    AutoResizeText(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(8.dp),
                        text = attempt[7].toString(),
                        fontSizeRange = FontSizeRange(1.sp, 24.sp),
                        fontFamily = achivoFontFamily
                    )
                } else {
                    Spacer(modifier = Modifier.size(0.dp))
                }
            }
        }
    }

}

@Composable
fun WordleLetter(
    modifier: Modifier = Modifier,
    color: Color,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .heightIn(max = 68.dp)
            .aspectRatio(1f)
            .background(
                color
            )
    ) {
        content()
    }
}

@Composable
fun LetterButton(
    modifier: Modifier,
    letter: String,
    state: KeyboardLetter,
    letterClick: (String) -> Unit
) {

    var startAnimation by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(state) {
        startAnimation = false
    }

    val animateColor by animateColor(
        condition = startAnimation,
        startValue = lessWhite,
        endValue = state.letterState,
        delay = 1000
    )

    Box(modifier = modifier
        .fillMaxHeight()
        .fillMaxWidth()
        .clip(
            RoundedCornerShape(8.dp)
        )
        .background(animateColor)
        .clickable {
            letterClick(letter)
        }) {
        BasicText(modifier = Modifier.align(Alignment.Center), text = letter)
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewWordle() {
    NovaGincanaBiblicaTheme {
        WordleScreen(
            wordleWord = "Teste",
            "MONKE",
            listWordleAttemps = generateStartWordleAttemptList(),
            listOfKeyboardStates = initiateKeyboardState(),
            "Test error",
            {},
            {}) {

        }
    }
}