package com.example.novagincanabiblica.ui.screens.gamemodes.solomode

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Button
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.novagincanabiblica.R
import com.example.novagincanabiblica.ui.basicviews.BasicButton
import com.example.novagincanabiblica.ui.basicviews.BasicText
import com.example.novagincanabiblica.ui.basicviews.animateAlpha
import com.example.novagincanabiblica.ui.basicviews.animateAngle
import com.example.novagincanabiblica.ui.basicviews.animatePosition
import com.example.novagincanabiblica.ui.navigation.navigateWithoutRemembering
import com.example.novagincanabiblica.ui.screens.Routes
import com.example.novagincanabiblica.ui.theme.NovaGincanaBiblicaTheme
import com.example.novagincanabiblica.viewmodel.SoloModeViewModel

@Composable
fun InitializePreSoloScreen(
    navController: NavHostController,
    soloViewModel: SoloModeViewModel
) {
    val questionNumber by soloViewModel.currentQuestionNumber.collectAsStateWithLifecycle()
    PreSoloScreen(
        navController = navController,
        questionNumber = questionNumber
    )
}

@Composable
fun PreSoloScreen(
    navController: NavHostController,
    questionNumber: Int
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
    val animateButtonsAlpha by animateAlpha(startAnimation, duration = 500, delay = 1500)

    Box(
        modifier = Modifier.fillMaxSize(),
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
                        text = "Question",
                        style = MaterialTheme.typography.displayLarge
                    )
                    Text(
                        modifier = Modifier
                            .align(Alignment.End)
                            .rotate(animateTitleAngle)
                            .offset {
                                animateNumberPosition
                            }
                            .alpha(animateScreenAlpha),
                        text = "$questionNumber",
                        fontSize = 115.sp,
                        style = MaterialTheme.typography.displayLarge
                    )
                }
                BasicText(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .alpha(animateButtonsAlpha),
                    text = when (questionNumber) {
                        1 -> "Easy"
                        2 -> "Medium"
                        3 -> "Hard"
                        else -> "Pastor Level"
                    },
                    fontSize = 25,
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .alpha(animateButtonsAlpha)
            ) {
                Column(modifier = Modifier.align(Alignment.Center)) {
                    BasicButton(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        text = stringResource(R.string.start_question)
                    ) {
                        navController.navigateWithoutRemembering(route = Routes.SoloModeQuestion)
                    }
                    BasicButton(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        text = stringResource(id = R.string.go_back)
                    ) {
                        navController.popBackStack()
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
        PreSoloScreen(rememberNavController(), 8)
    }
}