package com.bsoftwares.thebiblequiz.ui.screens.home

import android.app.Activity.RESULT_OK
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.bsoftwares.thebiblequiz.R
import com.bsoftwares.thebiblequiz.data.models.BibleVerse
import com.bsoftwares.thebiblequiz.data.models.Session
import com.bsoftwares.thebiblequiz.data.models.state.DialogType
import com.bsoftwares.thebiblequiz.data.models.state.ProfileDialogType
import com.bsoftwares.thebiblequiz.ui.basicviews.AnimatedBorderCard
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicContainer
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicText
import com.bsoftwares.thebiblequiz.ui.basicviews.generateSubSequentialAlphaAnimations
import com.bsoftwares.thebiblequiz.ui.basicviews.generateSubSequentialPositionAnimations
import com.bsoftwares.thebiblequiz.ui.screens.Routes
import com.bsoftwares.thebiblequiz.ui.screens.profile.PaywallScreen
import com.bsoftwares.thebiblequiz.ui.theme.NovaGincanaBiblicaTheme
import com.bsoftwares.thebiblequiz.viewmodel.HomeViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun InitializeHomeScreen(navController: NavHostController, homeViewModel: HomeViewModel) {
    val localSession by homeViewModel.localSession.collectAsStateWithLifecycle()
    val dailyBibleVerse by homeViewModel.dailyBibleVerse.collectAsStateWithLifecycle()
    val errorMessage by homeViewModel.feedbackMessage.collectAsStateWithLifecycle()
    val hasUserPlayedLocally by homeViewModel.hasUserPlayedLocally.collectAsStateWithLifecycle()
    val isRefreshing by homeViewModel.isRefreshing.collectAsStateWithLifecycle()
    val enabled by homeViewModel.clickable.collectAsStateWithLifecycle()
    val dialog by homeViewModel.displayDialog.collectAsStateWithLifecycle()

    var displayDialog by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(dialog) {
        if (dialog != DialogType.EmptyValue) {
            displayDialog = true
        }
    }

    val pullRefreshState =
        rememberPullRefreshState(isRefreshing, onRefresh = { homeViewModel.refresh() })

    var hourOfTheDay by remember {
        mutableIntStateOf(0)
    }

    LaunchedEffect(Unit) {
        val rightNow = Calendar.getInstance()
        hourOfTheDay = rightNow[Calendar.HOUR_OF_DAY]
        homeViewModel.checkGamesAvailability()
    }

    if (displayDialog) {
        when (dialog) {
            ProfileDialogType.StartPremium -> {
                PaywallScreen(enablePremium = {
                    homeViewModel.updateToPremium()
                }) {
                    homeViewModel.updateDialog()
                }
            }

            else -> Unit
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

    HomeScreen(
        navController = navController,
        hourOfTheDay = hourOfTheDay,
        localSession = localSession,
        dailyBibleVerse = dailyBibleVerse,
        pullRefreshState = pullRefreshState,
        hasUserPlayedLocally = hasUserPlayedLocally,
        isRefreshing = isRefreshing,
        enabled = enabled,
        navigate = {
            navController.navigate(it.value)
            homeViewModel.updateClickable()
        }, openDialog = { dialogToOpen ->
            homeViewModel.updateDialog(dialogType = dialogToOpen)
        }
    ) {
        homeViewModel.signIn(launcher)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    hourOfTheDay: Int,
    localSession: Session,
    dailyBibleVerse: BibleVerse,
    hasUserPlayedLocally: Boolean,
    pullRefreshState: PullRefreshState,
    isRefreshing: Boolean,
    enabled: Boolean,
    navigate: (Routes) -> Unit,
    openDialog: (DialogType) -> Unit,
    onClickSignIn: () -> Unit,
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
            numberOfViews = 6,
            condition = startAnimation,
            duration = 1000
        )
    val animationPositionList = generateSubSequentialPositionAnimations(
        numberOfViews = 6,
        condition = startAnimation,
        offsetStart = IntOffset(-80, 0),
        duration = 500
    )

    val bibleVerseIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(
            Intent.EXTRA_TEXT,
            "I want to share this verse with you:\n\n${dailyBibleVerse.verse}\n\n${dailyBibleVerse.reference}"
        )
        type = "text/plain"
    }
    val appIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(
            Intent.EXTRA_TEXT,
            "Hi, we should play The Bible Quiz together. Download it here."
        )
        type = "text/plain"
    }

    val bibleVerseShareIntent = Intent.createChooser(bibleVerseIntent, null)
    val appShareIntent = Intent.createChooser(appIntent, "Choose where you're sharing this")
    val haptic = LocalHapticFeedback.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row {
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
                        (6..12).contains(hourOfTheDay) -> stringResource(
                            R.string.good_morning_msg,
                            localSession.userInfo.userName
                        )

                        (12..18).contains(hourOfTheDay) -> stringResource(
                            R.string.good_afternoon,
                            localSession.userInfo.userName
                        )

                        else -> stringResource(
                            R.string.good_evening,
                            localSession.userInfo.userName
                        )
                    }
                )

            }
            BasicContainer(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(alpha = animationLayoutList[0].value)
                    .offset {
                        animationPositionList[0].value
                    },
                onLongClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    context.startActivity(bibleVerseShareIntent)
                }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        BasicText(
                            modifier = Modifier.align(Alignment.Start),
                            text = stringResource(R.string.verse_of_the_day)
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
            BasicContainer(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(alpha = animationLayoutList[1].value).offset {
                        animationPositionList[1].value
                    },
                enabled = enabled,
                onClick = {
                    // Uncomment this before release
                    if (localSession.hasPlayedQuizGame/* || hasUserPlayedLocally*/)
                        navigate(Routes.QuizResults)
                    else
                        navigate(Routes.QuizMode)
                }
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
                        text = stringResource(R.string.daily_bible_quiz),
                        fontSize = 24,
                        lineHeight = 22
                    )
                }
            }
            BasicContainer(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(alpha = animationLayoutList[2].value).offset {
                        animationPositionList[2].value
                    },
                enabled = enabled,
                onClick = {
                    //navController.navigate(Routes.WordleMode.value)
                    if (localSession.hasPlayerWordleGame/* || hasUserPlayedLocally*/)
                        navigate(Routes.WordleResults)
                    else
                        navigate(Routes.WordleMode)
                }
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
                        text = stringResource(R.string.biblical_wordle_home),
                        fontSize = 24,
                        lineHeight = 22
                    )
                }
            }

            BasicContainer(modifier = Modifier
                .fillMaxWidth()
                .alpha(alpha = animationLayoutList[3].value)
                .offset {
                    animationPositionList[3].value
                },
                enabled = enabled,
                onClick = {
                    if (localSession.userInfo.userId.isNotBlank()) {
                        navigate(Routes.Profile)
                    } else {
                        onClickSignIn()
                    }
                }
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    if (localSession.userInfo.userId.isNotBlank()) {
                        AsyncImage(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape),
                            model = localSession.userInfo.profilePictureUrl,
                            contentDescription = null
                        )
                    } else {
                        Image(
                            modifier = Modifier
                                .size(48.dp)
                                .align(Alignment.CenterVertically),
                            painter = painterResource(id = R.drawable.baseline_login_24),
                            contentDescription = null
                        )
                    }
                    BasicText(
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 8.dp),
                        text = if (localSession.userInfo.userId.isNotBlank()) stringResource(R.string.profile) else stringResource(
                            R.string.login_with_google
                        ),
                        fontSize = 24,
                        lineHeight = 22
                    )
                }
            }

            var forLastView = 4

            if (localSession.userInfo.userId.isNotBlank() && !localSession.premium) {
                AnimatedBorderCard(modifier = Modifier.alpha(alpha = animationLayoutList[4].value).offset {
                    animationPositionList[4].value
                }) {
                    BasicContainer(
                        modifier = Modifier
                            .fillMaxWidth(),
                        enabled = enabled,
                        onClick = {
                            openDialog(ProfileDialogType.StartPremium)
                        }
                    ) {

                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                        ) {
                            Image(
                                modifier = Modifier.size(48.dp),
                                painter = painterResource(id = R.drawable.crown_svgrepo_com),
                                contentDescription = null
                            )
                            BasicText(
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .padding(start = 8.dp),
                                text = stringResource(R.string.get_premium),
                                fontSize = 24,
                                lineHeight = 22
                            )
                        }
                    }
                }
                forLastView = 5
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(alpha = animationLayoutList[forLastView].value)
                    .offset {
                        animationPositionList[forLastView].value
                    },
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                BasicContainer(modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                    enabled = enabled,
                    onClick = {
                        context.startActivity(appShareIntent)
                    }
                ) {

                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                    ) {

                        Image(
                            modifier = Modifier
                                .size(32.dp)
                                .align(Alignment.CenterVertically),
                            painter = painterResource(id = R.drawable.baseline_share_24),
                            contentDescription = null
                        )

                        BasicText(
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(start = 8.dp),
                            text = stringResource(R.string.share),
                            fontSize = 24,
                            lineHeight = 22
                        )
                    }
                }

                BasicContainer(modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                    enabled = enabled,
                    onClick = {

                    }
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                    ) {

                        Image(
                            modifier = Modifier
                                .size(32.dp)
                                .align(Alignment.CenterVertically),
                            painter = painterResource(id = R.drawable.baseline_star_rate_24),
                            contentDescription = null
                        )

                        BasicText(
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(start = 8.dp),
                            text = stringResource(R.string.rate),
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

@OptIn(ExperimentalMaterialApi::class)
@Preview(showBackground = true)
@Composable
fun HomePreview() {
    NovaGincanaBiblicaTheme {
        HomeScreen(
            navController = rememberNavController(),
            hourOfTheDay = 22,
            localSession = Session(),
            dailyBibleVerse = BibleVerse(),
            hasUserPlayedLocally = true,
            pullRefreshState = rememberPullRefreshState(
                refreshing = true,
                onRefresh = { /*TODO*/ }),
            isRefreshing = true,
            enabled = true,
            navigate = {

            }, {

            }
        ) {

        }
    }
}