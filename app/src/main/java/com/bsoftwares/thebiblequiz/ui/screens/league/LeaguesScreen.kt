package com.bsoftwares.thebiblequiz.ui.screens.league

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.bsoftwares.thebiblequiz.R
import com.bsoftwares.thebiblequiz.data.models.League
import com.bsoftwares.thebiblequiz.data.models.LeagueRule
import com.bsoftwares.thebiblequiz.data.models.Session
import com.bsoftwares.thebiblequiz.data.models.SessionInLeague
import com.bsoftwares.thebiblequiz.data.models.UserTitle
import com.bsoftwares.thebiblequiz.data.models.getString
import com.bsoftwares.thebiblequiz.data.models.state.DialogType
import com.bsoftwares.thebiblequiz.data.models.state.FeedbackMessage
import com.bsoftwares.thebiblequiz.data.models.state.LeagueDialog
import com.bsoftwares.thebiblequiz.data.models.state.getPainter
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicContainer
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicDialog
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicPositiveNegativeDialog
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicScreenBox
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicText
import com.bsoftwares.thebiblequiz.ui.basicviews.animateAlpha
import com.bsoftwares.thebiblequiz.ui.screens.Routes
import com.bsoftwares.thebiblequiz.ui.screens.games.quiz.screens.BasicRadioButton
import com.bsoftwares.thebiblequiz.ui.screens.profile.FriendItem
import com.bsoftwares.thebiblequiz.ui.theme.NovaGincanaBiblicaTheme
import com.bsoftwares.thebiblequiz.ui.theme.basicContainerClean
import com.bsoftwares.thebiblequiz.ui.theme.closeToBlack
import com.bsoftwares.thebiblequiz.ui.theme.container_in_container
import com.bsoftwares.thebiblequiz.ui.theme.contrastColor
import com.bsoftwares.thebiblequiz.ui.theme.darkYellow
import com.bsoftwares.thebiblequiz.ui.theme.disableClicks
import com.bsoftwares.thebiblequiz.ui.theme.enableClicks
import com.bsoftwares.thebiblequiz.ui.theme.gray
import com.bsoftwares.thebiblequiz.ui.theme.lightBrown
import com.bsoftwares.thebiblequiz.ui.theme.wrongPlace
import com.bsoftwares.thebiblequiz.viewmodel.HomeViewModel
import kotlinx.coroutines.delay

@Composable
fun InitializeLeagueScreen(navController: NavHostController, viewModel: HomeViewModel) {
    val league by viewModel.currentLeague.collectAsStateWithLifecycle()
    val dialog by viewModel.displayDialog.collectAsStateWithLifecycle()
    val friendsNotInThisLeague by viewModel.listOfFriendsNotInLeague.collectAsStateWithLifecycle()
    val currentSessionInLeague by viewModel.sessionInLeague.collectAsStateWithLifecycle()
    val feedbackMessage by viewModel.feedbackMessage.collectAsStateWithLifecycle()

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

    var displayDialog by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(dialog) {
        if (dialog != DialogType.EmptyValue) {
            displayDialog = true
        }
    }

    if (displayDialog) {
        when (dialog) {
            is LeagueDialog.FriendList -> {
                BasicDialog(onDismissRequest = {
                    viewModel.updateDialog()
                }) {
                    AddFriendsDialog(friendsList = friendsNotInThisLeague) {
                        viewModel.sendLeagueRequest(it)
                        viewModel.updateDialog()
                    }
                }
            }

            is LeagueDialog.RemoveFriend -> {
                val userToBeRemoved = (dialog as LeagueDialog.RemoveFriend).sessionInLeague
                BasicPositiveNegativeDialog(
                    onDismissRequest = {
                        viewModel.updateDialog()
                    },
                    title = stringResource(R.string.removing_from_league),
                    description = stringResource(
                        R.string.are_you_sure_you_want_to_remove_from_the_league,
                        userToBeRemoved.userName
                    ),
                    positiveFunction = {
                        viewModel.leaveLeague(userToBeRemoved)
                    }
                )
            }

            is LeagueDialog.TitleUpdate -> {
                UpdateTitleDialog(session = currentSessionInLeague, onDismissRequest = {
                    viewModel.updateDialog()
                }) {
                    viewModel.updateTitle(it)
                }
            }

            else -> Unit
        }
    }

    BasicScreenBox(
        feedbackMessage = feedbackMessage,
        conditionToDisplayFeedbackMessage = feedbackMessage == FeedbackMessage.RemovedUserSuccessfully || feedbackMessage == FeedbackMessage.TitleUpdated,
        enabled = enabled.first
    ) {
        LeagueScreen(
            league = league,
            sessionInLeague = currentSessionInLeague,
            navigateEditScreen = {
                enabled = disableClicks {
                    navController.navigate(Routes.EditLeague.value) {
                        launchSingleTop = true
                    }
                }
            },
            openUserProfile = {
                enabled = disableClicks {
                    navController.navigate(Routes.Profile.withParameter(it))
                }
            }) {
            viewModel.updateDialog(it)
        }
    }

}

