package com.example.novagincanabiblica.ui.screens.games.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.novagincanabiblica.R
import com.example.novagincanabiblica.data.models.QuestionDifficulty
import com.example.novagincanabiblica.ui.basicviews.BasicButton
import com.example.novagincanabiblica.ui.basicviews.animateAlpha
import com.example.novagincanabiblica.ui.basicviews.animateAngle
import com.example.novagincanabiblica.ui.basicviews.animatePosition
import com.example.novagincanabiblica.ui.navigation.navigateWithoutRemembering
import com.example.novagincanabiblica.ui.screens.Routes
import com.example.novagincanabiblica.ui.theme.NovaGincanaBiblicaTheme
import com.example.novagincanabiblica.viewmodel.BibleQuizViewModel

@Composable
fun InitializePreSoloScreen(
    navController: NavHostController,
    soloViewModel: BibleQuizViewModel
) {
    val currentQuestion by soloViewModel.currentQuestion.collectAsStateWithLifecycle()
    PreSoloScreen(
        navController = navController,
        currentQuestionDifficulty = currentQuestion.difficulty
    ) {
        soloViewModel.updateSession()
        navController.navigateWithoutRemembering(route = Routes.Quiz)
    }
}

@Composable
fun PreSoloScreen(
    navController: NavHostController,
    currentQuestionDifficulty: QuestionDifficulty,
    startQuestionClick: () -> Unit
) {
    var startAnimation by remember {
        mutableStateOf(true)
    }
    LaunchedEffect(Unit) {
        startAnimation = false
    }

    //Question Text
    val animateTitleAngle by animateAngle(startAnimation, -20f, -5f)
    val animateQuestionPosition by animatePosition(
        startAnimation,
        IntOffset(-500, 0),
        IntOffset.Zero
    )
    val animateScreenAlpha by animateAlpha(startAnimation)

    //Question Number and buttons
    val animateNumberPosition by animatePosition(
        startAnimation,
        IntOffset(500, -70),
        IntOffset(0, -70)
    )
    val animateButtonsAlpha by animateAlpha(startAnimation, duration = 500, delay = 1000)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxHeight(0.75f)
                    .fillMaxWidth()
            ) {
                Column(modifier = Modifier.align(Alignment.Center)) {

                    Text(
                        modifier = Modifier
                            .rotate(animateTitleAngle)
                            .offset {
                                animateQuestionPosition
                            }
                            .alpha(animateScreenAlpha),
                        text = currentQuestionDifficulty.name,
                        style = MaterialTheme.typography.displayLarge,
                        fontSize = when (currentQuestionDifficulty) {
                            QuestionDifficulty.EASY -> 115.sp
                            QuestionDifficulty.MEDIUM -> 75.sp
                            QuestionDifficulty.HARD -> 115.sp
                            QuestionDifficulty.IMPOSSIBLE -> 45.sp
                        }
                    )
                    Text(
                        modifier = Modifier
                            .align(Alignment.End)
                            .rotate(animateTitleAngle)
                            .offset {
                                animateNumberPosition
                            }
                            .alpha(animateScreenAlpha),
                        text = "Question",
                        style = MaterialTheme.typography.displayLarge
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(animateButtonsAlpha)
            ) {
                Row(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    BasicButton(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(0.3f)
                            .clip(RoundedCornerShape(16.dp)),
                        text = stringResource(id = R.string.go_back)
                    ) {
                        navController.popBackStack()
                    }
                    BasicButton(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(0.7f)
                            .clip(RoundedCornerShape(16.dp)),
                        text = stringResource(R.string.start_question)
                    ) {
                        startQuestionClick()
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
        PreSoloScreen(rememberNavController(), QuestionDifficulty.EASY) {

        }
    }
}