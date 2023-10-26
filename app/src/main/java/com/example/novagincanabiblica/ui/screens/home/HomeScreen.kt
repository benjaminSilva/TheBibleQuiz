package com.example.novagincanabiblica.ui.screens.home

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.novagincanabiblica.R
import com.example.novagincanabiblica.ui.basicviews.BasicText
import com.example.novagincanabiblica.ui.basicviews.generateSubSequentialAlphaAnimations
import com.example.novagincanabiblica.ui.basicviews.generateSubSequentialPositionAnimations
import com.example.novagincanabiblica.ui.basicviews.shadowWithAnimation
import com.example.novagincanabiblica.ui.screens.Routes
import com.example.novagincanabiblica.ui.theme.NovaGincanaBiblicaTheme
import com.example.novagincanabiblica.ui.theme.almostWhite

@Composable
fun HomeScreen(navController: NavHostController) {
    var startAnimation by remember {
        mutableStateOf(true)
    }
    LaunchedEffect(Unit) {
        startAnimation = false
    }

    val animationLayoutList =
        generateSubSequentialAlphaAnimations(numberOfViews = 4, condition = startAnimation)
    val animationPositionList = generateSubSequentialPositionAnimations(
        numberOfViews = 4,
        condition = startAnimation,
        offsetStart = IntOffset(-80, 0)
    )

    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, "But you are a chosen people, a royal priesthood, a holy nation, God’s special possession, that you may declare the praises of him who called you out of darkness into his wonderful light.\n1 Peter 2:9")
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(modifier = Modifier
            .offset {
                animationPositionList[0].value
            }
            .alpha(animationLayoutList[0].value)) {
            Image(
                painter = painterResource(id = R.drawable.baseline_wb_sunny_24),
                contentDescription = null
            )
            BasicText(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 8.dp), text = "Good morning Benjamin"
            )
        }
        Box(modifier = Modifier
            .fillMaxWidth()
            .shadowWithAnimation(
                20.dp,
                offset = animationPositionList[1].value,
                alpha = animationLayoutList[1].value
            )
            .offset {
                animationPositionList[1].value
            }
            .alpha(animationLayoutList[1].value)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(16.dp))
                    .clickable {
                        context.startActivity(shareIntent)
                    }
                    .background(almostWhite)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    BasicText(
                        modifier = Modifier.align(Alignment.Start),
                        text = "Verse of the day (Click to share)"
                    )
                    BasicText(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Start),
                        text = "But you are a chosen people, a royal priesthood, a holy nation, God’s special possession, that you may declare the praises of him who called you out of darkness into his wonderful light.",
                        fontSize = 24,
                        lineHeight = 22
                    )

                    BasicText(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .align(Alignment.End),
                        text = "1 Peter 2:9"
                    )

                }
            }
        }
        Box(modifier = Modifier
            .fillMaxWidth()
            .shadowWithAnimation(
                20.dp,
                offset = animationPositionList[1].value,
                alpha = animationLayoutList[1].value
            )
            .offset {
                animationPositionList[1].value
            }
            .alpha(animationLayoutList[1].value)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(16.dp))
                    .clickable { navController.navigate(Routes.SoloMode.value) }
                    .background(almostWhite)
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    Image(
                        modifier = Modifier.size(64.dp),
                        painter = painterResource(id = R.drawable.baseline_menu_book_24),
                        contentDescription = null
                    )
                    BasicText(
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 8.dp),
                        text = "Daily Bible\nQuiz",
                        fontSize = 24,
                        lineHeight = 22
                    )
                }
            }
        }
        Box(modifier = Modifier
            .fillMaxWidth()
            .shadowWithAnimation(
                20.dp,
                offset = animationPositionList[2].value,
                alpha = animationLayoutList[2].value
            )
            .offset {
                animationPositionList[2].value
            }
            .alpha(animationLayoutList[2].value)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(16.dp))
                    .clickable { navController.navigate(Routes.SoloMode.value) }
                    .background(almostWhite)
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    Image(
                        modifier = Modifier.size(64.dp),
                        painter = painterResource(id = R.drawable.baseline_border_clear_24),
                        contentDescription = null
                    )
                    BasicText(
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 8.dp),
                        text = "Biblical\nWordle",
                        fontSize = 24,
                        lineHeight = 22
                    )
                }
            }
        }

        Box(modifier = Modifier
            .fillMaxWidth()
            .shadowWithAnimation(
                20.dp,
                offset = animationPositionList[3].value,
                alpha = animationLayoutList[3].value
            )
            .offset {
                animationPositionList[3].value
            }
            .alpha(animationLayoutList[3].value)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(16.dp))
                    .clickable { navController.navigate(Routes.SoloMode.value) }
                    .background(almostWhite)
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    Image(
                        modifier = Modifier.size(32.dp),
                        painter = painterResource(id = R.drawable.baseline_login_24),
                        contentDescription = null
                    )
                    BasicText(
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 8.dp),
                        text = "Sign Up / Login",
                        fontSize = 24,
                        lineHeight = 22
                    )
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