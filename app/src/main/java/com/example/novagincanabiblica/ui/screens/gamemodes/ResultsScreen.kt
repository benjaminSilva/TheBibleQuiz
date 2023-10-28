package com.example.novagincanabiblica.ui.screens.gamemodes

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.novagincanabiblica.R
import com.example.novagincanabiblica.data.models.Answer
import com.example.novagincanabiblica.data.models.state.ButtonAnswerState
import com.example.novagincanabiblica.data.models.Question
import com.example.novagincanabiblica.ui.basicviews.BasicButton
import com.example.novagincanabiblica.ui.basicviews.BasicText
import com.example.novagincanabiblica.ui.screens.Routes
import com.example.novagincanabiblica.ui.theme.NovaGincanaBiblicaTheme
import com.example.novagincanabiblica.ui.theme.almostWhite
import com.example.novagincanabiblica.viewmodel.SoloModeViewModel

@Composable
fun InitializeSoloResultScreen(
    navController: NavHostController,
    soloViewModel: SoloModeViewModel
) {
    val question by soloViewModel.currentQuestion.collectAsStateWithLifecycle()
    PreSoloScreen(
        navController = navController,
        question = question
    )
}

@Composable
fun PreSoloScreen(
    navController: NavHostController,
    question: Question
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(20.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(almostWhite)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    BasicText(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        text = "Some text for Result"
                    )

                    QuestionGridResultView(
                        question = question
                    )
                }

            }


            Button(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = {
                    navController.popBackStack(Routes.Home.value, false)
                }) {
                Text(text = stringResource(R.string.go_back))
            }
        }
    }
}

@Composable
fun QuestionGridResultView(question: Question) {
    Box {
        Column {
            Text(text = question.question)
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                BasicButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    text = question.listOfAnswers[0].answerText,
                    colors = question.listOfAnswers[0].displayAnswerColor()
                )
                BasicButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    text = question.listOfAnswers[1].answerText,
                    colors = question.listOfAnswers[1].displayAnswerColor()
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                BasicButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    text = question.listOfAnswers[2].answerText,
                    colors = question.listOfAnswers[2].displayAnswerColor()
                )
                BasicButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    text = question.listOfAnswers[3].answerText,
                    colors = question.listOfAnswers[3].displayAnswerColor()
                )
            }
        }
    }
}

@Composable
fun Answer.displayAnswerColor(): ButtonColors =
    ButtonDefaults.buttonColors(
        containerColor = when {
            isCorrect && selected -> ButtonAnswerState.CorrectAnswer.value
            isCorrect -> ButtonAnswerState.CorrectAnswer.value
            selected -> ButtonAnswerState.WrongAnswerSelected.value
            else -> ButtonAnswerState.WrongAnswer.value
        }
    )


@Preview(showBackground = true)
@Composable
fun PreviewResultScreen() {
    NovaGincanaBiblicaTheme {

    }
}