package com.example.novagincanabiblica.ui.screens.solomode

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
import com.example.novagincanabiblica.data.models.Question
import com.example.novagincanabiblica.ui.theme.NovaGincanaBiblicaTheme
import com.example.novagincanabiblica.viewmodel.SoloModeViewModel


@Composable
fun InitializeSoloQuestionScreen(navController: NavHostController, soloViewModel: SoloModeViewModel) {
    val currentQuestion by soloViewModel.currentQuestion.collectAsStateWithLifecycle()
    SoloQuestionScreen(navController = navController, currentQuestion)
}

@Composable
fun SoloQuestionScreen(
    navController: NavHostController,
    currentQuestion: Question
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
                onClick = { }) {
                Text(text = currentQuestion.listOfAnswers[0])
            }
            Button(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = { navController.popBackStack() }) {
                Text(text = currentQuestion.listOfAnswers[1])
            }
            Button(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = { navController.popBackStack() }) {
                Text(text = currentQuestion.listOfAnswers[2])
            }
            Button(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = { navController.popBackStack() }) {
                Text(text = currentQuestion.listOfAnswers[3])
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
                listOfAnswers = listOf("He", "David", "Eric", "Leia")
            )
        )
    }
}