package com.example.novagincanabiblica.ui.screens.gamemodes.solomode

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.novagincanabiblica.data.models.Answer
import com.example.novagincanabiblica.data.models.Question
import com.example.novagincanabiblica.data.models.AnswerDestinationState
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

    handleAnotherNavigation(
        navController = navController,
        answerState = answerState
    )

    SoloQuestionScreen(
        navController = navController,
        currentQuestion = currentQuestionState
    ) { answer ->
        soloViewModel.verifyAnswer(answer)
    }

}

//This seems more proper
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

//This Works Faster
/*fun handleNavigation(
    isCorrect: Boolean,
    navController: NavHostController,
    soloViewModel: SoloModeViewModel
) {
    if (isCorrect) {
        navController.navigate(Routes.SOLOPREQUESTION.value) {
            popUpTo(Routes.SOLOPREQUESTION.value) {
                inclusive = true
            }
        }
    } else {
        navController.navigate(Routes.HOME.value) {
            popUpTo(Routes.HOME.value) {
                inclusive = true
            }
        }
    }
    soloViewModel.updateQuestionNumber()
}*/

@Composable
fun SoloQuestionScreen(
    navController: NavHostController,
    currentQuestion: Question,
    answerClick: (Answer) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column {
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = currentQuestion.question
            )
            Button(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = { answerClick(currentQuestion.listOfAnswers[0]) }) {
                Text(text = currentQuestion.listOfAnswers[0].answerText)
            }
            Button(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = { answerClick(currentQuestion.listOfAnswers[1]) }) {
                Text(text = currentQuestion.listOfAnswers[1].answerText)
            }
            Button(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = { answerClick(currentQuestion.listOfAnswers[2]) }) {
                Text(text = currentQuestion.listOfAnswers[2].answerText)
            }
            Button(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = { answerClick(currentQuestion.listOfAnswers[3]) }) {
                Text(text = currentQuestion.listOfAnswers[3].answerText)
            }
            Button(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = {
                    navController.popBackStack()
                }) {
                Text(text = "Give Up")
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
                ).shuffled(),
            )
        ) {}
    }
}