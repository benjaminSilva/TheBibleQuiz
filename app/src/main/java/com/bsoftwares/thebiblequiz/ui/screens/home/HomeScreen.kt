package com.bsoftwares.thebiblequiz.ui.screens.home

import android.content.Intent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.bsoftwares.thebiblequiz.R
import com.bsoftwares.thebiblequiz.data.models.BibleVerse
import com.bsoftwares.thebiblequiz.data.models.Session
import com.bsoftwares.thebiblequiz.data.models.isReady
import com.bsoftwares.thebiblequiz.data.models.state.DialogType
import com.bsoftwares.thebiblequiz.data.models.state.FeedbackMessage
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicContainer
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicScreenBox
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicText
import com.bsoftwares.thebiblequiz.ui.basicviews.generateSubSequentialAlphaAnimations
import com.bsoftwares.thebiblequiz.ui.basicviews.generateSubSequentialPositionAnimations
import com.bsoftwares.thebiblequiz.ui.screens.Routes
import com.bsoftwares.thebiblequiz.ui.theme.NovaGincanaBiblicaTheme
import com.bsoftwares.thebiblequiz.ui.theme.container_in_container
import com.bsoftwares.thebiblequiz.ui.theme.disableClicks
import com.bsoftwares.thebiblequiz.ui.theme.emptyString
import com.bsoftwares.thebiblequiz.ui.theme.enableClicks
import com.bsoftwares.thebiblequiz.viewmodel.HomeViewModel
import kotlinx.coroutines.delay
import java.util.Calendar

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun InitializeHomeScreen(navController: NavHostController, homeViewModel: HomeViewModel) {
    val localSession by homeViewModel.localSession.collectAsStateWithLifecycle()
    val dailyBibleVerse by homeViewModel.dailyBibleVerse.collectAsStateWithLifecycle()
    val feedbackMessage by homeViewModel.feedbackMessage.collectAsStateWithLifecycle()
    val isRefreshing by homeViewModel.isRefreshing.collectAsStateWithLifecycle()
    val dialog by homeViewModel.displayDialog.collectAsStateWithLifecycle()
    val day by homeViewModel.day.collectAsStateWithLifecycle()
    val remainingTimeForNextDay by homeViewModel.remainingTimeForNextDay.collectAsStateWithLifecycle()

    var displayDialog by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(dialog) {
        if (dialog != DialogType.EmptyValue) {
            displayDialog = true
        }
    }


    var enabled by remember {
        mutableStateOf(enableClicks())
    }

    LaunchedEffect(enabled) {
        if (!enabled.first) {
            enabled.second()
            delay(1000)
            enabled = enableClicks()
        }
    }

    val pullRefreshState =
        rememberPullRefreshState(isRefreshing, onRefresh = { homeViewModel.refresh() })

    var hourOfTheDay by remember {
        mutableIntStateOf(0)
    }

    LaunchedEffect(localSession) {
        if (localSession.isReady()){
            val rightNow = Calendar.getInstance()
            hourOfTheDay = rightNow[Calendar.HOUR_OF_DAY]
            homeViewModel.cancelDelayedAction()
            homeViewModel.updateDialog()
        } else {
            homeViewModel.delayedAction {
                homeViewModel.updateDialog(DialogType.Loading)
            }
        }
    }

    if (displayDialog) {
        when (dialog) {
            DialogType.Loading -> {
                DialogType.Loading.Generate()
            }
            else -> Unit
        }
    }

    BasicScreenBox(
        feedbackMessage = feedbackMessage,
        conditionToDisplayFeedbackMessage = feedbackMessage == FeedbackMessage.NewDay,
        enabled = enabled.first
    ) {
        if (localSession.isReady()) {
            HomeScreen(
                remainingTimeForNextDay = remainingTimeForNextDay,
                dayNumber = day,
                hourOfTheDay = hourOfTheDay,
                localSession = localSession,
                dailyBibleVerse = dailyBibleVerse,
                pullRefreshState = pullRefreshState,
                isRefreshing = isRefreshing,
                navigate = {
                    enabled = disableClicks {
                        if (it is Routes.Profile) {
                            navController.navigate(it.withParameter(localSession.userInfo.userId))
                        } else {
                            navController.navigate(it.value)
                        }
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    remainingTimeForNextDay: String,
    dayNumber: Int,
    hourOfTheDay: Int,
    localSession: Session,
    dailyBibleVerse: BibleVerse,
    pullRefreshState: PullRefreshState,
    isRefreshing: Boolean,
    navigate: (Routes) -> Unit
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current

    var startAnimation by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(localSession) {
        if (localSession.isReady()) {
            startAnimation = false
        }
    }

    val animationLayoutList =
        generateSubSequentialAlphaAnimations(
            numberOfViews = 7,
            condition = startAnimation,
            duration = 1000
        )
    val animationPositionList = generateSubSequentialPositionAnimations(
        numberOfViews = 7,
        condition = startAnimation,
        offsetStart = IntOffset(-80, 0),
        duration = 500
    )

    val bibleVerseIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(
            Intent.EXTRA_TEXT,
            stringResource(
                R.string.i_want_to_share_this_verse_with_you,
                dailyBibleVerse.verse,
                dailyBibleVerse.reference
            )
        )
        type = "text/plain"
    }

    val appIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(
            Intent.EXTRA_TEXT,
            stringResource(R.string.hi_we_should_play_the_bible_quiz_together)
        )
        type = "text/plain"
    }

    val bibleVerseShareIntent = Intent.createChooser(bibleVerseIntent, null)
    val appShareIntent = Intent.createChooser(
        appIntent,
        stringResource(R.string.choose_where_you_re_sharing_this)
    )
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
            Row(modifier = Modifier
                .alpha(alpha = animationLayoutList[0].value)
                .offset {
                    animationPositionList[0].value
                }) {
                Image(
                    modifier = Modifier.align(Alignment.CenterVertically),
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
                            localSession.userInfo.userName, remainingTimeForNextDay, dayNumber
                        )

                        (12..18).contains(hourOfTheDay) -> stringResource(
                            R.string.good_afternoon,
                            localSession.userInfo.userName, remainingTimeForNextDay, dayNumber
                        )

                        else -> stringResource(
                            R.string.good_evening,
                            localSession.userInfo.userName, remainingTimeForNextDay, dayNumber
                        )
                    }
                )

            }
            BasicContainer(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(alpha = animationLayoutList[1].value)
                    .offset {
                        animationPositionList[1].value
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
                    .alpha(alpha = animationLayoutList[2].value)
                    .offset {
                        animationPositionList[2].value
                    },
                onClick = {
                    if (localSession.hasPlayedQuizGame)
                        navigate(Routes.QuizResults)
                    else
                        navigate(Routes.QuizMode)
                }
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        Image(
                            modifier = Modifier.size(64.dp),
                            painter = painterResource(id = R.drawable.bible_24),
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

                    BasicContainer(
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.CenterVertically),
                        shape = RoundedCornerShape(4.dp),
                        backGroundColor = container_in_container()
                    ) {
                        if (localSession.hasPlayedQuizGame) {
                            Image(
                                modifier = Modifier.size(24.dp),
                                painter = painterResource(id = R.drawable.baseline_check_24_bw),
                                contentDescription = null
                            )
                        } else {
                            Spacer(modifier = Modifier.size(24.dp))
                        }
                    }
                }
            }
            BasicContainer(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(alpha = animationLayoutList[3].value)
                    .offset {
                        animationPositionList[3].value
                    },
                onClick = {
                    if (localSession.hasPlayerWordleGame)
                        navigate(Routes.WordleResults)
                    else
                        navigate(Routes.WordleMode)
                }
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        Image(
                            modifier = Modifier.size(64.dp),
                            painter = painterResource(id = R.drawable.group_481),
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

                    BasicContainer(
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.CenterVertically),
                        shape = RoundedCornerShape(4.dp),
                        backGroundColor = container_in_container()
                    ) {
                        if (localSession.hasPlayerWordleGame) {
                            Image(
                                modifier = Modifier.size(24.dp),
                                painter = painterResource(id = R.drawable.baseline_check_24_bw),
                                contentDescription = null
                            )
                        } else {
                            Spacer(modifier = Modifier.size(24.dp))
                        }
                    }
                }
            }

            BasicContainer(modifier = Modifier
                .fillMaxWidth()
                .alpha(alpha = animationLayoutList[4].value)
                .offset {
                    animationPositionList[4].value
                },
                onClick = {
                    navigate(Routes.Profile)
                }
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    AsyncImage(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape),
                        model = localSession.userInfo.profilePictureUrl,
                        contentDescription = null
                    )

                    BasicText(
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 8.dp),
                        text = if (localSession.userInfo.userId.isNotBlank()) stringResource(R.string.profile) else emptyString,
                        fontSize = 24,
                        lineHeight = 22
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(alpha = animationLayoutList[5].value)
                    .offset {
                        animationPositionList[5].value
                    },
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                BasicContainer(modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
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
                    onClick = {
                        uriHandler.openUri("https://play.google.com/store/apps/details?id=com.bsoftwares.thebiblequiz")
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
            BasicContainer(modifier = Modifier
                .fillMaxWidth()
                .alpha(alpha = animationLayoutList[6].value)
                .offset {
                    animationPositionList[6].value
                },
                onClick = {
                    uriHandler.openUri("https://www.gofundme.com/f/the-bible-quiz-project")
                }
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.volunteer_activism_24dp),
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape),
                        contentDescription = null
                    )
                    BasicText(
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 8.dp),
                        text = stringResource(R.string.donate_to_our_project),
                        fontSize = 24,
                        lineHeight = 22
                    )
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
            remainingTimeForNextDay = "23:33:12",
            dayNumber = 13,
            hourOfTheDay = 22,
            localSession = Session(),
            dailyBibleVerse = BibleVerse(),
            pullRefreshState = rememberPullRefreshState(
                refreshing = true,
                onRefresh = { /*TODO*/ }),
            isRefreshing = true,
            navigate = {

            }
        )
    }
}