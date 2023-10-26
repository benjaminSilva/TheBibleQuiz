package com.example.novagincanabiblica.ui.screens.home

import androidx.compose.foundation.Image
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.novagincanabiblica.R
import com.example.novagincanabiblica.ui.basicviews.BasicText
import com.example.novagincanabiblica.ui.basicviews.animateAlpha
import com.example.novagincanabiblica.ui.basicviews.animatePosition
import com.example.novagincanabiblica.ui.basicviews.animateScale
import com.example.novagincanabiblica.ui.navigation.navigateWithoutRemembering
import com.example.novagincanabiblica.ui.screens.Routes
import com.example.novagincanabiblica.ui.theme.NovaGincanaBiblicaTheme
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun SplashScreen(navController: NavHostController) {

    var startAnimation by remember {
        mutableStateOf(true)
    }
    LaunchedEffect(Unit) {
        startAnimation = false
        delay(1800)
        navController.navigate(Routes.Start.value) {
            popUpTo(navController.graph.id)
        }
    }

    val fadeOut by animateAlpha(
        condition = startAnimation,
        startValue = 1f,
        endValue = 0f,
        duration = 500,
        delay = 1500
    )

    val animateScale by animateScale(startAnimation, 1f, 1.2f, duration = 2000, delay = 0)

    Box(
        modifier = Modifier
            .fillMaxHeight(0.9f)
            .fillMaxWidth()
            .scale(animateScale)
            .alpha(fadeOut)
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Image(
                modifier = Modifier
                    .size(128.dp, 128.dp)
                    .align(Alignment.CenterHorizontally),
                painter = painterResource(id = R.drawable.baseline_menu_book_24),
                contentDescription = null
            )
            BasicText(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(start = 8.dp), text = "The Bible Quiz",
                fontSize = 24
            )
        }
    }

}

@Preview(showBackground = true)
@Composable
fun SplashPreview() {
    NovaGincanaBiblicaTheme {
        SplashScreen(rememberNavController())
    }
}