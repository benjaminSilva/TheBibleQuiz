package com.example.novagincanabiblica.ui.screens.gamemodes.solomode

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.novagincanabiblica.R
import com.example.novagincanabiblica.ui.navigation.navigateWithoutRemembering
import com.example.novagincanabiblica.ui.screens.Routes
import com.example.novagincanabiblica.ui.theme.NovaGincanaBiblicaTheme
import com.example.novagincanabiblica.viewmodel.SoloModeViewModel

@Composable
fun InitializePreSoloScreen(
    navController: NavHostController,
    soloViewModel: SoloModeViewModel
) {
    LaunchedEffect(Unit) {
        soloViewModel.setupNewQuestion()
    }

    val questionNumber by soloViewModel.currentQuestionNumber.collectAsStateWithLifecycle()
    PreSoloScreen(
        navController = navController,
        questionNumber = questionNumber
    )
}

@Composable
fun PreSoloScreen(
    navController: NavHostController,
    questionNumber: Int
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column {
            Box(modifier = Modifier
                .fillMaxHeight(0.5f)
                .fillMaxWidth()) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = "Question $questionNumber",
                    style = MaterialTheme.typography.headlineLarge
                )
            }
            Box(modifier = Modifier
                .fillMaxHeight(0.5f)
                .fillMaxWidth()) {
                Column (modifier = Modifier.align(Alignment.Center)) {
                    Button(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        onClick = { navController.navigateWithoutRemembering(route = Routes.SoloModeQuestion) }) {
                        Text(text = stringResource(R.string.start_question))
                    }
                    Button(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        onClick = { navController.popBackStack() }) {
                        Text(text = stringResource(R.string.go_back))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPreSoloScreen() {
    NovaGincanaBiblicaTheme {
        PreSoloScreen(rememberNavController(), 8)
    }
}