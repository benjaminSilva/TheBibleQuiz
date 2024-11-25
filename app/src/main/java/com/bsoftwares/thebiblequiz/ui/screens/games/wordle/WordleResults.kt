package com.bsoftwares.thebiblequiz.ui.screens.games.wordle

import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bsoftwares.thebiblequiz.data.models.Session
import com.bsoftwares.thebiblequiz.data.models.WordleDataCalculated
import com.bsoftwares.thebiblequiz.data.models.WordleGame
import com.bsoftwares.thebiblequiz.data.models.state.FeedbackMessage
import com.bsoftwares.thebiblequiz.data.models.wordle.LetterState
import com.bsoftwares.thebiblequiz.data.models.wordle.Wordle
import com.bsoftwares.thebiblequiz.data.models.wordle.WordleAttemptState
import com.bsoftwares.thebiblequiz.data.models.wordle.WordleAttempt
import com.bsoftwares.thebiblequiz.data.models.wordle.generateStartWordleAttemptList
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicContainer
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicText
import com.bsoftwares.thebiblequiz.ui.basicviews.highlightText
import com.bsoftwares.thebiblequiz.ui.screens.Routes
import com.bsoftwares.thebiblequiz.ui.screens.games.quiz.screens.BackAndShare
import com.bsoftwares.thebiblequiz.ui.theme.NovaGincanaBiblicaTheme
import com.bsoftwares.thebiblequiz.viewmodel.WordleViewModel

@Composable
fun InitializeWordleResult(navController: NavHostController, viewModel: WordleViewModel) {
    val wordle by viewModel.wordle.collectAsStateWithLifecycle()
    val listOfAttempts by viewModel.attempts.collectAsStateWithLifecycle()
    val session by viewModel.localSession.collectAsStateWithLifecycle()
    val calculatedWordleData by viewModel.calculatedWordleData.collectAsStateWithLifecycle()

    LaunchedEffect(session) {
        viewModel.calculateWordleData()
    }

    WordleResultsScreen(
        navController = navController, wordle = wordle,
        listOfAttempts = listOfAttempts,
        session = session, calculatedWordleData = calculatedWordleData
    )
}

@Composable
fun WordleResultsScreen(
    navController: NavHostController,
    wordle: Wordle,
    listOfAttempts: List<WordleAttempt>,
    session: Session,
    calculatedWordleData: WordleDataCalculated
) {

    var localDesity = LocalDensity.current

    var heightForSpacer by remember {
        mutableStateOf(0.dp)
    }

    var verseText by remember {
         mutableStateOf(AnnotatedString(""))
    }

    val context = LocalContext.current

    LaunchedEffect(key1 = wordle) {
        if (wordle.word.isNotBlank()) {
            verseText = highlightText(wordle.verse, wordle.word, context)
        }
    }

    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(
            Intent.EXTRA_TEXT,
            "I want to share this verse with you:\n\n${wordle.verse}\n\n${wordle.reference}"
        )
        type = "text/plain"
    }

    var emojiIntent by remember {
        mutableStateOf(Intent())
    }

    LaunchedEffect(key1 = listOfAttempts) {
        if (listOfAttempts.first().attemptState != WordleAttemptState.USER_IS_CURRENTLY_HERE)
            emojiIntent = Intent.createChooser(Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(
                    Intent.EXTRA_TEXT,
                    generateResultEmoteString(listOfAttempts)
                )
                type = "text/plain"
            }, null)
    }

    val shareVerse = Intent.createChooser(sendIntent, null)
    val haptic = LocalHapticFeedback.current

    Box(
        modifier = Modifier
            .fillMaxHeight()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BasicText(text = "Word")
            RowLetterWordle(
                wordleWord = wordle.word,
                attempt = wordle.word,
                letterStates = WordleAttempt(
                    wordle.word,
                    attemptState = WordleAttemptState.USER_HAS_TRIED,
                    listOfLetterStates = listOf(
                        LetterState.LETTER_CORRECT_PLACE,
                        LetterState.LETTER_CORRECT_PLACE,
                        LetterState.LETTER_CORRECT_PLACE,
                        LetterState.LETTER_CORRECT_PLACE,
                        LetterState.LETTER_CORRECT_PLACE,
                        LetterState.LETTER_CORRECT_PLACE,
                        LetterState.LETTER_CORRECT_PLACE,
                        LetterState.LETTER_CORRECT_PLACE
                    )
                ),
                errorMessage = FeedbackMessage.NoMessage,
                isFromResults = true
            )
            BasicText(text = "Found in this verse")
            BasicContainer (onLongClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                context.startActivity(shareVerse)
            }) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    BasicText(
                        text = verseText, fontSize = 24,
                        lineHeight = 22
                    )
                    BasicText(modifier = Modifier.align(Alignment.End), text = wordle.reference)
                }
            }
            BasicText(text = "Stats")
            if (session.userInfo.userId.isNotEmpty()) {
                WordleStats(wordleStats = session.wordle.wordleStats, progresses = calculatedWordleData)
            } else {
                BasicContainer {
                    BasicText(modifier = Modifier.fillMaxWidth().padding(16.dp), text = "If you login, you will keep up your stats.")
                }
            }
            BasicText(text = "Attempts")
            WordleRows(
                wordleWord = wordle.word,
                attempt = "",
                listWordleAttempts = listOfAttempts,
                errorMessage = FeedbackMessage.NoMessage,
                isFromResults = true
            )
            Spacer(modifier = Modifier.height(height = heightForSpacer))
        }
        BackAndShare(modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(bottom = 16.dp)
            .onGloballyPositioned {
                heightForSpacer = with(localDesity) {
                    it.size.height.toDp()
                }
            }, goBackClick = {
            navController.popBackStack(Routes.Home.value, false)
        }) {
            context.startActivity(emojiIntent)
        }
    }
}

fun generateResultEmoteString(listOfAttempts: List<WordleAttempt>) = StringBuilder().apply {
    append("The Bible Wordle ")
    append("${listOfAttempts.count { it.attemptState == WordleAttemptState.USER_HAS_TRIED }}/6\n")
    val green = String(Character.toChars(0x1F7E9))
    val black = String(Character.toChars(0x2B1B))
    val yellow = String(Character.toChars(0x1F7E8))
    listOfAttempts.forEach {
        if (it.attemptState == WordleAttemptState.USER_HAS_TRIED) {
            append("\n")
            it.listOfLetterStates.forEach { letterState ->
                when (letterState) {
                    LetterState.LETTER_CORRECT_PLACE -> append(green)
                    LetterState.LETTER_NOT_IN_WORD -> append(black)
                    LetterState.LETTER_WRONG_PLACE -> append(yellow)
                    LetterState.LETTER_NOT_CHECKED -> Unit
                }
            }
        }
    }
}.toString()

@Preview(showBackground = true)
@Composable
fun PreviewWordleResults() {
    NovaGincanaBiblicaTheme {
        WordleResultsScreen(
            rememberNavController(),
            Wordle(),
            generateStartWordleAttemptList(),
            session = Session().copy(wordle = WordleGame().copy(wordleStats = generateWordleData())),
            generatedCalculatedData()
        )
    }
}