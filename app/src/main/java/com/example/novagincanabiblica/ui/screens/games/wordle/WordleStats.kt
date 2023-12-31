package com.example.novagincanabiblica.ui.screens.games.wordle

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.novagincanabiblica.data.models.WordleData
import com.example.novagincanabiblica.data.models.WordleDataCalculated
import com.example.novagincanabiblica.ui.basicviews.BasicText
import com.example.novagincanabiblica.ui.basicviews.DaysStreak
import com.example.novagincanabiblica.ui.basicviews.animateAlpha
import com.example.novagincanabiblica.ui.basicviews.animateInt
import com.example.novagincanabiblica.ui.screens.games.quiz.BackAndShare
import com.example.novagincanabiblica.ui.theme.NovaGincanaBiblicaTheme
import com.example.novagincanabiblica.ui.theme.almostWhite
import com.example.novagincanabiblica.ui.theme.closeToBlack
import com.example.novagincanabiblica.ui.theme.correctAnswer
import com.example.novagincanabiblica.ui.theme.lessWhite
import com.example.novagincanabiblica.ui.theme.wrongAnswer

@Composable
fun WordleStats(
    modifier: Modifier = Modifier,
    wordleStats: WordleData,
    progresses: WordleDataCalculated,
    isFromProfileScreen: Boolean = false,
    closeDialog: () -> Unit = {}
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Box(
            modifier = modifier
                .shadow(20.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    almostWhite
                )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                BasicText(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = "Wordle",
                    fontSize = 18
                )
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(lessWhite)
                        .padding(top = 16.dp, bottom = 16.dp, end = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    WordleProgress(
                        attemptNumber = "1",
                        wins = wordleStats.winOnFirst,
                        progress = progresses.firstTryFloat
                    )
                    WordleProgress(
                        attemptNumber = "2",
                        wins = wordleStats.winOnSecond,
                        progress = progresses.secondTryFloat
                    )
                    WordleProgress(
                        attemptNumber = "3",
                        wins = wordleStats.winOnThird,
                        progress = progresses.thirdTryFloat
                    )
                    WordleProgress(
                        attemptNumber = "4",
                        wins = wordleStats.winOnForth,
                        progress = progresses.forthTryFloat
                    )
                    WordleProgress(
                        attemptNumber = "5",
                        wins = wordleStats.winOnFirth,
                        progress = progresses.firthTryFloat
                    )
                    WordleProgress(
                        attemptNumber = "6",
                        wins = wordleStats.winOnSixth,
                        progress = progresses.sixthTryFloat
                    )
                    WordleProgress(
                        attemptNumber = "L",
                        wins = wordleStats.lost,
                        progress = progresses.lostFloat,
                        isLose = true
                    )
                }
            }
        }
        if (wordleStats.streak > 1) {
            DaysStreak(wordleStats.streak)
        }
        if (isFromProfileScreen) {
            BackAndShare(modifier = Modifier, goBackClick = {
                closeDialog()
            }) {

            }
        }
    }
}

@Composable
fun WordleProgress(
    modifier: Modifier = Modifier,
    attemptNumber: String,
    wins: Int,
    progress: Float,
    isLose: Boolean = false
) {

    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(
            modifier = Modifier
                .height(30.dp)
                .fillMaxWidth()
                .weight(0.1f)
        ) {
            BasicText(
                modifier = Modifier.align(Alignment.Center),
                text = attemptNumber,
                fontSize = 22
            )
        }
        MyWordleProgress(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.9f), progress = progress, wins = wins, isLose = isLose
        )
    }
}

@Composable
fun MyWordleProgress(modifier: Modifier, progress: Float, wins: Int, isLose: Boolean = false) {

    var startAnimation by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(wins) {
        if (wins > 0) {
            startAnimation = false
        }
    }

    val barColor = if (isLose) wrongAnswer else correctAnswer

    val animatedProgress by animateAlpha(condition = startAnimation, endValue = progress)
    val animateWin by animateInt(startAnimation = startAnimation, endValue = wins)

    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .height(30.dp)
                .background(closeToBlack)
        )
        Box {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .clip(RoundedCornerShape(16.dp))
                    .height(30.dp)
                    .background(barColor)
            )
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .width(38.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .height(30.dp)
                    .background(barColor)
            )
            BasicText(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .align(Alignment.CenterEnd), text = animateWin.toString()
            )
        }

    }
}

@Preview(showBackground = true)
@Composable
fun PreviewWordleStats() {
    NovaGincanaBiblicaTheme {
        WordleStats(
            wordleStats = generateWordleData(),
            progresses = generatedCalculatedData()
        )
    }
}

fun generateWordleData(): WordleData = WordleData(0, 2, 4, 7, 5, 3, 2, 5)
fun generatedCalculatedData(): WordleDataCalculated =
    WordleDataCalculated(0.0f, 0.4f, 0.8f, 1f, 0.6f, 0.6f, 0.2f)