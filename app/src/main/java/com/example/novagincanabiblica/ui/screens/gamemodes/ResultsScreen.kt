package com.example.novagincanabiblica.ui.screens.gamemodes

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.novagincanabiblica.R
import com.example.novagincanabiblica.data.models.Answer
import com.example.novagincanabiblica.data.models.ButtonAnswerState
import com.example.novagincanabiblica.data.models.Question
import com.example.novagincanabiblica.data.models.QuestionAnswerState
import com.example.novagincanabiblica.ui.theme.NovaGincanaBiblicaTheme
import com.example.novagincanabiblica.viewmodel.SoloModeViewModel

@Composable
fun InitializeSoloResultScreen(
    navController: NavHostController,
    soloViewModel: SoloModeViewModel
) {
    BackHandler {
        navController.popBackStack()
    }
    PreSoloScreen(
        navController = navController,
        questionsAnswered = soloViewModel.getAnsweredQuestions(),
        totalQuestionsAnsweredCorrectly = soloViewModel.getCorrectAnswerQuestionSize(),
        totalQuestion = soloViewModel.questions.size
    )
}

@Composable
fun PreSoloScreen(
    navController: NavHostController,
    questionsAnswered: List<Question>,
    totalQuestionsAnsweredCorrectly: Int,
    totalQuestion: Int
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn {

            item {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = "Congratulations, You scored $totalQuestionsAnsweredCorrectly/$totalQuestion"
                )
            }

            itemsIndexed(questionsAnswered) { index, question ->
                QuestionGridResultView(
                    question = question,
                    questionId = questionsAnswered.size - index
                )
            }

            item {
                Button(
                    modifier = Modifier.align(Alignment.Center),
                    onClick = { navController.popBackStack() }) {
                    Text(text = stringResource(R.string.go_back))
                }
            }
        }
    }
}

@Composable
fun QuestionGridResultView(question: Question, questionId: Int) {
    var showDetails by rememberSaveable {
        mutableStateOf(false)
    }
    Box(modifier = Modifier
        .animateContentSize()
        .clickable {
            showDetails = !showDetails
        }) {
        Column {
            Row {
                Text(text = "Question $questionId")
                when (question.answerState) {
                    QuestionAnswerState.ANSWERED_CORRECTLY -> Image(
                        painter = painterResource(id = R.drawable.baseline_check_24),
                        contentDescription = null
                    )

                    QuestionAnswerState.ANSWERED_WRONGLY -> Image(
                        painter = painterResource(id = R.drawable.baseline_close_24),
                        contentDescription = null
                    )

                    else -> Unit
                }
            }
            if (showDetails) {
                Text(text = question.question)
                Row {
                    Button(
                        onClick = { /*TODO*/ },
                        colors = question.listOfAnswers[0].displayAnswerColor()
                    ) {
                        Text(text = question.listOfAnswers[0].answerText)
                    }
                    Button(
                        onClick = { /*TODO*/ },
                        colors = question.listOfAnswers[1].displayAnswerColor()
                    ) {
                        Text(text = question.listOfAnswers[1].answerText)
                    }
                }
                Row {
                    Button(
                        onClick = { /*TODO*/ },
                        colors = question.listOfAnswers[2].displayAnswerColor()
                    ) {
                        Text(text = question.listOfAnswers[2].answerText)
                    }
                    Button(
                        onClick = { /*TODO*/ },
                        colors = question.listOfAnswers[3].displayAnswerColor()
                    ) {
                        Text(text = question.listOfAnswers[3].answerText)
                    }
                }
            }
        }
    }
}

@Composable
fun Answer.displayAnswerColor(): ButtonColors =
    ButtonDefaults.buttonColors(
        containerColor = when {
            isCorrect && selected -> ButtonAnswerState.CorrectAnswer.value
            selected -> ButtonAnswerState.WrongAnswerSelected.value
            else -> ButtonAnswerState.WrongAnswer.value
        }
    )


@Preview(showBackground = true)
@Composable
fun PreviewResultScreen() {
    NovaGincanaBiblicaTheme {
        PreSoloScreen(rememberNavController(), questionsAnswered = listOf(), 3, 5)
    }
}