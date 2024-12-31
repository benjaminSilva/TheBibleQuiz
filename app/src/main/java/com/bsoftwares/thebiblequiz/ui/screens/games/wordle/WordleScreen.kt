package com.bsoftwares.thebiblequiz.ui.screens.games.wordle

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.bsoftwares.thebiblequiz.R
import com.bsoftwares.thebiblequiz.data.models.state.FeedbackMessage
import com.bsoftwares.thebiblequiz.data.models.state.WordleDialogType
import com.bsoftwares.thebiblequiz.data.models.wordle.KeyboardLetter
import com.bsoftwares.thebiblequiz.data.models.wordle.WordleAttempt
import com.bsoftwares.thebiblequiz.data.models.wordle.WordleAttemptState
import com.bsoftwares.thebiblequiz.data.models.wordle.generateStartWordleAttemptList
import com.bsoftwares.thebiblequiz.data.models.wordle.initiateKeyboardState
import com.bsoftwares.thebiblequiz.ui.basicviews.AutoResizeText
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicContainer
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicDialog
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicScreenBox
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicText
import com.bsoftwares.thebiblequiz.ui.basicviews.FlipCard
import com.bsoftwares.thebiblequiz.ui.basicviews.FontSizeRange
import com.bsoftwares.thebiblequiz.ui.basicviews.ShakeConfig
import com.bsoftwares.thebiblequiz.ui.basicviews.animateColor
import com.bsoftwares.thebiblequiz.ui.basicviews.rememberShakeController
import com.bsoftwares.thebiblequiz.ui.basicviews.shake
import com.bsoftwares.thebiblequiz.ui.navigation.navigateWithoutRemembering
import com.bsoftwares.thebiblequiz.ui.screens.Routes
import com.bsoftwares.thebiblequiz.ui.screens.games.quiz.HowToPlayDialog
import com.bsoftwares.thebiblequiz.ui.theme.NovaGincanaBiblicaTheme
import com.bsoftwares.thebiblequiz.ui.theme.achivoFontFamily
import com.bsoftwares.thebiblequiz.ui.theme.almostBlack
import com.bsoftwares.thebiblequiz.ui.theme.basicContainerClean
import com.bsoftwares.thebiblequiz.ui.theme.contrastColor
import com.bsoftwares.thebiblequiz.ui.theme.zillasFontFamily
import com.bsoftwares.thebiblequiz.viewmodel.WordleViewModel

@Composable
fun InitializeWordleScreen(navController: NavHostController, viewModel: WordleViewModel) {
    val wordle by viewModel.wordle.collectAsStateWithLifecycle()
    val navigate by viewModel.navigateToResults.collectAsStateWithLifecycle()
    val attempts by viewModel.attempts.collectAsStateWithLifecycle()
    val attempt by viewModel.attemptsString.collectAsStateWithLifecycle()
    val listKeyBoardState = viewModel.keyboardState
    val feedbackMessage by viewModel.feedbackMessage.collectAsStateWithLifecycle()
    val session by viewModel.localSession.collectAsStateWithLifecycle()
    val isNewDay by viewModel.isNewDay.collectAsStateWithLifecycle()
    val dialog by viewModel.displayDialog.collectAsStateWithLifecycle()

    when (dialog) {
        is WordleDialogType.HowToPlay -> {
            BasicDialog({
                viewModel.updateDialog()
            }) {
                HowToPlayDialog(
                    stringResource(R.string.how_to_play_wordle),
                    stringResource(R.string.word_puzzle_rules)
                )
            }
        }

        else -> Unit
    }

    LaunchedEffect(isNewDay) {
        if (isNewDay) {
            navController.popBackStack()
        }
    }

    LaunchedEffect(navigate) {
        if (navigate) {
            if (!session.premium && session.hasPlayedQuizGame) {
                navController.navigateWithoutRemembering(
                    route = Routes.AdScreen,
                    baseRoute = Routes.WordleMode
                )
            } else {
                navController.navigateWithoutRemembering(
                    route = Routes.WordleResults,
                    baseRoute = Routes.WordleMode
                )
            }
        }
    }

    BasicScreenBox(feedbackMessage = feedbackMessage) {
        if (wordle.word.isNotEmpty()) {
            WordleScreen(
                wordleWord = wordle.word,
                attempt = attempt,
                listWordleAttempts = attempts,
                listOfKeyboardStates = listKeyBoardState,
                errorMessage = feedbackMessage,
                showDialog = {
                    viewModel.updateDialog(WordleDialogType.HowToPlay)
                },
                updateAttemptString = {
                    viewModel.updateAttemptString(it)
                }) {
                viewModel.checkWord()
            }
        }
    }
}

