package com.bsoftwares.thebiblequiz.ui.screens.games.quiz

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.bsoftwares.thebiblequiz.R
import com.bsoftwares.thebiblequiz.data.models.QuestionStatsData
import com.bsoftwares.thebiblequiz.data.models.QuestionStatsDataCalculated
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicContainer
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicText
import com.bsoftwares.thebiblequiz.ui.basicviews.animateAlpha
import com.bsoftwares.thebiblequiz.ui.basicviews.animateInt
import com.bsoftwares.thebiblequiz.ui.basicviews.animatePosition
import com.bsoftwares.thebiblequiz.ui.screens.games.quiz.screens.BackAndShare
import com.bsoftwares.thebiblequiz.ui.theme.NovaGincanaBiblicaTheme
import com.bsoftwares.thebiblequiz.ui.theme.almostWhite
import com.bsoftwares.thebiblequiz.ui.theme.closeToBlack
import com.bsoftwares.thebiblequiz.ui.theme.container_in_container
import com.bsoftwares.thebiblequiz.ui.theme.correctAnswer
import com.bsoftwares.thebiblequiz.ui.theme.gray

@Composable
fun QuizStats(
    data: QuestionStatsData,
    calculatedData: QuestionStatsDataCalculated,
    isFromProfileScreen: Boolean = false,
    closeDialog: () -> Unit = {}
) {

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        BasicContainer {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.baseline_check_24),
                        contentDescription = null
                    )
                    BasicText(
                        modifier = Modifier.align(Alignment.Center),
                        text = "The Bible Quiz",
                        fontSize = 18
                    )
                    BasicText(modifier = Modifier.align(Alignment.CenterEnd), text = stringResource(
                        R.string.total
                    )
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(gray)
                ) {
                    PointsProgressRow(
                        correctPoints = data.easyCorrect,
                        progress = calculatedData.easyFloat,
                        totalPoints = data.getTotalEasy().toString(),
                        difficulty = stringResource(R.string.easy_stats),
                        progressInt = calculatedData.easyInt
                    )

                    PointsProgressRow(
                        correctPoints = data.mediumCorrect,
                        progress = calculatedData.mediumFloat,
                        totalPoints = data.getTotalMedium().toString(),
                        difficulty = stringResource(R.string.medium_stats),
                        progressInt = calculatedData.mediumInt
                    )

                    PointsProgressRow(
                        correctPoints = data.hardCorrect,
                        progress = calculatedData.hardFLoat,
                        totalPoints = data.getTotalHard().toString(),
                        difficulty = stringResource(R.string.hard_stats),
                        progressInt = calculatedData.hardInt
                    )

                    PointsProgressRow(
                        correctPoints = data.impossibleCorrect,
                        progress = calculatedData.impossibleFloat,
                        totalPoints = data.getTotalImpossible().toString(),
                        difficulty = stringResource(R.string.impossible_stats),
                        progressInt = calculatedData.impossibleInt
                    )
                }
            }
        }

        if (data.streak > 1) {
            DaysStreak(streakDays = data.streak)
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
fun DaysStreak(streakDays: Int) {
    var startAnimation by remember {
        mutableStateOf(true)
    }
    LaunchedEffect(Unit) {
        startAnimation = false
    }

    val animateDaysPosition by animatePosition(
        condition = startAnimation,
        startValue = IntOffset(-30, 0),
        endValue = IntOffset.Zero,
        duration = 500
    )

    val animateDaysAlpha by animateAlpha(condition = startAnimation, duration = 500)

    val animateStreak by animateInt(
        startAnimation = startAnimation,
        endValue = streakDays
    )

    BasicContainer (modifier = Modifier.fillMaxWidth()) {
        Row {
            BasicContainer(
                modifier = Modifier
                    .padding(8.dp)
                    .size(64.dp),
                backGroundColor = container_in_container()
            ) {
                BasicText(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.Center),
                    text = animateStreak.toString(),
                    fontSize = 28
                )
            }

            BasicText(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .offset {
                        animateDaysPosition
                    }
                    .alpha(animateDaysAlpha),
                text = stringResource(R.string.days_in_streak), fontSize = 22
            )
        }
    }
    
}

@Composable
fun PointsProgressRow(
    modifier: Modifier = Modifier,
    correctPoints: Int,
    progress: Float,
    totalPoints: String,
    difficulty: String,
    progressInt: Int
) {

    var startAnimation by remember {
        mutableStateOf(true)
    }
    LaunchedEffect(Unit) {
        startAnimation = false
    }

    val animateProgress by animateAlpha(
        condition = startAnimation,
        endValue = progress
    )

    val animateDays by animateInt(
        startAnimation = startAnimation,
        endValue = correctPoints
    )

    val progressInteger by animateInt(
        startAnimation = startAnimation,
        endValue = progressInt
    )

    Row(
        modifier = modifier.padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .height(40.dp)
                .fillMaxWidth()
                .weight(.1f)
        ) {
            BasicText(
                modifier = Modifier.align(Alignment.Center),
                text = animateDays.toString(),
                fontSize = 22,
                fontColor = closeToBlack
            )
        }

        MyProgressBar(
            modifier = Modifier
                .fillMaxWidth()
                .weight(.8f)
                .align(Alignment.CenterVertically),
            progress = animateProgress,
            text = "$difficulty($progressInteger%)"
        )

        Box(
            modifier = Modifier
                .height(40.dp)
                .fillMaxWidth()
                .weight(.1f)
        ) {
            BasicText (
                modifier = Modifier.align(Alignment.Center),
                text = totalPoints,
                fontColor = closeToBlack,
                fontSize = 22
            )
        }
    }
}

@Composable
fun MyProgressBar(modifier: Modifier = Modifier, progress: Float, text: String) {

    Box(
        modifier = modifier.clip(RoundedCornerShape(16.dp))
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(40.dp)
                .background(closeToBlack)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth(progress)
                .clip(RoundedCornerShape(16.dp))
                .height(40.dp)
                .background(correctAnswer)
        )
        BasicText(
            modifier = Modifier.align(Alignment.Center),
            text = text,
            fontColor = almostWhite
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewQuestionStats() {
    NovaGincanaBiblicaTheme {
        QuizStats(generateStatsData(), calculatedData = generateCalculatedData())
    }
}

fun generateStatsData(): QuestionStatsData =
    QuestionStatsData(43, 3, 30, 88, 77, 80, 10, 60, 6)

fun generateCalculatedData(): QuestionStatsDataCalculated = QuestionStatsDataCalculated(.5f, .3f, .2f, .15f, 5, 10 ,12 ,25)