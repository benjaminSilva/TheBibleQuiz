package com.bsoftwares.thebiblequiz.ui.screens

import android.app.Activity.RESULT_OK
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.bsoftwares.thebiblequiz.data.models.state.FeedbackMessage
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicContainer
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicScreenBox
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicText
import com.bsoftwares.thebiblequiz.viewmodel.HomeViewModel
import kotlinx.coroutines.delay

@Composable
fun InitializeLoginScreen(navController: NavHostController, homeViewModel: HomeViewModel) {

    val localSession by homeViewModel.localSession.collectAsStateWithLifecycle()
    val feedbackMessage by homeViewModel.feedbackMessage.collectAsStateWithLifecycle()

    LaunchedEffect(localSession) {
        if (localSession.userInfo.userId.isNotEmpty()) {
            navController.navigate(Routes.Home.value) {
                popUpTo(Routes.LoginScreen.value) { inclusive = true }
            }
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { result ->
            if (result.resultCode == RESULT_OK) {
                homeViewModel.signInSomething(result.data)
            }
        }
    )

    BasicScreenBox(
        feedbackMessage = feedbackMessage,
        conditionToDisplayFeedbackMessage = feedbackMessage == FeedbackMessage.NoGoogleAccountFoundOnDevice
    ) {
        LoginScreen {
            homeViewModel.signIn(launcher)
        }
    }
}

@Composable
fun LoginScreen(loginWithGoogle: () -> Unit) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val screenHeight = maxHeight // This gives the full height of the screen.

        // VeryCoolLogo: 65% from top
        VeryCoolLogo(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = screenHeight * 0.30f)
        )

        // BasicContainer: 25% from top
        BasicContainer(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = screenHeight * 0.75f),
            onClick = loginWithGoogle
        ) {
            BasicText(modifier = Modifier.padding(16.dp), text = "Login with Google", fontSize = 24)
        }
    }
}
@Composable
fun VeryCoolLogo(modifier: Modifier) {

    var THE by remember {
        mutableStateOf("")
    }

    var BIBLE by remember {
        mutableStateOf("")
    }

    var QUIZ by remember {
        mutableStateOf("")
    }

    val delayTime = 100L

    LaunchedEffect(Unit) {
        delay(delayTime)
        THE = "T"
        delay(delayTime)
        THE = "TH"
        delay(delayTime)
        THE = "THE"
        delay(delayTime)
        BIBLE = "B"
        delay(delayTime)
        BIBLE = "BI"
        delay(delayTime)
        BIBLE = "BIB"
        delay(delayTime)
        BIBLE = "BIBL"
        delay(delayTime)
        BIBLE = "BIBLE"
        delay(delayTime)
        QUIZ = "Q"
        delay(delayTime)
        QUIZ = "QU"
        delay(delayTime)
        QUIZ = "QUI"
        delay(delayTime)
        QUIZ = "QUIZ"
        while (true) {
            delay(1000)
            QUIZ = "QUIZ."
            delay(1000)
            QUIZ = "QUIZ"
        }
    }

    Column(modifier = modifier.offset(x = (-30).dp)) {
        BasicText(
            modifier = Modifier
                .width(175.dp)
                .height(75.dp)
                .align(Alignment.Start),
            text = THE,
            fontSize = 64
        )
        BasicText(
            modifier = Modifier
                .width(175.dp)
                .height(75.dp)
                .align(Alignment.Start)
                .offset(y = (-20).dp), text = BIBLE, fontSize = 64
        )
        BasicText(
            modifier = Modifier
                .width(175.dp)
                .height(75.dp)
                .align(Alignment.Start)
                .offset(y = (-40).dp), text = QUIZ, fontSize = 64
        )
    }
}

@Preview
@Composable
fun PreviewLoginScreen() {
    LoginScreen {

    }
}