@Composable
fun WordleScreen(
    wordleWord: String,
    attempt: String,
    listWordleAttempts: List<WordleAttempt>,
    listOfKeyboardStates: List<KeyboardLetter>,
    errorMessage: FeedbackMessage,
    showDialog: () -> Unit,
    updateAttemptString: (String) -> Unit,
    checkWord: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(0.75f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(vertical = 16.dp, horizontal = 32.dp)
                    .align(Alignment.Center),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                BasicContainer(modifier = Modifier.align(Alignment.CenterHorizontally), onClick = {
                    showDialog()
                }) {
                    BasicText(
                        modifier = Modifier.padding(16.dp),
                        text = stringResource(R.string.biblical_wordle),
                        fontSize = 32
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    WordleRows(
                        wordleWord = wordleWord,
                        attempt = attempt,
                        listWordleAttempts = listWordleAttempts,
                        errorMessage = errorMessage
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(0.25f)
                .padding(bottom = 8.dp)
                .systemGestureExclusion()
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
                    ) {
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
                        .background(basicContainerClean())
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
                            fontSizeRange = FontSizeRange(1.sp, 32.sp),
                            color = contrastColor()
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
                        .background(basicContainerClean())
                        .clickable {
                            updateAttemptString(attempt.dropLast(1))
                        }) {
                        Image(
                            modifier = Modifier.align(Alignment.Center),
                            painter = painterResource(id = R.drawable.backspace_24px),
                            colorFilter = ColorFilter.tint(contrastColor()),
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
    listWordleAttempts: List<WordleAttempt>,
    errorMessage: FeedbackMessage,
    isFromResults: Boolean = false
) {
    for (i in 0 until 6) {
        if (isFromResults) {
            if (listWordleAttempts[i].attemptState == WordleAttemptState.USER_HAS_TRIED) {
                RowLetterWordle(
                    wordleWord = wordleWord,
                    attempt = listWordleAttempts[i].run {
                        when (attemptState) {
                            WordleAttemptState.USER_IS_CURRENTLY_HERE -> attempt
                            WordleAttemptState.USER_HAS_TRIED -> word
                            WordleAttemptState.USER_WILL_STILL_TRY -> ""
                        }
                    },
                    letterStates = listWordleAttempts[i],
                    errorMessage = errorMessage,
                    isFromResults = isFromResults
                )
            }

        } else {
            RowLetterWordle(
                wordleWord = wordleWord,
                attempt = listWordleAttempts[i].run {
                    when (attemptState) {
                        WordleAttemptState.USER_IS_CURRENTLY_HERE -> attempt
                        WordleAttemptState.USER_HAS_TRIED -> word
                        WordleAttemptState.USER_WILL_STILL_TRY -> ""
                    }
                },
                letterStates = listWordleAttempts[i],
                errorMessage = errorMessage,
                isFromResults = isFromResults
            )
        }
    }
}

@Composable
fun RowLetterWordle(
    wordleWord: String,
    attempt: String,
    letterStates: WordleAttempt,
    errorMessage: FeedbackMessage,
    isFromResults: Boolean = false
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
        if (errorMessage != FeedbackMessage.NoMessage && (errorMessage == FeedbackMessage.WordNotIntList || errorMessage == FeedbackMessage.RepeatedWord || attempt.length != wordleWord.length) && letterStates.attemptState == WordleAttemptState.USER_IS_CURRENTLY_HERE
        ) {
            shakeController.shake(
                ShakeConfig(
                    iterations = 4,
                    intensity = 4_000f,
                    rotateY = 5f,
                    translateX = 20f,
                )
            )
        }
    }

    LaunchedEffect(letterStates.listOfLetterStates) {
        if (wordleWord == attempt && !isFromResults && letterStates.attemptState == WordleAttemptState.USER_HAS_TRIED) {
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

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shake(shakeController)
            .border(2.dp, almostBlack, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(almostBlack),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        if (wordleWord.length > 3) {
            for (i in wordleWord.indices) {
                FlipCard(
                    modifier = Modifier.weight(1f),
                    delay = 300 * i,
                    letterState = letterStates.listOfLetterStates[i]
                ) {
                    if (attempt.length > i) {
                        AutoResizeText(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(8.dp),
                            text = attempt[i].toString(),
                            fontSizeRange = FontSizeRange(1.sp, 24.sp),
                            fontFamily = achivoFontFamily,
                            color = contrastColor()
                        )
                    } else {
                        Spacer(modifier = Modifier.size(0.dp))
                    }
                }
            }
        }
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
        startValue = basicContainerClean(),
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
        BasicText(
            modifier = Modifier.align(Alignment.Center),
            text = letter,
            fontColor = contrastColor()
        )
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewWordle() {
    NovaGincanaBiblicaTheme {
        WordleScreen(
            wordleWord = "Teste",
            "LOVE",
            listWordleAttempts = generateStartWordleAttemptList(),
            listOfKeyboardStates = initiateKeyboardState(),
            FeedbackMessage.RepeatedWord,
            showDialog = {},
            {}) {

        }
    }
}