@Composable
fun LeagueScreen(
    league: League,
    sessionInLeague: SessionInLeague,
    navigateEditScreen: () -> Unit,
    openUserProfile: (String) -> Unit,
    updateDialog: (DialogType) -> Unit
) {

    val haptic = LocalHapticFeedback.current

    var startAnimation by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(league) {
        if (league.leagueId.isNotEmpty())
            startAnimation = false
    }

    val animateAlpha = animateAlpha(
        condition = startAnimation,
        startValue = 0f,
        endValue = 1f,
        duration = 400
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BasicContainer(modifier = Modifier.fillMaxWidth(), onClick = { navigateEditScreen() }) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    BasicContainer(modifier = Modifier.align(Alignment.CenterVertically)) {
                        Image(
                            modifier = Modifier
                                .size(48.dp)
                                .padding(8.dp),
                            painter = league.leagueIcon.getPainter(),
                            contentDescription = null
                        )
                    }

                    Column(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        BasicText(text = league.leagueName, fontSize = 22)
                        BasicText(
                            text = league.leagueRule.getString()
                        )
                    }
                }
            }

            fun longClickFunction(userName: SessionInLeague) {
                when {
                    sessionInLeague.adminUser && !userName.adminUser -> {
                        updateDialog(LeagueDialog.RemoveFriend(sessionInLeague = userName))
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                    sessionInLeague.userId == userName.userId -> {
                        updateDialog(LeagueDialog.TitleUpdate)
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                }
            }

            BasicContainer(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .alpha(animateAlpha.value)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    league.listOfUsers.onEachIndexed { index, user ->
                        when (index) {
                            0 -> {
                                FirstPosition(
                                    session = user,
                                    rule = league.leagueRule,
                                    onLongClick = {
                                        longClickFunction(user)
                                    }) {
                                    openUserProfile(user.userId)
                                }
                            }

                            1 -> SecondPosition(
                                session = user,
                                rule = league.leagueRule, onLongClick = {
                                    longClickFunction(user)
                                }
                            ) {
                                openUserProfile(user.userId)
                            }

                            2 -> OthersPositions(
                                session = user,
                                backGroundColor = lightBrown,
                                placementString = stringResource(R.string.third_place),
                                textColor = closeToBlack,
                                rule = league.leagueRule, onLongClick = {
                                    longClickFunction(user)
                                }
                            ) {
                                openUserProfile(user.userId)
                            }

                            else -> OthersPositions(
                                backGroundColor = basicContainerClean(),
                                session = user,
                                placementString = stringResource(R.string.place, index + 1),
                                textColor = contrastColor(),
                                rule = league.leagueRule, onLongClick = {
                                    longClickFunction(user)
                                }
                            ) {
                                openUserProfile(user.userId)
                            }
                        }

                    }
                    Spacer(modifier = Modifier.height(44.dp))
                }
            }
        }
        if (sessionInLeague.adminUser) {


            FloatingActionButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(32.dp), onClick = {
                    updateDialog(LeagueDialog.FriendList)
                }, containerColor = container_in_container()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_add_24),
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
fun AddFriendsDialog(friendsList: List<Session>, addSelectedFriends: (List<Session>) -> Unit) {

    val listOfSelected = remember {
        mutableStateListOf<Session>()
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        BasicContainer {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                BasicText(
                    text = stringResource(R.string.friends_you_can_add_to_this_league),
                    fontSize = 22
                )
                if (friendsList.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        friendsList.onEach {
                            FriendItem(
                                profilePicture = it.userInfo.profilePictureUrl,
                                userName = it.userInfo.userName,
                                selectable = true
                            ) {
                                if (listOfSelected.contains(it)) {
                                    listOfSelected.remove(it)
                                } else {
                                    listOfSelected.add(it)
                                }
                            }
                        }
                    }
                } else {
                    BasicText(text = stringResource(R.string.you_don_t_have_any_friends_that_could_be_added))
                }
            }
        }
        BasicContainer(modifier = Modifier.align(Alignment.End), onClick = {
            addSelectedFriends(listOfSelected)
        }) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    painter = painterResource(id = R.drawable.baseline_send_24),
                    contentDescription = null
                )
                BasicText(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    text = stringResource(R.string.send_invitation)
                )
            }
        }
    }
}

