package com.example.novagincanabiblica.ui.screens.games.quiz.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.novagincanabiblica.R
import com.example.novagincanabiblica.data.models.quiz.QuestionDifficulty
import com.example.novagincanabiblica.data.models.state.QuizDialogType
import com.example.novagincanabiblica.ui.basicviews.BasicContainer
import com.example.novagincanabiblica.ui.basicviews.BasicText
import com.example.novagincanabiblica.ui.basicviews.animateAlpha
import com.example.novagincanabiblica.ui.basicviews.animateAngle
import com.example.novagincanabiblica.ui.basicviews.animatePosition
import com.example.novagincanabiblica.ui.navigation.navigateWithoutRemembering
import com.example.novagincanabiblica.ui.screens.Routes
import com.example.novagincanabiblica.ui.screens.games.quiz.HowToPlayQuizDialog
import com.example.novagincanabiblica.ui.theme.NovaGincanaBiblicaTheme
import com.example.novagincanabiblica.viewmodel.BibleQuizViewModel

@Composable
fun InitializePreSoloScreen(
    navController: NavHostController,
    soloViewModel: BibleQuizViewModel
) {
    val currentQuestion by soloViewModel.currentQuestion.collectAsStateWithLifecycle()
    val displayDialog by soloViewModel.displayDialog.collectAsStateWithLifecycle()

    val (dialogType, displayIt) = displayDialog

    if (displayIt) {
        Dialog(onDismissRequest = {
            soloViewModel.displayDialog(
                displayIt = false
            )
        }) {
            HowToPlayQuizDialog()
        }
    }

    PreSoloScreen(
        navController = navController,
        currentQuestionDifficulty = currentQuestion.difficulty,
        openHowToPlayQuestionDialog = {
            soloViewModel.displayDialog(dialogType = QuizDialogType.HowToPlay, displayIt = true)
        }
    ) {
        soloViewModel.updateGameAvailability()
        navController.navigateWithoutRemembering(route = Routes.Quiz, baseRoute = Routes.QuizMode)
    }
}

@Composable
fun PreSoloScreen(
    navController: NavHostController,
    currentQuestionDifficulty: QuestionDifficulty,
    openHowToPlayQuestionDialog: () -> Unit,
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
            ) {
                Row(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(
                        modifier = Modifier.weight(0.3f),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        BasicContainer(modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .weight(1f),
                            shadowAlpha = animateButtonsAlpha, onClick = {
                                openHowToPlayQuestionDialog()
                            }) {
                            BasicText(
                                modifier = Modifier.align(Alignment.Center),
                                text = "How to play"
                            )
                        }

                        BasicContainer(modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .weight(1f),
                            shadowAlpha = animateButtonsAlpha, onClick = {
                                navController.navigate(Routes.SuggestQuestion.value)
                            }) {
                            BasicText(
                                modifier = Modifier.align(Alignment.Center),
                                textAlign = TextAlign.Center,
                                text = "Suggest a question"
                            )
                        }

                        BasicContainer(modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .weight(1f),
                            shadowAlpha = animateButtonsAlpha, onClick = {
                                navController.popBackStack()
                            }) {
                            BasicText(
                                modifier = Modifier.align(Alignment.Center), text = stringResource(
                                    id = R.string.go_back
                                )
                            )
                        }
                    }

                    BasicContainer(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth()
                            .weight(0.7f),
                        shadowAlpha = animateButtonsAlpha,
                        onClick = {
                            startQuestionClick()
                        }
                    ) {
                        BasicText(
                            modifier = Modifier.align(Alignment.Center),
                            text = stringResource(id = R.string.start_question)
                        )
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
        PreSoloScreen(rememberNavController(), QuestionDifficulty.EASY, {}) {

        }
    }
}