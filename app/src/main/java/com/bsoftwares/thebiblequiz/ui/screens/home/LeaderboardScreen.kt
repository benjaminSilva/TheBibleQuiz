package com.bsoftwares.thebiblequiz.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.bsoftwares.thebiblequiz.R
import com.bsoftwares.thebiblequiz.data.models.LeaderboardDuration
import com.bsoftwares.thebiblequiz.data.models.LeaderboardFilter
import com.bsoftwares.thebiblequiz.data.models.LeaderboardType
import com.bsoftwares.thebiblequiz.data.models.RankingData
import com.bsoftwares.thebiblequiz.data.models.getString
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicContainer
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicScreenBox
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicText
import com.bsoftwares.thebiblequiz.ui.basicviews.animateAlphaByState
import com.bsoftwares.thebiblequiz.ui.basicviews.animateAsState
import com.bsoftwares.thebiblequiz.ui.screens.Routes
import com.bsoftwares.thebiblequiz.ui.screens.games.quiz.screens.BasicRadioButton
import com.bsoftwares.thebiblequiz.ui.screens.league.RankingView
import com.bsoftwares.thebiblequiz.viewmodel.LeaderboardViewModel
import kotlinx.coroutines.delay

@Composable
fun InitializeLeaderboardScreen(
    navController: NavHostController,
    leaderboardViewModel: LeaderboardViewModel
) {
    val leaderboard by leaderboardViewModel.rankingData.collectAsStateWithLifecycle()

    BasicScreenBox {
        LeaderboardsScreen(
            displayData = leaderboard,
            updateLeaderboards = {
                leaderboardViewModel.updateLeaderboard(it)
            }, goBack = {
                navController.popBackStack()
            }) {
            navController.navigate(Routes.Profile.withParameter(it))
        }
    }
}

enum class AnimationPhase { SHOWING, OUT, IN }

@Composable
fun LeaderboardsScreen(
    displayData: List<RankingData>,
    updateLeaderboards: (LeaderboardType) -> Unit,
    goBack: () -> Unit,
    navigateToUser: (String) -> Unit
) {
    val listOfDurationOptions = LeaderboardDuration.values()
    val listOfFilterOptions = LeaderboardFilter.values()

    val (durationSelectedOption, onDurationSelected) = remember {
        mutableStateOf(
            LeaderboardDuration.ALL_TIME
        )
    }

    val (filterSelectedOption, onFilterSelected) = remember {
        mutableStateOf(
            LeaderboardFilter.ALL
        )
    }

    var phase by remember { mutableStateOf(AnimationPhase.OUT) }
    var startAnimation by remember { mutableStateOf(false) }
    var toTheLeft by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(750)
        phase = AnimationPhase.SHOWING
    }

    LaunchedEffect (startAnimation, displayData) {
        if (startAnimation) {
            phase = AnimationPhase.OUT
            delay(250)
            phase = AnimationPhase.IN
            delay(250)
            phase = AnimationPhase.SHOWING
            startAnimation = false
        }
    }

    val alphaAnimation by animateAlphaByState(phase)
    val offsetAnimation by animateAsState(phase = phase, toTheLeft = toTheLeft)

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
            BasicContainer (modifier = Modifier.align(Alignment.CenterStart), onClick = {
                goBack()
            }) {
                Image(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .size(24.dp)
                        .align(Alignment.Center),
                    painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                    contentDescription = null
                )
            }
            BasicText(modifier = Modifier.padding(start = 8.dp).align(Alignment.Center), text = "Leaderboards", fontSize = 28)
        }
        BasicContainer(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .weight(0.85f)
        ) {
            RankingView(
                modifier = Modifier.fillMaxSize().alpha(alphaAnimation).offset {
                    offsetAnimation
                },
                displayData,
                longClickFunction = {

                },
                openUserProfile = {
                    navigateToUser(it)
                })
        }

        BasicContainer(
            modifier = Modifier
                .height(100.dp)
                .padding(horizontal = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOfFilterOptions.forEach { filter ->
                        BasicRadioButton(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f),
                            selected = filter == filterSelectedOption,
                            updateRadioButton = {
                                onFilterSelected(filter)
                                toTheLeft = translateBasedOnWhatItIs(filter, filterSelectedOption)
                                startAnimation = true
                                updateLeaderboards(
                                    translateLeaderBoard(
                                        selectedFilter = filter,
                                        selectedDuration = durationSelectedOption
                                    )
                                )
                            }
                        ) {
                            BasicText(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .align(Alignment.Center),
                                text = filter.getString(),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOfDurationOptions.forEach { duration ->
                        BasicRadioButton(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f),
                            selected = duration == durationSelectedOption,
                            updateRadioButton = {
                                onDurationSelected(duration)
                                startAnimation = true
                                toTheLeft = duration == LeaderboardDuration.MONTHLY
                                updateLeaderboards(
                                    translateLeaderBoard(
                                        selectedFilter = filterSelectedOption,
                                        selectedDuration = duration
                                    )
                                )
                            }
                        ) {
                            BasicText(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .align(Alignment.Center),
                                text = duration.getString(),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

fun translateBasedOnWhatItIs(
    filter: LeaderboardFilter,
    onFilterSelected: LeaderboardFilter
): Boolean =
    when {
        filter == LeaderboardFilter.WORDLE -> true
        onFilterSelected == LeaderboardFilter.QUIZ -> true
        filter == LeaderboardFilter.ALL && onFilterSelected == LeaderboardFilter.QUIZ -> true
        else -> false
    }

fun translateLeaderBoard(
    selectedFilter: LeaderboardFilter,
    selectedDuration: LeaderboardDuration
): LeaderboardType = if (selectedDuration == LeaderboardDuration.MONTHLY) {
    when (selectedFilter) {
        LeaderboardFilter.WORDLE -> LeaderboardType.MONTHLY_WORDLE
        LeaderboardFilter.QUIZ -> LeaderboardType.MONTHLY_QUIZ
        LeaderboardFilter.ALL -> LeaderboardType.MONTHLY_QUIZ_WORDLE
    }
} else {
    when (selectedFilter) {
        LeaderboardFilter.WORDLE -> LeaderboardType.ALL_TIME_WORDLE
        LeaderboardFilter.QUIZ -> LeaderboardType.ALL_TIME_QUIZ
        LeaderboardFilter.ALL -> LeaderboardType.ALL_TIME_QUIZ_WORDLE
    }
}