@Composable
fun FirstPosition(
    session: SessionInLeague,
    rule: LeagueRule,
    onLongClick: () -> Unit,
    openUserProfile: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        BasicText(text = stringResource(R.string.first_place), fontSize = 26)
        BasicContainer(
            backGroundColor = wrongPlace,
            onClick = openUserProfile,
            onLongClick = onLongClick
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box {
                        AsyncImage(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape),
                            model = session.profileImage,
                            contentDescription = null
                        )
                        Image(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .size(12.dp)
                                .rotate(-45f),
                            painter = painterResource(id = R.drawable.crown_svgrepo_com),
                            colorFilter = ColorFilter.tint(darkYellow),
                            contentDescription = null
                        )
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        BasicText(text = session.userName, fontSize = 22, fontColor = closeToBlack)
                        BasicText(text = session.title.getString(), fontColor = closeToBlack)
                    }
                }
                BasicContainer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(88.dp)
                ) {
                    BasicText(
                        modifier = Modifier
                            .align(Alignment.Center),
                        text = stringResource(
                            R.string.points, when (rule) {
                                LeagueRule.QUIZ_AND_WORDLE -> session.totalPoints
                                LeagueRule.QUIZ_ONLY -> session.pointsForQuiz
                                LeagueRule.WORDLE_ONLY -> session.pointsForWordle
                            }
                        ),
                        fontSize = 48
                    )
                }
            }
        }
    }

}

@Composable
fun SecondPosition(
    session: SessionInLeague,
    rule: LeagueRule,
    onLongClick: () -> Unit,
    openUserProfile: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        BasicText(text = stringResource(R.string.second_place), fontSize = 22)
        BasicContainer(
            backGroundColor = gray,
            onClick = openUserProfile,
            onLongClick = onLongClick
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AsyncImage(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape),
                        model = session.profileImage,
                        contentDescription = null
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        BasicText(text = session.userName, fontSize = 22, fontColor = closeToBlack)
                        BasicText(text = session.title.getString(), fontColor = closeToBlack)
                    }
                }
                BasicContainer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(66.dp)
                ) {
                    BasicText(
                        modifier = Modifier
                            .align(Alignment.Center),
                        text = stringResource(
                            R.string.points, when (rule) {
                                LeagueRule.QUIZ_AND_WORDLE -> session.totalPoints
                                LeagueRule.QUIZ_ONLY -> session.pointsForQuiz
                                LeagueRule.WORDLE_ONLY -> session.pointsForWordle
                            }
                        ),
                        fontSize = 36
                    )
                }
            }
        }
    }
}

