package com.example.novagincanabiblica.ui.screens.gamemodes.solomode

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.novagincanabiblica.data.models.Answer
import com.example.novagincanabiblica.data.models.Question
import com.example.novagincanabiblica.data.models.state.AnswerDestinationState
import com.example.novagincanabiblica.ui.basicviews.AutoResizeText
import com.example.novagincanabiblica.ui.basicviews.BasicButton
import com.example.novagincanabiblica.ui.basicviews.BasicText
import com.example.novagincanabiblica.ui.basicviews.FontSizeRange
import com.example.novagincanabiblica.ui.basicviews.animateAlpha
import com.example.novagincanabiblica.ui.basicviews.animatePosition
import com.example.novagincanabiblica.ui.navigation.navigateWithoutRemembering
import com.example.novagincanabiblica.ui.screens.Routes
import com.example.novagincanabiblica.ui.theme.NovaGincanaBiblicaTheme
import com.example.novagincanabiblica.viewmodel.SoloModeViewModel


@Composable
fun InitializeSoloQuestionScreen(
    navController: NavHostController,
    soloViewModel: SoloModeViewModel
) {

    val currentQuestionState by soloViewModel.currentQuestion.collectAsStateWithLifecycle()
    val answerState by soloViewModel.nextDestination.collectAsStateWithLifecycle(initialValue = AnswerDestinationState.STAY)
    val currentQuestionNumber by soloViewModel.currentQuestionNumber.collectAsStateWithLifecycle()

    var startAnimation by remember {
        mutableStateOf(true)
    }
    LaunchedEffect(Unit) {
        startAnimation = false

    }

    handleAnotherNavigation(
        navController = navController,
        answerState = answerState
    )

    SoloQuestionScreen(
        navController = navController,
        currentQuestion = currentQuestionState,
        currentQuestionNumber = currentQuestionNumber,
        startAnimation = startAnimation
    ) { answer ->
        soloViewModel.verifyAnswer(answer)
    }

}

fun handleAnotherNavigation(
    navController: NavHostController,
    answerState: AnswerDestinationState
) {
    when (answerState) {
        AnswerDestinationState.NEXT_QUESTION -> navController.navigateWithoutRemembering(
            route = Routes.SoloModePreQuestion
        )

        AnswerDestinationState.RESULTS -> navController.navigateWithoutRemembering(
            route = Routes.Results
        )

        else -> Unit
    }
}

@Composable
fun SoloQuestionScreen(
    navController: NavHostController,
    currentQuestion: Question,
    currentQuestionNumber: Int,
    startAnimation: Boolean,
    answerClick: (Answer) -> Unit
) {

    //Animate answer 1
    val animateQuestionPosition1 by animatePosition(
        startAnimation,
        IntOffset(-500, 0),
        IntOffset.Zero,
        duration = 500,
        delay = 200
    )
    val animateScreenAlpha1 by animateAlpha(startAnimation, duration = 500, delay = 500)

    //Animate answer 2
    val animateQuestionPosition2 by animatePosition(
        startAnimation,
        IntOffset(-500, 0),
        IntOffset.Zero,
        duration = 500,
        delay = 400
    )
    val animateScreenAlpha2 by animateAlpha(startAnimation, duration = 500, delay = 700)

    //Animate answer 3
    val animateQuestionPosition3 by animatePosition(
        startAnimation,
        IntOffset(-500, 0),
        IntOffset.Zero,
        duration = 500,
        delay = 600
    )
    val animateScreenAlpha3 by animateAlpha(startAnimation, duration = 500, delay = 900)

    //Animate answer 4
    val animateQuestionPosition4 by animatePosition(
        startAnimation,
        IntOffset(-500, 0),
        IntOffset.Zero,
        duration = 500,
        delay = 800
    )
    val animateScreenAlpha4 by animateAlpha(startAnimation, duration = 500, delay = 1100)

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight(.6f)
                .alpha(animateScreenAlpha1)
        ) {
            Column(modifier = Modifier.align(Alignment.Center)) {
                BasicText(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxHeight(0.1f),
                    text = when (currentQuestionNumber) {
                        1 -> "Easy"
                        2 -> "Medium"
                        3 -> "Hard"
                        else -> "Pastor Level"
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
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 16.dp)
            ) {
                BasicButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                        .alpha(animateScreenAlpha1)
                        .offset {
                            animateQuestionPosition1
                        },
                    text = currentQuestion.listOfAnswers[0].answerText
                ) {
                    answerClick(currentQuestion.listOfAnswers[0])
                }
                BasicButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                        .alpha(animateScreenAlpha2)
                        .offset {
                            animateQuestionPosition2
                        },
                    text = currentQuestion.listOfAnswers[1].answerText
                ) {
                    answerClick(currentQuestion.listOfAnswers[1])
                }
                BasicButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                        .alpha(animateScreenAlpha3)
                        .offset {
                            animateQuestionPosition3
                        },
                    text = currentQuestion.listOfAnswers[2].answerText
                ) {
                    answerClick(currentQuestion.listOfAnswers[2])
                }
                BasicButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                        .alpha(animateScreenAlpha4)
                        .offset {
                            animateQuestionPosition4
                        },
                    text = currentQuestion.listOfAnswers[3].answerText
                ) {
                    answerClick(currentQuestion.listOfAnswers[3])
                }
                BasicButton(
                    modifier = Modifier.align(Alignment.Start),
                    text = "Give Up"
                ) {
                    navController.popBackStack()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSoloQuestionScreen() {
    NovaGincanaBiblicaTheme {
        SoloQuestionScreen(
            rememberNavController(),
            Question(
                question = "Who did that?",
                listOfAnswers = listOf(
                    Answer("David", true),
                    Answer("Eric", true),
                    Answer("Leia", true),
                    Answer("Abbie", true)
                ).shuffled()
            ), 1, true
        ) {}
    }
}