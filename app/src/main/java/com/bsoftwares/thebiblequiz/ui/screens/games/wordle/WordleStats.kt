package com.bsoftwares.thebiblequiz.ui.screens.games.wordle

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bsoftwares.thebiblequiz.data.models.WordleData
import com.bsoftwares.thebiblequiz.data.models.WordleDataCalculated
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicContainer
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicText
import com.bsoftwares.thebiblequiz.ui.screens.games.quiz.DaysStreak
import com.bsoftwares.thebiblequiz.ui.basicviews.animateAlpha
import com.bsoftwares.thebiblequiz.ui.basicviews.animateInt
import com.bsoftwares.thebiblequiz.ui.screens.games.quiz.screens.BackAndShare
import com.bsoftwares.thebiblequiz.ui.theme.NovaGincanaBiblicaTheme
import com.bsoftwares.thebiblequiz.ui.theme.closeToBlack
import com.bsoftwares.thebiblequiz.ui.theme.correctAnswer
import com.bsoftwares.thebiblequiz.ui.theme.gray
import com.bsoftwares.thebiblequiz.ui.theme.lessWhite
import com.bsoftwares.thebiblequiz.ui.theme.wrongAnswer

@Composable
fun WordleStats(
    modifier: Modifier = Modifier,
    wordleStats: WordleData,
    progresses: WordleDataCalculated,
    isFromProfileScreen: Boolean = false,
    closeDialog: () -> Unit = {}
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        BasicContainer {
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
                        .background(gray)
                        .padding(top = 16.dp, bottom = 16.dp, end = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    WordleProgress(
                        attemptNumber = "1",
                        wins = wordleStats.winOnFirst,
                        progress = progresses.firstTryFloat,
                        total = wordleStats.getMax()
                    )
                    WordleProgress(
                        attemptNumber = "2",
                        wins = wordleStats.winOnSecond,
                        progress = progresses.secondTryFloat,
                        total = wordleStats.getTotal()
                    )
                    WordleProgress(
                        attemptNumber = "3",
                        wins = wordleStats.winOnThird,
                        progress = progresses.thirdTryFloat,
                        total = wordleStats.getTotal()
                    )
                    WordleProgress(
                        attemptNumber = "4",
                        wins = wordleStats.winOnForth,
                        progress = progresses.forthTryFloat,
                        total = wordleStats.getTotal()
                    )
                    WordleProgress(
                        attemptNumber = "5",
                        wins = wordleStats.winOnFirth,
                        progress = progresses.firthTryFloat,
                        total = wordleStats.getTotal()
                    )
                    WordleProgress(
                        attemptNumber = "6",
                        wins = wordleStats.winOnSixth,
                        progress = progresses.sixthTryFloat,
                        total = wordleStats.getTotal()
                    )
                    WordleProgress(
                        attemptNumber = "L",
                        wins = wordleStats.lost,
                        progress = progresses.lostFloat,
                        isLose = true,
                        total = wordleStats.getTotal()
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
    total: Int,
    progress: Float,
    isLose: Boolean = false
) {

    var startAnimation by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(wins) {
        if (wins > 0) {
            startAnimation = false
        }
    }

    val animatedProgress by animateAlpha(condition = startAnimation, endValue = progress)
    val animateWin by animateInt(startAnimation = startAnimation, endValue = wins)
    val animatePercentage by animateInt(startAnimation = startAnimation, endValue = ((wins.toFloat()/total.toFloat()) * 100).toInt())

    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .height(30.dp)
                .fillMaxWidth()
                .weight(0.1f)
        ) {
            BasicText(
                modifier = Modifier.align(Alignment.Center),
                text = attemptNumber,
                fontSize = 22,
                fontColor = closeToBlack
            )
        }
        MyWordleProgress(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.9f), animatedProgress = animatedProgress, animateWin = animateWin, isLose = isLose
        )

        Box(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .height(30.dp)
                .fillMaxWidth()
                .weight(0.2f)
        ) {
            BasicText(
                modifier = Modifier.align(Alignment.CenterStart),
                text = if (animatePercentage >= 10) "${animatePercentage}%" else "0${animatePercentage}%",
                fontSize = 22,
                fontColor = closeToBlack
            )
        }

    }
}

@Composable
fun MyWordleProgress(modifier: Modifier, animatedProgress: Float, animateWin: Int, isLose: Boolean = false) {

    val barColor = if (isLose) wrongAnswer else correctAnswer

    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .height(40.dp)
                .background(closeToBlack)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth(animatedProgress)
                .clip(RoundedCornerShape(16.dp))
                .height(40.dp)
                .border(2.dp, closeToBlack, RoundedCornerShape(16.dp))
                .background(barColor)
        )

        BasicText(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .align(Alignment.CenterEnd), text = animateWin.toString(),
            fontColor = lessWhite,
            fontSize = 22
        )

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