@Composable
fun OthersPositions(
    backGroundColor: Color,
    placementString: String,
    session: SessionInLeague,
    textColor: Color,
    rule: LeagueRule, onLongClick: () -> Unit,
    openUserProfile: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        BasicText(
            text = placementString,
            fontSize = 18
        )
        BasicContainer(
            backGroundColor = backGroundColor,
            onClick = openUserProfile,
            onLongClick = onLongClick
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AsyncImage(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .align(Alignment.CenterVertically)
                        .fillMaxWidth()
                        .weight(0.2f)
                        .aspectRatio(1f),
                    model = session.profileImage,
                    contentDescription = null
                )
                Column(modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .fillMaxWidth()
                    .weight(0.7f)) {
                    BasicText(
                        text = session.userName,
                        fontSize = 22,
                        fontColor = textColor
                    )
                    BasicText(
                        text = session.title.getString(),
                        fontColor = textColor
                    )
                }
                BasicText(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .fillMaxWidth()
                        .weight(0.3f),
                    text = stringResource(
                        R.string.p, when (rule) {
                            LeagueRule.QUIZ_AND_WORDLE -> session.totalPoints
                            LeagueRule.QUIZ_ONLY -> session.pointsForQuiz
                            LeagueRule.WORDLE_ONLY -> session.pointsForWordle
                        }
                    ),
                    fontSize = 22,
                    textAlign = TextAlign.End,
                    fontColor = textColor
                )
            }
        }
    }
}

@Composable
fun UpdateTitleDialog(session: SessionInLeague ,onDismissRequest: () -> Unit, updateTitle: (UserTitle) -> Unit) {

    val listOfLeagueDurationOptions = UserTitle.values()

    val (selectedOption, onOptionSelected) = remember {
        mutableStateOf(
            session.title
        )
    }

    BasicPositiveNegativeDialog(
        onDismissRequest = onDismissRequest,
        title = stringResource(R.string.update_your_title),
        dialogIcon = null,
        positiveString = stringResource(R.string.update),
        negativeString = stringResource(R.string.go_back),
        positiveFunction = {
            updateTitle(selectedOption)
        },
        negativeIcon = painterResource(R.drawable.baseline_arrow_back_24)
    ) {
        LazyColumn (verticalArrangement = Arrangement.spacedBy(4.dp)) {
            items(listOfLeagueDurationOptions) { option ->
                BasicRadioButton(
                    modifier = Modifier.height(50.dp),
                    selected = option == selectedOption,
                    updateRadioButton = {
                        onOptionSelected(option)
                    }
                ) {
                    BasicText(
                        modifier = Modifier
                            .padding(8.dp)
                            .align(Alignment.Center),
                        text = option.getString(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLeagueScreen() {
    NovaGincanaBiblicaTheme {
        LeagueScreen(
            League(
                listOfUsers =
                listOf(
                    SessionInLeague(
                        userName = "Benjamin",
                        title = UserTitle.NEW_BELIEVER,
                        pointsForWordle = 22,
                        pointsForQuiz = 15
                    ),
                    SessionInLeague(
                        userName = "Abbie",
                        title = UserTitle.NEW_BELIEVER,
                        pointsForWordle = 36,
                        pointsForQuiz = 12
                    ),
                    SessionInLeague(userName = "Leia", pointsForWordle = 12, pointsForQuiz = 10),
                    SessionInLeague(userName = "Adeline", pointsForWordle = 0, pointsForQuiz = 2)
                )
            ), sessionInLeague = SessionInLeague(), navigateEditScreen = {

            }, openUserProfile = {}
        ) {}
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLeagueScreenIfAdmin() {
    NovaGincanaBiblicaTheme {
        LeagueScreen(
            League(
                listOfUsers =
                listOf(
                    SessionInLeague(
                        userName = "Benjamin",
                        title = UserTitle.NEW_BELIEVER,
                        pointsForWordle = 22,
                        pointsForQuiz = 15
                    ),
                    SessionInLeague(
                        userName = "Abbie",
                        title = UserTitle.NEW_BELIEVER,
                        pointsForWordle = 36,
                        pointsForQuiz = 12
                    ),
                    SessionInLeague(userName = "Leia", pointsForWordle = 12, pointsForQuiz = 10),
                    SessionInLeague(userName = "Adeline", pointsForWordle = 0, pointsForQuiz = 2)
                )
            ), sessionInLeague = SessionInLeague(adminUser = true), navigateEditScreen = {

            }, openUserProfile = {}
        ) {

        }
    }
}