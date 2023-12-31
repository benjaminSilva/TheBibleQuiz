package com.example.novagincanabiblica.ui.screens.home

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.novagincanabiblica.R
import com.example.novagincanabiblica.data.models.BibleVerse
import com.example.novagincanabiblica.data.models.Session
import com.example.novagincanabiblica.ui.basicviews.BasicText
import com.example.novagincanabiblica.ui.basicviews.generateSubSequentialAlphaAnimations
import com.example.novagincanabiblica.ui.basicviews.generateSubSequentialPositionAnimations
import com.example.novagincanabiblica.ui.basicviews.shadowWithAnimation
import com.example.novagincanabiblica.ui.screens.Routes
import com.example.novagincanabiblica.ui.theme.NovaGincanaBiblicaTheme
import com.example.novagincanabiblica.ui.theme.almostWhite
import com.example.novagincanabiblica.viewmodel.HomeViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun InitializeHomeScreen(navController: NavHostController, homeViewModel: HomeViewModel) {
    val localSession by homeViewModel.localSession.collectAsStateWithLifecycle()
    val dailyBibleVerse by homeViewModel.dailyBibleVerse.collectAsStateWithLifecycle()
    val errorMessage by homeViewModel.errorMessage.collectAsStateWithLifecycle("")
    val hasUserPlayedLocally by homeViewModel.hasUserPlayedLocally.collectAsStateWithLifecycle()
    val isRefreshing by homeViewModel.isRefreshing.collectAsStateWithLifecycle()

    val pullRefreshState =
        rememberPullRefreshState(isRefreshing, onRefresh = { homeViewModel.refresh() })

    var hourOfTheDay by remember {
        mutableIntStateOf(0)
    }

    val context = LocalContext.current

    LaunchedEffect(key1 = errorMessage) {
        if (errorMessage.isNotEmpty()) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(Unit) {
        val rightNow = Calendar.getInstance()
        hourOfTheDay = rightNow[Calendar.HOUR_OF_DAY]
        homeViewModel.checkGamesAvailability()
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { result ->
            if (result.resultCode == RESULT_OK) {
                homeViewModel.signInSomething(result)
            }
        }
    )

    HomeScreen(
        navController = navController,
        hourOfTheDay = hourOfTheDay,
        localSession = localSession,
        dailyBibleVerse = dailyBibleVerse,
        pullRefreshState = pullRefreshState,
        hasUserPlayedLocally = hasUserPlayedLocally,
        isRefreshing = isRefreshing
    ) {
        homeViewModel.signIn(launcher)
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    hourOfTheDay: Int,
    localSession: Session,
    dailyBibleVerse: BibleVerse,
    hasUserPlayedLocally: Boolean,
    pullRefreshState: PullRefreshState,
    isRefreshing: Boolean,
    onClickSignIn: () -> Unit
) {
    val context = LocalContext.current

    var startAnimation by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(Unit) {
        startAnimation = false
    }

    val animationLayoutList =
        generateSubSequentialAlphaAnimations(
            numberOfViews = 4,
            condition = startAnimation,
            duration = 500
        )
    val animationPositionList = generateSubSequentialPositionAnimations(
        numberOfViews = 4,
        condition = startAnimation,
        offsetStart = IntOffset(-80, 0),
        duration = 500
    )

    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(
            Intent.EXTRA_TEXT,
            "I want to share this verse with you:\n\n${dailyBibleVerse.verse}\n\n${dailyBibleVerse.reference}"
        )
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    val haptic = LocalHapticFeedback.current

    Box(modifier = Modifier
        .fillMaxSize()
        .pullRefresh(pullRefreshState)
        .verticalScroll(rememberScrollState())) {
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
                    painter = painterResource(
                        id = when {
                            (6..18).contains(hourOfTheDay) -> R.drawable.baseline_wb_sunny_24
                            else -> R.drawable.baseline_nights_stay_24
                        }
                    ),
                    contentDescription = null
                )
                BasicText(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(start = 8.dp), text = when {
                        (6..12).contains(hourOfTheDay) -> "Good morning ${localSession.userInfo?.userName}"
                        (12..18).contains(hourOfTheDay) -> "Good afternoon ${localSession.userInfo?.userName}"
                        else -> "Good evening ${localSession.userInfo?.userName}"
                    }
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
                        .combinedClickable(onLongClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            context.startActivity(shareIntent)
                        }) {}
                        .animateContentSize()
                        .background(almostWhite)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        BasicText(
                            modifier = Modifier.align(Alignment.Start),
                            text = "Verse of the day"
                        )
                        BasicText(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.Start),
                            text = dailyBibleVerse.verse,
                            fontSize = 24,
                            lineHeight = 22
                        )

                        BasicText(
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .align(Alignment.End),
                            text = dailyBibleVerse.reference
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
                        .clickable {
                            // Uncomment this before release
                            if (localSession.hasPlayedQuizGame/* || hasUserPlayedLocally*/)
                                navController.navigate(Routes.QuizResults.value)
                            else
                                navController.navigate(Routes.QuizMode.value)
                        }
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
                        .clickable {
                            //navController.navigate(Routes.WordleMode.value)
                            if (localSession.hasPlayerWordleGame/* || hasUserPlayedLocally*/)
                                navController.navigate(Routes.WordleResults.value)
                            else
                                navController.navigate(Routes.WordleMode.value)
                        }
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
                        .clickable {
                            if (!localSession.userInfo?.userId.isNullOrBlank()) {
                                navController.navigate(Routes.Profile.value)
                            } else {
                                onClickSignIn()
                            }
                        }
                        .background(almostWhite)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        if (!localSession.userInfo?.userId.isNullOrBlank()) {
                            AsyncImage(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape),
                                model = localSession.userInfo?.profilePictureUrl,
                                contentDescription = null
                            )
                        } else {
                            Image(
                                modifier = Modifier
                                    .size(32.dp)
                                    .align(Alignment.CenterVertically),
                                painter = painterResource(id = R.drawable.baseline_login_24),
                                contentDescription = null
                            )
                        }
                        BasicText(
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(start = 8.dp),
                            text = if (!localSession.userInfo?.userId.isNullOrBlank()) "Profile" else "Sign Up / Login with Google",
                            fontSize = 24,
                            lineHeight = 22
                        )
                    }
                }
            }
        }
        PullRefreshIndicator(
            modifier = Modifier.align(Alignment.TopCenter),
            refreshing = isRefreshing,
            state = pullRefreshState
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    NovaGincanaBiblicaTheme {

    }
}