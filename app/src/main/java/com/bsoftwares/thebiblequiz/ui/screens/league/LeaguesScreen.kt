package com.bsoftwares.thebiblequiz.ui.screens.league

import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.bsoftwares.thebiblequiz.R
import com.bsoftwares.thebiblequiz.data.models.League
import com.bsoftwares.thebiblequiz.data.models.LeagueRule
import com.bsoftwares.thebiblequiz.data.models.Session
import com.bsoftwares.thebiblequiz.data.models.SessionInLeague
import com.bsoftwares.thebiblequiz.data.models.getString
import com.bsoftwares.thebiblequiz.data.models.state.DialogType
import com.bsoftwares.thebiblequiz.data.models.state.LeagueDialog
import com.bsoftwares.thebiblequiz.data.models.state.getPainter
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicContainer
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicDialog
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicText
import com.bsoftwares.thebiblequiz.ui.screens.Routes
import com.bsoftwares.thebiblequiz.ui.screens.profile.FriendItem
import com.bsoftwares.thebiblequiz.ui.theme.NovaGincanaBiblicaTheme
import com.bsoftwares.thebiblequiz.ui.theme.almostWhite
import com.bsoftwares.thebiblequiz.ui.theme.closeToBlack
import com.bsoftwares.thebiblequiz.ui.theme.darkYellow
import com.bsoftwares.thebiblequiz.ui.theme.gray
import com.bsoftwares.thebiblequiz.ui.theme.lessWhite
import com.bsoftwares.thebiblequiz.ui.theme.lightBrown
import com.bsoftwares.thebiblequiz.ui.theme.wrongPlace
import com.bsoftwares.thebiblequiz.viewmodel.HomeViewModel

@Composable
fun InitializeLeagueScreen(navController: NavHostController, viewModel: HomeViewModel) {
    val league by viewModel.currentLeague.collectAsStateWithLifecycle()
    val dialog by viewModel.displayDialog.collectAsStateWithLifecycle()
    val friendsNotInThisLeague by viewModel.listOfFriendsNotInLeague.collectAsStateWithLifecycle()
    val currentSessionInLeague by viewModel.sessionInLeague.collectAsStateWithLifecycle()

    BackHandler {
        viewModel.updateVisibleSession(null)
        viewModel.updateIsFromLeague()
        navController.popBackStack()
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

            else -> Unit
        }
    }

    LeagueScreen(league = league, sessionInLeague = currentSessionInLeague, navigateEditScreen = {
        navController.navigate(Routes.EditLeague.value)
    }, openUserProfile = {
        viewModel.updateVisibleSession(userId = it)
        navController.popBackStack()
    }) {
        viewModel.updateDialog(it)
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

            BasicContainer(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    league.listOfUsers.onEachIndexed { index, sessionInLeague ->
                        when (index) {
                            0 -> {
                                FirstPosition(session = sessionInLeague, rule = league.leagueRule) {
                                    openUserProfile(sessionInLeague.userId)
                                }
                            }

                            1 -> SecondPosition(
                                session = sessionInLeague,
                                rule = league.leagueRule
                            ) {
                                openUserProfile(sessionInLeague.userId)
                            }

                            2 -> OthersPositions(
                                session = sessionInLeague,
                                rule = league.leagueRule
                            ) {
                                openUserProfile(sessionInLeague.userId)
                            }

                            else -> OthersPositions(
                                backGroundColor = colorResource(id = R.color.basic_container_color),
                                session = sessionInLeague,
                                index = index + 1,
                                rule = league.leagueRule
                            ) {
                                openUserProfile(sessionInLeague.userId)
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
                }, containerColor = almostWhite
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
                BasicText(text = "Friends you can add to this League", fontSize = 22)
                if (friendsList.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        friendsList.onEach {
                            FriendItem(
                                profilePicture = it.userInfo.profilePictureUrl,
                                userName = it.userInfo.userName,
                                backgroundColor = lessWhite,
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
                    BasicText(text = "You don't have any friends that could be added. (Kinda sad)")
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
                    text = "Send Invitation"
                )
            }
        }
    }
}

@Composable
fun FirstPosition(session: SessionInLeague, rule: LeagueRule, openUserProfile: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        BasicText(text = "First place", fontSize = 26)
        BasicContainer(backGroundColor = wrongPlace, onClick = { openUserProfile() }) {
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
                        BasicText(text = session.title, fontColor = closeToBlack)
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
                        text = "${
                            when (rule) {
                                LeagueRule.QUIZ_AND_WORDLE -> session.totalPoints
                                LeagueRule.QUIZ_ONLY -> session.pointsForQuiz
                                LeagueRule.WORDLE_ONLY -> session.pointsForWordle
                            }
                        } Points",
                        fontSize = 48
                    )
                }
            }
        }
    }

}

@Composable
fun SecondPosition(session: SessionInLeague, rule: LeagueRule, openUserProfile: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        BasicText(text = "Second Place", fontSize = 22)
        BasicContainer(backGroundColor = gray, onClick = { openUserProfile() }) {
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
                        BasicText(text = session.title, fontColor = closeToBlack)
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
                        text = "${
                            when (rule) {
                                LeagueRule.QUIZ_AND_WORDLE -> session.totalPoints
                                LeagueRule.QUIZ_ONLY -> session.pointsForQuiz
                                LeagueRule.WORDLE_ONLY -> session.pointsForWordle
                            }
                        } Points",
                        fontSize = 36
                    )
                }
            }
        }
    }
}

@Composable
fun OthersPositions(
    backGroundColor: Color = lightBrown,
    session: SessionInLeague,
    index: Int = 0,
    rule: LeagueRule,
    openUserProfile: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        BasicText(
            text = if (backGroundColor == lightBrown) "Third Place" else "$index° Place",
            fontSize = 18
        )
        BasicContainer(backGroundColor = backGroundColor, onClick = {
            openUserProfile()
        }) {
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
                BasicText(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .fillMaxWidth()
                        .weight(0.7f),
                    text = session.userName,
                    fontSize = 22,
                    fontColor = closeToBlack
                )
                BasicText(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .fillMaxWidth()
                        .weight(0.3f),
                    text = "${
                        when (rule) {
                            LeagueRule.QUIZ_AND_WORDLE -> session.totalPoints
                            LeagueRule.QUIZ_ONLY -> session.pointsForQuiz
                            LeagueRule.WORDLE_ONLY -> session.pointsForWordle
                        }
                    }P",
                    fontSize = 22,
                    textAlign = TextAlign.End,
                    fontColor = closeToBlack
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLeagueScreen() {
    NovaGincanaBiblicaTheme {
        LeagueScreen(
            League(listOfUsers =
            listOf(
                SessionInLeague(
                    userName = "Benjamin",
                    title = "By the grace of God",
                    pointsForWordle = 22,
                    pointsForQuiz = 15
                ),
                SessionInLeague(
                    userName = "Abbie",
                    title = "Alone in the desert",
                    pointsForWordle = 36,
                    pointsForQuiz = 12
                ),
                SessionInLeague(userName = "Leia", pointsForWordle = 12, pointsForQuiz = 10),
                SessionInLeague(userName = "Adeline", pointsForWordle = 0, pointsForQuiz = 2)
            )
            ), sessionInLeague = SessionInLeague(), navigateEditScreen = {

            }, openUserProfile = {}
        ) {

        }
    }
}