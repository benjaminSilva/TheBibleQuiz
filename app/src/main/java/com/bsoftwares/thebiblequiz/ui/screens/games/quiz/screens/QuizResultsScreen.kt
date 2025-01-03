package com.bsoftwares.thebiblequiz.ui.screens.games.quiz.screens

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicContainer
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicScreenBox
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicText
import com.bsoftwares.thebiblequiz.ui.screens.Routes
import com.bsoftwares.thebiblequiz.ui.screens.games.quiz.QuizStats
import com.bsoftwares.thebiblequiz.ui.theme.NovaGincanaBiblicaTheme
import com.bsoftwares.thebiblequiz.ui.theme.disableClicks
import com.bsoftwares.thebiblequiz.ui.theme.emptyString
import com.bsoftwares.thebiblequiz.ui.theme.enableClicks
import com.bsoftwares.thebiblequiz.viewmodel.BibleQuizViewModel
import kotlinx.coroutines.delay

@Composable
fun InitializeQuizResultScreen(
    navController: NavHostController,
    soloViewModel: BibleQuizViewModel
) {
    val question by soloViewModel.currentQuestion.collectAsStateWithLifecycle()
    val session by soloViewModel.localSession.collectAsStateWithLifecycle()
    val calculatedData by soloViewModel.calculatedQuizData.collectAsStateWithLifecycle()
    val correctAnswer by soloViewModel.correctAnswer.collectAsStateWithLifecycle()
    val day by soloViewModel.day.collectAsStateWithLifecycle()
    val isNewDay by soloViewModel.isNewDay.collectAsStateWithLifecycle()

    var enabled by remember {
        mutableStateOf(enableClicks())
    }

    LaunchedEffect(enabled) {
        if (!enabled.first) {
            enabled.second()
            delay(1000)
            enabled = enableClicks()
        }
    }

    LaunchedEffect(isNewDay) {
        if (isNewDay) {
            navController.popBackStack()
        }
    }

    LaunchedEffect(session) {
        soloViewModel.calculateQuizData()
    }

    val context = LocalContext.current

    val correctOrWrongString =
        if (session.quizStats.answerSelected == correctAnswer.answerText) stringResource(
            R.string.i_answered_the_question_correctly_today
        ) else stringResource(R.string.i_got_the_answer_wrong)

    val intent = Intent.createChooser(Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(
            Intent.EXTRA_TEXT,
            stringResource(
                R.string.the_bible_quiz_day_you_should_try_it_out,
                day,
                correctOrWrongString,
                String(Character.toChars(0x1F440))
            )
        )
        type = "text/plain"
    }, null)

    val shareAnswerIntent = Intent.createChooser(intent, null)

    BasicScreenBox(enabled = enabled.first) {
        ResultsScreen(
            navController = navController,
            question = question,
            session = session,
            calculatedData = calculatedData,
            correctAnswer = correctAnswer
        ) {
            enabled = disableClicks {
                context.startActivity(shareAnswerIntent)
            }
        }
    }
}

@Composable
fun ResultsScreen(
    navController: NavHostController,
    question: Question,
    session: Session,
    calculatedData: QuestionStatsDataCalculated,
    correctAnswer: Answer,
    shareAnswer: () -> Unit
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
                    Row(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        BasicText(
                            text = when (session.quizStats.answerSelected) {
                                emptyString -> stringResource(R.string.you_didn_t_select_an_answer)
                                correctAnswer.answerText -> stringResource(R.string.you_got_it)
                                else -> stringResource(R.string.you_got_it_wrong)
                            },
                            fontSize = 18
                        )
                        Image(
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .size(24.dp),
                            painter = painterResource(id = if (correctAnswer.answerText == session.quizStats.answerSelected) R.drawable.baseline_check_24_bw else R.drawable.baseline_close_24_bw),
                            contentDescription = emptyString,
                        )
                    }

                    QuestionGridResultView(
                        question = question,
                        selectedAnswer = session.quizStats.answerSelected,
                        correctAnswer = correctAnswer
                    )

                }
            }
            QuizStats(data = session.quizStats, calculatedData = calculatedData)
        }

        BackAndShare(modifier = Modifier
            .fillMaxHeight(0.07f)
            .align(Alignment.BottomCenter), goBackClick = {
            navController.popBackStack(Routes.Home.value, false)
        }) {
            shareAnswer()
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
        BasicContainer(modifier = Modifier.weight(1f), onClick = { shareClick() }) {
            Row(
                modifier = Modifier.padding(16.dp),
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
            Row(
                modifier = Modifier.padding(16.dp),
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
fun QuestionGridResultView(question: Question, correctAnswer: Answer, selectedAnswer: String) {
    Box {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            BasicText(text = question.question, fontSize = 22)
            Spacer(modifier = Modifier.size(8.dp))
            Row {
                BasicText(text = stringResource(id = R.string.correct_answer))
                BasicText(
                    modifier = Modifier.padding(start = 8.dp),
                    text = correctAnswer.answerText
                )
            }
            if (selectedAnswer != correctAnswer.answerText) {
                Row {
                    BasicText(text = stringResource(id = R.string.selected_answer))
                    BasicText(
                        modifier = Modifier.padding(start = 8.dp),
                        text = selectedAnswer.ifEmpty {
                            stringResource(
                                R.string.none
                            )
                        })
                }
            }
            if (question.bibleVerse.isNotEmpty()) {
                Row {
                    BasicText(text = stringResource(id = R.string.bible_verse))
                    BasicText(modifier = Modifier.padding(start = 8.dp), text = question.bibleVerse)
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
            question = Question(
                question = "Who did something bad at some point?",
                bibleVerse = "Answer found in this verse Test 22:1"
            ),
            session = Session(),
            calculatedData = QuestionStatsDataCalculated(),
            correctAnswer = Answer(answerText = "Very nice answer")
        ) {

        }
    }
}