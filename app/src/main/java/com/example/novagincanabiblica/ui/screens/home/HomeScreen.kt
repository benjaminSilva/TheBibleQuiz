package com.example.novagincanabiblica.ui.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.novagincanabiblica.R
import com.example.novagincanabiblica.ui.basicviews.BasicButton
import com.example.novagincanabiblica.ui.basicviews.animateAlpha
import com.example.novagincanabiblica.ui.screens.Routes
import com.example.novagincanabiblica.ui.theme.NovaGincanaBiblicaTheme

@Composable
fun HomeScreen(navController: NavHostController) {
    var startAnimation by remember {
        mutableStateOf(true)
    }
    LaunchedEffect(Unit) {
        startAnimation = false
    }

    val animateScreenAlpha by animateAlpha(startAnimation)

    //Buttons
    val animateButtonsAlpha by animateAlpha(startAnimation, duration = 500, delay = 800)

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxHeight(.75f)
                .alpha(animateScreenAlpha)
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.displayLarge,
                textAlign = TextAlign.Center
            )
        }
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.CenterHorizontally)
                .alpha(animateButtonsAlpha)
        ) {
            Column(modifier = Modifier.align(Alignment.Center)) {
                BasicButton(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = stringResource(R.string.the_quiz_mode)
                ) {
                    navController.navigate(Routes.SoloMode.value)
                }
                BasicButton(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = stringResource(R.string.biblical_wordle)
                ) {
                    navController.navigate(Routes.SoloMode.value)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    NovaGincanaBiblicaTheme {
        HomeScreen(rememberNavController())
    }
}