package com.bsoftwares.thebiblequiz.ui.screens

import android.app.Activity.RESULT_OK
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicContainer
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicScreenBox
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicText
import com.bsoftwares.thebiblequiz.viewmodel.HomeViewModel

@Composable
fun InitializeLoginScreen(navController: NavHostController, homeViewModel: HomeViewModel) {

    val localSession by homeViewModel.localSession.collectAsStateWithLifecycle()

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

    BasicScreenBox {
        LoginScreen {
            homeViewModel.signIn(launcher)
        }
    }
}

@Composable
fun LoginScreen(loginWithGoogle: () -> Unit) {
    BasicContainer(onClick = loginWithGoogle) {
        BasicText(text = "Login with Google")
    }
}