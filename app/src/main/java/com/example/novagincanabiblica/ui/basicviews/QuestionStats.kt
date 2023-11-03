package com.example.novagincanabiblica.ui.basicviews

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
fun QuestionStats(data: QuestionStatsData) {

    var startAnimation by remember {
        mutableStateOf(true)
    }
    LaunchedEffect(Unit) {
        startAnimation = false
    }

    val animateEasy by animateAlpha(
        condition = startAnimation,
        endValue = getAlphaValueToAnimate(data.easyCorrect, data.easyWrong)
    )

    val animateMedium by animateAlpha(
        condition = startAnimation,
        endValue = getAlphaValueToAnimate(data.mediumCorrect, data.mediumWrong)
    )

    val animateHard by animateAlpha(
        condition = startAnimation,
        endValue = getAlphaValueToAnimate(data.hardCorrect, data.hardWrong)
    )

    val animateImpossible by animateAlpha(
        condition = startAnimation,
        endValue = getAlphaValueToAnimate(data.impossibleCorrect, data.impossibleWrong)

    )

    val animateEasyCorrect by animateInt(
        startAnimation = startAnimation,
        endValue = data.easyCorrect
    )
    val animateMediumCorrect by animateInt(
        startAnimation = startAnimation,
        endValue = data.mediumCorrect
    )
    val animateHardCorrect by animateInt(
        startAnimation = startAnimation,
        endValue = data.hardCorrect
    )
    val animateImpossibleCorrect by animateInt(
        startAnimation = startAnimation,
        endValue = data.impossibleCorrect
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
                    totalPoints = data.getTotalEasy().toString(),
                    difficulty = "EASY/(${animateEasy.times(100).toInt()}%)"
                )

                PointsProgressRow(
                    correctPoints = animateMediumCorrect.toString(),
                    progress = animateMedium,
                    totalPoints = data.getTotalMedium().toString(),
                    difficulty = "MEDIUM/(${animateMedium.times(100).toInt()}%)"
                )

                PointsProgressRow(
                    correctPoints = animateHardCorrect.toString(),
                    progress = animateHard,
                    totalPoints = data.getTotalHard().toString(),
                    difficulty = "HARD/(${animateHard.times(100).toInt()}%)"
                )

                PointsProgressRow(
                    correctPoints = animateImpossibleCorrect.toString(),
                    progress = animateImpossible,
                    totalPoints = data.getTotalImpossible().toString(),
                    difficulty = "IMPOSSIBLE/(${animateImpossible.times(100).toInt()}%)"
                )
            }
        }
    }

}

fun getAlphaValueToAnimate(correct: Int, wrong: Int): Float = if (itDoesntBreak(
        correct,
        wrong
    )
) (correct.toFloat() / (correct + wrong)) else 0f

//Checks if we are not dividing zero or by zero.
fun itDoesntBreak(correct: Int, wrong: Int) = !(correct == 0 || correct + wrong == 0)

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