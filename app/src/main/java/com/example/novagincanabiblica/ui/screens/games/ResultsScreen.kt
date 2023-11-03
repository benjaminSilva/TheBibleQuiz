package com.example.novagincanabiblica.ui.screens.games

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.novagincanabiblica.R
import com.example.novagincanabiblica.data.models.Question
import com.example.novagincanabiblica.data.models.Session
import com.example.novagincanabiblica.data.models.state.QuestionAnswerState
import com.example.novagincanabiblica.ui.basicviews.BasicText
import com.example.novagincanabiblica.ui.basicviews.QuestionStats
import com.example.novagincanabiblica.ui.basicviews.animateAlpha
import com.example.novagincanabiblica.ui.basicviews.animateInt
import com.example.novagincanabiblica.ui.basicviews.animatePosition
import com.example.novagincanabiblica.ui.screens.Routes
import com.example.novagincanabiblica.ui.theme.NovaGincanaBiblicaTheme
import com.example.novagincanabiblica.ui.theme.almostWhite
import com.example.novagincanabiblica.ui.theme.lessWhite
import com.example.novagincanabiblica.viewmodel.BibleQuizViewModel

@Composable
fun InitializeSoloResultScreen(
    navController: NavHostController,
    soloViewModel: BibleQuizViewModel
) {
    val question by soloViewModel.currentQuestion.collectAsStateWithLifecycle()
    val session by soloViewModel.localSession.collectAsStateWithLifecycle()

    ResultsScreen(
        navController = navController,
        question = question,
        session = session
    )
}

@Composable
fun ResultsScreen(
    navController: NavHostController,
    question: Question,
    session: Session
) {

    var startAnimation by remember {
        mutableStateOf(true)
    }
    LaunchedEffect(Unit) {
        startAnimation = false
    }

    val animateDaysPosition by animatePosition(
        condition = startAnimation,
        startValue = IntOffset(-30, 0),
        endValue = IntOffset.Zero,
        duration = 500,
        delay = 500
    )

    val animateDaysAlpha by animateAlpha(condition = startAnimation, duration = 500, delay = 500)

    val animateStreak by animateInt(
        startAnimation = startAnimation,
        endValue = session.userStats.streak
    )

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
                            "Wrong answer... try again tomorrow."
                        }
                    )

                    QuestionGridResultView(
                        question = question
                    )
                }
            }

            if (!session.userInfo?.userId.isNullOrBlank()) {
                QuestionStats(data = session.userStats)
            }

            if (session.userInfo?.userId.isNullOrBlank()) {
                BasicText(text = "Login to Keep up with you stats, gain badges add friends.")
            }

            if (session.userStats.streak > 1) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(20.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(almostWhite)
                ) {
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .size(64.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(lessWhite)
                    ) {
                        BasicText(
                            modifier = Modifier.padding(16.dp).align(Alignment.Center),
                            text = animateStreak.toString(),
                            fontSize = 28
                        )
                    }

                    BasicText(
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .offset {
                                animateDaysPosition
                            }
                            .alpha(animateDaysAlpha),
                        text = "Days in streak", fontSize = 22
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.07f)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .shadow(20.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable {
                        navController.popBackStack(Routes.Home.value, false)
                    }
                    .background(almostWhite))
            {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(almostWhite),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Image(
                        modifier = Modifier
                            .padding(16.dp)
                            .size(24.dp),
                        painter = painterResource(id = R.drawable.baseline_share_24),
                        contentDescription = null
                    )
                    BasicText(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        text = "Share", fontSize = 16
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .shadow(20.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable {
                        navController.popBackStack(Routes.Home.value, false)
                    }
                    .background(almostWhite)
            ) {
                Image(
                    modifier = Modifier
                        .padding(16.dp)
                        .size(24.dp),
                    painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                    contentDescription = null
                )
                BasicText(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    text = "Go Back",
                    fontSize = 16
                )
            }
        }
    }
}

@Composable
fun QuestionGridResultView(question: Question) {
    Box {
        Column {
            BasicText(text = question.question, fontSize = 22)
            Row {
                BasicText(text = "Correct Answer:")
                BasicText(text = question.listOfAnswers.find { it.correct }?.answerText)
            }
            Row {
                BasicText(text = "Bible Verse:")
                BasicText(text = question.bibleVerse)
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
            question = Question(),
            session = Session()
        )
    }
}