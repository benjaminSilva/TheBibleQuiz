package com.example.novagincanabiblica.ui.screens.games

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.novagincanabiblica.R
import com.example.novagincanabiblica.data.models.Answer
import com.example.novagincanabiblica.data.models.Question
import com.example.novagincanabiblica.data.models.Session
import com.example.novagincanabiblica.data.models.state.ButtonAnswerState
import com.example.novagincanabiblica.data.models.state.QuestionAnswerState
import com.example.novagincanabiblica.ui.basicviews.BasicButton
import com.example.novagincanabiblica.ui.basicviews.BasicText
import com.example.novagincanabiblica.ui.basicviews.QuestionStats
import com.example.novagincanabiblica.ui.basicviews.generateStatsData
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
    val session by soloViewModel.sessionState.collectAsStateWithLifecycle()

    PreSoloScreen(
        navController = navController,
        question = question,
        session = session
    )
}

@Composable
fun PreSoloScreen(
    navController: NavHostController,
    question: Question,
    session: Session
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
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
                    BasicText(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        text = if (question.answerState == QuestionAnswerState.ANSWERED_CORRECTLY) {
                            "You got it!"
                        } else {
                            "Almost, tomorrow you can try again!"
                        }
                    )

                    QuestionGridResultView(
                        question = question
                    )
                }
            }
            
            QuestionStats(questionStatsData = generateStatsData())

            if (session.data?.userId.isNullOrBlank()) {
                BasicText(text = "Login to Keep up with you stats and add friends.")
            }

            BasicButton(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = stringResource(R.string.go_back)
            ) {
                navController.popBackStack(Routes.Home.value, false)
            }
        }
    }
}

@Composable
fun QuestionGridResultView(question: Question) {
    Box {
        Column {
            BasicText(text = question.question)
            Row {
                BasicText(text = "Correct Answer:")
                BasicText(text = question.listOfAnswers.find { it.isCorrect }?.answerText)
            }
            Row {
                BasicText(text = "Bible Verse:")
                BasicText(text = question.bibleVerse)
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