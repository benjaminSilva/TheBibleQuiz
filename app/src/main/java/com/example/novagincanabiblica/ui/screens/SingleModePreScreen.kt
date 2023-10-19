package com.example.novagincanabiblica.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.novagincanabiblica.R
import com.example.novagincanabiblica.data.models.SoloGameMode
import com.example.novagincanabiblica.ui.theme.NovaGincanaBiblicaTheme
import com.example.novagincanabiblica.viewmodel.SoloModeViewModel

@Composable
fun PreSoloScreen(
    navController: NavHostController,
    context: Context,
    viewModel: SoloModeViewModel
) {
    val questions by viewModel.question.collectAsState()
    runCatching {
        viewModel.loadQuestionsForSoloMode(context.assets.open("game.json").bufferedReader().use {
            it.readText()
        })
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Column {
            if (questions.isNotEmpty()) {
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = "Question 1"
                )
            }
            Column {
                Button(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = { }) {
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

@Preview(showBackground = true)
@Composable
fun PreviewPreSoloScreen() {
    NovaGincanaBiblicaTheme {
        PreSoloScreen(rememberNavController(), LocalContext.current, SoloModeViewModel())
    }
}