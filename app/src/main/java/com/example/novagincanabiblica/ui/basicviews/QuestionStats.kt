package com.example.novagincanabiblica.ui.basicviews

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.novagincanabiblica.R
import com.example.novagincanabiblica.data.models.QuestionStatsData
import com.example.novagincanabiblica.ui.theme.NovaGincanaBiblicaTheme
import com.example.novagincanabiblica.ui.theme.almostWhite
import com.example.novagincanabiblica.ui.theme.closeToBlack
import com.example.novagincanabiblica.ui.theme.correctAnswer
import com.example.novagincanabiblica.ui.theme.lessWhite

@Composable
fun QuestionStats(questionStatsData: QuestionStatsData) {

    var startAnimation by remember {
        mutableStateOf(true)
    }
    LaunchedEffect(Unit) {
        startAnimation = false
    }

    val totalEasy by remember {
        mutableIntStateOf(questionStatsData.easyWrong + questionStatsData.easyCorrect)
    }

    val totalMedium by remember {
        mutableIntStateOf(questionStatsData.mediumWrong + questionStatsData.mediumCorrect)
    }

    val totalHard by remember {
        mutableIntStateOf(questionStatsData.hardWrong + questionStatsData.hardCorrect)
    }

    val totalImpossible by remember {
        mutableIntStateOf(questionStatsData.impossibleWrong + questionStatsData.impossibleCorrect)
    }

    val animateEasy by animateAlpha(
        condition = startAnimation,
        endValue = if (itDoesntBreak(questionStatsData.easyCorrect, questionStatsData.easyWrong)) {
            (questionStatsData.easyCorrect.toFloat() / totalEasy)
        } else 0f
    )

    val animateMedium by animateAlpha(
        condition = startAnimation,
        endValue = if (itDoesntBreak(
                questionStatsData.mediumCorrect,
                questionStatsData.mediumWrong
            )
        )
            (questionStatsData.mediumCorrect.toFloat() / totalMedium) else 0f
    )

    val animateHard by animateAlpha(
        condition = startAnimation,
        endValue = if (itDoesntBreak(
                questionStatsData.hardCorrect,
                questionStatsData.hardWrong
            )
        ) (questionStatsData.hardCorrect.toFloat() / totalHard) else 0f
    )

    val animateImpossible by animateAlpha(
        condition = startAnimation,
        endValue = if (itDoesntBreak(
                questionStatsData.impossibleCorrect,
                questionStatsData.impossibleWrong
            )
        ) (questionStatsData.impossibleCorrect.toFloat() / totalImpossible) else 0f
    )

    val animateEasyCorrect by animateIntAsState(
        targetValue = if (startAnimation) 0 else questionStatsData.easyCorrect,
        animationSpec = tween(
            durationMillis = 1000,
            delayMillis = 100,
            easing = LinearOutSlowInEasing
        ),
        label = ""
    )

    val animate2 by animateIntAsState(
        targetValue = if (startAnimation) 0 else questionStatsData.mediumCorrect,
        animationSpec = tween(
            durationMillis = 1000,
            delayMillis = 100,
            easing = LinearOutSlowInEasing
        ),
        label = ""
    )

    val animate3 by animateIntAsState(
        targetValue = if (startAnimation) 0 else questionStatsData.hardCorrect,
        animationSpec = tween(
            durationMillis = 1000,
            delayMillis = 100,
            easing = LinearOutSlowInEasing
        ),
        label = ""
    )

    val animate4 by animateIntAsState(
        targetValue = if (startAnimation) 0 else questionStatsData.impossibleCorrect,
        animationSpec = tween(
            durationMillis = 1000,
            delayMillis = 100,
            easing = LinearOutSlowInEasing
        ),
        label = ""
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(20.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(almostWhite)
    ) {
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
                //BasicText(modifier = Modifier.align(Alignment.BottomStart), text = "Correct")
                BasicText(
                    modifier = Modifier.align(Alignment.Center),
                    text = "The Bible Quiz",
                    fontSize = 18
                )
                BasicText(modifier = Modifier.align(Alignment.CenterEnd), text = "Total")
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(lessWhite)
            ) {
                PointsProgressRow(
                    correctPoints = animateEasyCorrect.toString(),
                    progress = animateEasy,
                    totalPoints = totalEasy.toString(),
                    difficulty = "EASY"
                )

                PointsProgressRow(
                    correctPoints = animate2.toString(),
                    progress = animateMedium,
                    totalPoints = totalMedium.toString(),
                    difficulty = "MEDIUM"
                )

                PointsProgressRow(
                    correctPoints = animate3.toString(),
                    progress = animateHard,
                    totalPoints = totalHard.toString(),
                    difficulty = "HARD"
                )

                PointsProgressRow(
                    correctPoints = animate4.toString(),
                    progress = animateImpossible,
                    totalPoints = totalImpossible.toString(),
                    difficulty = "IMPOSSIBLE"
                )
            }
        }
    }

}

fun itDoesntBreak(correct: Int, wrong: Int): Boolean {
    return !(correct == 0 || correct + wrong == 0)
}

@Composable
fun PointsProgressRow(
    modifier: Modifier = Modifier,
    correctPoints: String,
    progress: Float,
    totalPoints: String,
    difficulty: String
) {
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
                text = correctPoints,
                fontSize = 22
            )
            /*AnimatedCounter(
                modifier = Modifier.align(Alignment.Center),
                count = correctPoints
            )*/

        }

        MyProgressBar(
            modifier = Modifier
                .fillMaxWidth()
                .weight(.8f)
                .align(Alignment.CenterVertically),
            progress = progress,
            text = difficulty
        )

        Box(
            modifier = Modifier
                .height(40.dp)
                .fillMaxWidth()
                .weight(.1f)
        ) {
            AnimatedCounter(
                modifier = Modifier.align(Alignment.Center),
                count = totalPoints
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
        QuestionStats(generateStatsData())
    }
}

fun generateStatsData(): QuestionStatsData =
    QuestionStatsData(43, 3, 30, 88, 77, 80, 10, 60, 6)