package com.bsoftwares.thebiblequiz.ui.screens.games.quiz.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bsoftwares.thebiblequiz.R
import com.bsoftwares.thebiblequiz.data.models.quiz.QuestionDifficulty
import com.bsoftwares.thebiblequiz.data.models.state.DialogType
import com.bsoftwares.thebiblequiz.data.models.state.QuizDialogType
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicContainer
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicDialog
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicScreenBox
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicText
import com.bsoftwares.thebiblequiz.ui.basicviews.animateAlpha
import com.bsoftwares.thebiblequiz.ui.basicviews.animateAngle
import com.bsoftwares.thebiblequiz.ui.basicviews.animateDp
import com.bsoftwares.thebiblequiz.ui.basicviews.animatePosition
import com.bsoftwares.thebiblequiz.ui.navigation.navigateWithoutRemembering
import com.bsoftwares.thebiblequiz.ui.screens.Routes
import com.bsoftwares.thebiblequiz.ui.screens.games.quiz.HowToPlayQuizDialog
import com.bsoftwares.thebiblequiz.ui.theme.NovaGincanaBiblicaTheme
import com.bsoftwares.thebiblequiz.ui.theme.achivoFontFamily
import com.bsoftwares.thebiblequiz.viewmodel.BibleQuizViewModel

@Composable
fun InitializePreQuizScreen(
    navController: NavHostController,
    soloViewModel: BibleQuizViewModel
) {
    val currentQuestion by soloViewModel.currentQuestion.collectAsStateWithLifecycle()
    val dialog by soloViewModel.displayDialog.collectAsStateWithLifecycle()

    var displayDialog by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(dialog) {
        displayDialog = dialog != DialogType.EmptyValue
    }

    if (displayDialog) {
        BasicDialog(onDismissRequest = {
            soloViewModel.updateDialog()
        }) {
            HowToPlayQuizDialog()
        }
    }

    BasicScreenBox {
        if (currentQuestion.question.isNotEmpty()) {
            PreSoloScreen(
                navController = navController,
                currentQuestionDifficulty = currentQuestion.difficulty,
                openHowToPlayQuestionDialog = {
                    soloViewModel.updateDialog(dialogType = QuizDialogType.HowToPlay)
                }
            ) {
                soloViewModel.updateGameAvailability()
                navController.navigateWithoutRemembering(route = Routes.Quiz, baseRoute = Routes.QuizMode)
            }
        }
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
    val animateShadow by animateDp(condition = startAnimation, delay = 1500, endValue = 20.dp)

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
                        fontSize = when (currentQuestionDifficulty) {
                            QuestionDifficulty.EASY -> 115.sp
                            QuestionDifficulty.MEDIUM -> 75.sp
                            QuestionDifficulty.HARD -> 115.sp
                            QuestionDifficulty.IMPOSSIBLE -> 45.sp
                        },
                        fontFamily = achivoFontFamily,
                        color = colorResource(id = R.color.contrast_color)
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
                        fontSize = 45.sp,
                        fontFamily = achivoFontFamily,
                        color = colorResource(id = R.color.contrast_color)
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
                            .weight(1f)
                            .alpha(animateButtonsAlpha),
                            shadow = animateShadow, onClick = {
                                openHowToPlayQuestionDialog()
                            }) {
                            BasicText(
                                modifier = Modifier.align(Alignment.Center),
                                textAlign = TextAlign.Center,
                                text = stringResource(
                                    id = R.string.how_to_play
                                )
                            )
                        }

                        BasicContainer(modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .weight(1f)
                            .alpha(animateButtonsAlpha),
                            shadow = animateShadow,
                            onClick = {
                                navController.navigate(Routes.SuggestQuestion.value)
                            }) {
                            BasicText(
                                modifier = Modifier.align(Alignment.Center),
                                textAlign = TextAlign.Center,
                                text = stringResource(
                                    id = R.string.suggest_a_question
                                )
                            )
                        }

                        BasicContainer(modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .weight(1f)
                            .alpha(animateButtonsAlpha),
                            shadow = animateShadow, onClick = {
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
                            .weight(0.7f)
                            .alpha(animateButtonsAlpha),
                        shadow = animateShadow,
                        onClick = {
                            startQuestionClick()
                        }
                    ) {
                        BasicText(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(16.dp),
                            text = stringResource(id = R.string.start_question),
                            fontSize = 32
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
        PreSoloScreen(rememberNavController(), QuestionDifficulty.HARD, {}) {

        }
    }
}