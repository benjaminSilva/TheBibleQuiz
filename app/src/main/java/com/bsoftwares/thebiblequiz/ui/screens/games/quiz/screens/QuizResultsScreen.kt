package com.bsoftwares.thebiblequiz.ui.screens.games.quiz.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bsoftwares.thebiblequiz.R
import com.bsoftwares.thebiblequiz.data.models.QuestionStatsDataCalculated
import com.bsoftwares.thebiblequiz.data.models.Session
import com.bsoftwares.thebiblequiz.data.models.quiz.Answer
import com.bsoftwares.thebiblequiz.data.models.quiz.Question
import com.bsoftwares.thebiblequiz.data.models.state.QuestionAnswerState
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicContainer
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicText
import com.bsoftwares.thebiblequiz.ui.screens.Routes
import com.bsoftwares.thebiblequiz.ui.screens.games.quiz.QuizStats
import com.bsoftwares.thebiblequiz.ui.theme.NovaGincanaBiblicaTheme
import com.bsoftwares.thebiblequiz.viewmodel.BibleQuizViewModel

@Composable
fun InitializeSoloResultScreen(
    navController: NavHostController,
    soloViewModel: BibleQuizViewModel
) {
    val question by soloViewModel.currentQuestion.collectAsStateWithLifecycle()
    val session by soloViewModel.localSession.collectAsStateWithLifecycle()
    val calculatedData by soloViewModel.calculatedQuizData.collectAsStateWithLifecycle()
    val correctAnswer by soloViewModel.correctAnswer.collectAsStateWithLifecycle()
    val selectedAnswer by soloViewModel.selectedAnswer.collectAsStateWithLifecycle()

    LaunchedEffect(session) {
        soloViewModel.calculateQuizData()
    }

    ResultsScreen(
        navController = navController,
        question = question,
        session = session,
        calculatedData = calculatedData,
        selectedAnswer = selectedAnswer,
        correctAnswer = correctAnswer
    )
}

@Composable
fun ResultsScreen(
    navController: NavHostController,
    question: Question,
    session: Session,
    calculatedData: QuestionStatsDataCalculated,
    selectedAnswer: Answer,
    correctAnswer: Answer
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight(0.93f)
                .align(Alignment.TopCenter),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BasicContainer {
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
                            "Wrong answer... try again tomorrow."
                        }
                    )

                    QuestionGridResultView(
                        question = question,
                        selectedAnswer = selectedAnswer,
                        correctAnswer = correctAnswer
                    )
                }
            }

            if (!session.userInfo?.userId.isNullOrBlank()) {
                QuizStats(data = session.quizStats, calculatedData = calculatedData)
            } else {
                BasicText(text = "Login to Keep up with you stats, gain badges add friends.")
            }

        }

        BackAndShare(modifier = Modifier
            .fillMaxHeight(0.07f)
            .align(Alignment.BottomCenter), goBackClick = {
            navController.popBackStack(Routes.Home.value, false)
        }) {

        }
    }
}

@Composable
fun BackAndShare(modifier: Modifier, goBackClick: () -> Unit, shareClick: () -> Unit) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        BasicContainer(modifier = Modifier.weight(1f), onClick = {shareClick()}) {
            Row (modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Image(
                    modifier = Modifier
                        .size(24.dp),
                    painter = painterResource(id = R.drawable.baseline_share_24),
                    contentDescription = null
                )
                BasicText(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    text = stringResource(id = R.string.share), fontSize = 16
                )
            }
        }

        BasicContainer(modifier = Modifier.weight(1f), onClick = {
            goBackClick()
        }) {
            Row (modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Image(
                    modifier = Modifier
                        .size(24.dp),
                    painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                    contentDescription = null
                )
                BasicText(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    text = stringResource(id = R.string.go_back),
                    fontSize = 16
                )
            }
        }
    }
}

@Composable
fun QuestionGridResultView(question: Question, correctAnswer: Answer, selectedAnswer: Answer) {
    Box {
        Column (verticalArrangement = Arrangement.spacedBy(4.dp)) {
            BasicText(text = question.question, fontSize = 22)
            Row {
                BasicText(text = stringResource(id = R.string.correct_answer))
                BasicText(text = correctAnswer.answerText)
            }
            if (!selectedAnswer.correct) {
                Row {
                    BasicText(text = stringResource(id = R.string.selected_answer))
                    BasicText(text = selectedAnswer.answerText)
                }
            }
            if (question.bibleVerse.isNotEmpty()) {
                Row {
                    BasicText(text = stringResource(id = R.string.bible_verse))
                    BasicText(text = question.bibleVerse)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewResultScreen() {
    NovaGincanaBiblicaTheme {
        ResultsScreen(
            navController = rememberNavController(),
            question = Question(question = "Who did something bad at some point?", bibleVerse = "Answer found in this verse Test 22:1"),
            session = Session(),
            calculatedData = QuestionStatsDataCalculated(),
            correctAnswer = Answer(answerText = "Very nice answer"),
            selectedAnswer = Answer(answerText = "Wrong answer")
        )
    }
}