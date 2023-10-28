package com.example.novagincanabiblica.ui.screens.gamemodes.solomode

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.novagincanabiblica.data.models.Answer
import com.example.novagincanabiblica.data.models.Question
import com.example.novagincanabiblica.data.models.state.AnswerDestinationState
import com.example.novagincanabiblica.ui.basicviews.AutoResizeText
import com.example.novagincanabiblica.ui.basicviews.BasicText
import com.example.novagincanabiblica.ui.basicviews.ClockTimer
import com.example.novagincanabiblica.ui.basicviews.FontSizeRange
import com.example.novagincanabiblica.ui.basicviews.animateAlpha
import com.example.novagincanabiblica.ui.basicviews.generateSubSequentialAlphaAnimations
import com.example.novagincanabiblica.ui.basicviews.generateSubSequentialPositionAnimations
import com.example.novagincanabiblica.ui.navigation.navigateWithoutRemembering
import com.example.novagincanabiblica.ui.screens.Routes
import com.example.novagincanabiblica.ui.theme.NovaGincanaBiblicaTheme
import com.example.novagincanabiblica.ui.theme.correctAnswer
import com.example.novagincanabiblica.ui.theme.lessWhite
import com.example.novagincanabiblica.ui.theme.wrongAnswer
import com.example.novagincanabiblica.viewmodel.SoloModeViewModel


@Composable
fun InitializeSoloQuestionScreen(
    navController: NavHostController,
    soloViewModel: SoloModeViewModel
) {

    val currentQuestionState by soloViewModel.currentQuestion.collectAsStateWithLifecycle()
    val answerState by soloViewModel.nextDestination.collectAsStateWithLifecycle(initialValue = AnswerDestinationState.STAY)
    val remainingTime by soloViewModel.remainingTime.collectAsStateWithLifecycle()
    val secondAnimation by soloViewModel.startSecondAnimation.collectAsStateWithLifecycle()
    val session by soloViewModel.sessionState.collectAsStateWithLifecycle()

    val context = LocalContext.current

    var startAnimation by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(Unit) {
        startAnimation = false
        soloViewModel.startClock()
    }

    handleAnotherNavigation(
        navController = navController,
        answerState = answerState
    )

    SoloQuestionScreen(
        currentQuestion = currentQuestionState,
        startAnimation = startAnimation,
        remainingTime = remainingTime,
        startSecondAnimation = secondAnimation
    ) { answer ->
        soloViewModel.verifyAnswer(answer)
    }

}

fun handleAnotherNavigation(
    navController: NavHostController,
    answerState: AnswerDestinationState
) {
    when (answerState) {
        AnswerDestinationState.RESULTS -> navController.navigateWithoutRemembering(
            route = Routes.Results
        )

        else -> Unit
    }
}

@Composable
fun SoloQuestionScreen(
    currentQuestion: Question,
    startAnimation: Boolean,
    startSecondAnimation: Boolean,
    remainingTime: String,
    answerClick: (Answer) -> Unit
) {

    val listOfAnimations =
        generateSubSequentialAlphaAnimations(numberOfViews = 4, condition = startAnimation)
    val listOfPositionAnimations = generateSubSequentialPositionAnimations(
        numberOfViews = 4,
        condition = startAnimation,
        offsetStart = IntOffset(-80, 0)
    )

    val animateClock by animateAlpha(
        condition = startAnimation,
        startValue = 1f,
        endValue = 0f,
        delay = 500,
        duration = 30000,
        easing = LinearEasing
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        ClockTimer(
            modifier = Modifier
                .size(64.dp)
                .align(Alignment.TopEnd),
            time = remainingTime,
            progress = animateClock
        )
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(.7f)
                    .alpha(listOfAnimations[0].value)
            ) {
                Column(modifier = Modifier.align(Alignment.Center)) {
                    BasicText(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .fillMaxHeight(0.1f),
                        text = currentQuestion.difficulty.name.lowercase().replaceFirstChar {
                            it.uppercase()
                        },
                        fontSize = 25,
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp)
                    ) {
                        AutoResizeText(
                            text = currentQuestion.question,
                            modifier = Modifier
                                .align(Alignment.Center),
                            fontSizeRange = FontSizeRange(12.sp, 44.sp),
                            style = MaterialTheme.typography.displayLarge
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .weight(.3f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .animateContentSize()
                        .align(Alignment.Center)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    AnswerButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .weight(1f),
                        alphaAnimation = listOfAnimations[0].value,
                        positionAnimation = listOfPositionAnimations[0].value,
                        startSecondAnimation = startSecondAnimation,
                        answer = currentQuestion.listOfAnswers[0]
                    ) {
                        answerClick(currentQuestion.listOfAnswers[0])
                    }

                    AnswerButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .weight(1f),
                        alphaAnimation = listOfAnimations[1].value,
                        positionAnimation = listOfPositionAnimations[1].value,
                        startSecondAnimation = startSecondAnimation,
                        answer = currentQuestion.listOfAnswers[1]
                    ) {
                        answerClick(currentQuestion.listOfAnswers[1])
                    }

                    AnswerButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .weight(1f),
                        alphaAnimation = listOfAnimations[2].value,
                        positionAnimation = listOfPositionAnimations[2].value,
                        startSecondAnimation = startSecondAnimation,
                        answer = currentQuestion.listOfAnswers[2]
                    ) {
                        answerClick(currentQuestion.listOfAnswers[2])
                    }

                    AnswerButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .weight(1f),
                        alphaAnimation = listOfAnimations[3].value,
                        positionAnimation = listOfPositionAnimations[3].value,
                        startSecondAnimation = startSecondAnimation,
                        answer = currentQuestion.listOfAnswers[3]
                    ) {
                        answerClick(currentQuestion.listOfAnswers[3])
                    }
                }
            }
        }
    }
}

@Composable
fun AnswerButton(
    modifier: Modifier,
    alphaAnimation: Float,
    positionAnimation: IntOffset,
    startSecondAnimation: Boolean,
    answer: Answer,
    answerClick: () -> Unit
) {

    val animateColor by animateColorAsState(
        targetValue = when {
            startSecondAnimation && answer.isCorrect -> correctAnswer
            startSecondAnimation && answer.selected && !answer.isCorrect -> wrongAnswer
            else -> lessWhite
                           }
        /*if (startSecondAnimation && answer.selected == QuestionPickState.SELECTED) {
            if (answer.isCorrect) correctAnswer else wrongAnswer
        } else {
            lessWhite
        }*/, animationSpec = tween(
            durationMillis = if (!answer.selected && answer.isCorrect) 1000 else 500,
            easing = LinearOutSlowInEasing
        ), label = "animateColor"
    )

    Box(modifier = modifier
        .clip(RoundedCornerShape(16.dp))
        .alpha(alphaAnimation)
        .background(animateColor)
        .clickable {
            answerClick()
        }
        .offset {
            positionAnimation
        }
        .padding(8.dp)) {
        Column(
            modifier = Modifier.align(Alignment.Center)
        ) {
            AnimatedVisibility(visible = startSecondAnimation && answer.selected) {
                BasicText(text = if (answer.isCorrect) "Correct Answer" else "Wrong Answer")
            }
            if (!startSecondAnimation || answer.selected) {
                BasicText(text = answer.answerText)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSoloQuestionScreen() {
    NovaGincanaBiblicaTheme {

    }
}