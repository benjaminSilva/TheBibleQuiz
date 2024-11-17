package com.bsoftwares.thebiblequiz.ui.screens.games.quiz.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.repeatable
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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.bsoftwares.thebiblequiz.R
import com.bsoftwares.thebiblequiz.data.models.quiz.Answer
import com.bsoftwares.thebiblequiz.data.models.quiz.Question
import com.bsoftwares.thebiblequiz.ui.basicviews.AutoResizeText
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicText
import com.bsoftwares.thebiblequiz.ui.basicviews.FontSizeRange
import com.bsoftwares.thebiblequiz.ui.basicviews.animateAlpha
import com.bsoftwares.thebiblequiz.ui.basicviews.generateSubSequentialAlphaAnimations
import com.bsoftwares.thebiblequiz.ui.basicviews.generateSubSequentialPositionAnimations
import com.bsoftwares.thebiblequiz.ui.navigation.navigateWithoutRemembering
import com.bsoftwares.thebiblequiz.ui.screens.Routes
import com.bsoftwares.thebiblequiz.ui.screens.games.quiz.ClockTimer
import com.bsoftwares.thebiblequiz.ui.theme.NovaGincanaBiblicaTheme
import com.bsoftwares.thebiblequiz.ui.theme.achivoFontFamily
import com.bsoftwares.thebiblequiz.ui.theme.correctAnswer
import com.bsoftwares.thebiblequiz.ui.theme.wrongAnswer
import com.bsoftwares.thebiblequiz.viewmodel.BibleQuizViewModel


@Composable
fun InitializeQuizScreen(
    navController: NavHostController,
    soloViewModel: BibleQuizViewModel
) {

    val currentQuestionState by soloViewModel.currentQuestion.collectAsStateWithLifecycle()
    val navigateNextScreen by soloViewModel.nextDestination.collectAsStateWithLifecycle(false)
    val remainingTime by soloViewModel.remainingTime.collectAsStateWithLifecycle()
    val secondAnimation by soloViewModel.startSecondAnimation.collectAsStateWithLifecycle()
    val screenClickable by soloViewModel.screenClickable.collectAsStateWithLifecycle()

    var startAnimation by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(Unit) {
        startAnimation = false
        soloViewModel.startClock()
    }
    
    LaunchedEffect(navigateNextScreen) {
        if (navigateNextScreen) {
            navController.navigateWithoutRemembering(
                route = Routes.QuizResults,
                baseRoute = Routes.QuizMode
            )
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        // Create an observer that triggers our remembered callbacks
        // for sending analytics events
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                soloViewModel.updateQuestionResult(isCorrect = false)
            }
        }

        // Add the observer to the lifecycle
        lifecycleOwner.lifecycle.addObserver(observer)

        // When the effect leaves the Composition, remove the observer
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    SoloQuestionScreen(
        currentQuestion = currentQuestionState,
        startAnimation = startAnimation,
        remainingTime = remainingTime,
        screenClickable = screenClickable,
        startSecondAnimation = secondAnimation
    ) { answer ->
        soloViewModel.verifyAnswer(answer)
    }

}

@Composable
fun SoloQuestionScreen(
    currentQuestion: Question,
    startAnimation: Boolean,
    startSecondAnimation: Boolean,
    remainingTime: String,
    screenClickable: Boolean,
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
                            fontFamily = achivoFontFamily
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
                        answer = currentQuestion.listOfAnswers[0],
                        screenClickable = screenClickable
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
                        answer = currentQuestion.listOfAnswers[1],
                        screenClickable = screenClickable
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
                        answer = currentQuestion.listOfAnswers[2],
                        screenClickable = screenClickable
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
                        answer = currentQuestion.listOfAnswers[3],
                        screenClickable = screenClickable
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
    screenClickable: Boolean,
    answerClick: () -> Unit
) {


    val animateColor by animateColorAsState(
        targetValue = when {
            startSecondAnimation && answer.correct -> correctAnswer
            startSecondAnimation && answer.selected && !answer.correct -> wrongAnswer
            else -> colorResource(id = R.color.basic_container_color)
        }, animationSpec = if (!answer.selected && answer.correct) repeatable(
            iterations = 3,
            animation = tween(
                durationMillis = 200,
                easing = LinearOutSlowInEasing,
            ), repeatMode = RepeatMode.Reverse,
            initialStartOffset = StartOffset(500)
        ) else tween(
            durationMillis = 500,
            easing = LinearOutSlowInEasing
        ), label = "animateColor"
    )

    Box(modifier = modifier
        .clip(RoundedCornerShape(16.dp))
        .alpha(alphaAnimation)
        .background(animateColor)
        .clickable {
            if (screenClickable) {
                answerClick()
            }
        }
        .offset {
            positionAnimation
        }
        .padding(8.dp)) {
        Column(
            modifier = Modifier.align(Alignment.Center)
        ) {
            AnimatedVisibility(visible = startSecondAnimation && answer.selected) {
                BasicText(text = if (answer.correct) "Correct Answer" else "Wrong Answer")
            }
            if (!startSecondAnimation || !answer.selected) {
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