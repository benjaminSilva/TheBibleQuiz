package com.bsoftwares.thebiblequiz.ui.screens.profile

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.bsoftwares.thebiblequiz.R
import com.bsoftwares.thebiblequiz.data.models.League
import com.bsoftwares.thebiblequiz.data.models.Session
import com.bsoftwares.thebiblequiz.data.models.isReady
import com.bsoftwares.thebiblequiz.data.models.state.DialogType
import com.bsoftwares.thebiblequiz.data.models.state.FeedbackMessage
import com.bsoftwares.thebiblequiz.data.models.state.ProfileDialogType
import com.bsoftwares.thebiblequiz.data.models.state.getPainter
import com.bsoftwares.thebiblequiz.ui.basicviews.AnimatedBorderCard
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicContainer
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicDialog
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicPositiveNegativeDialog
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicScreenBox
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicText
import com.bsoftwares.thebiblequiz.ui.basicviews.animateColor
import com.bsoftwares.thebiblequiz.ui.basicviews.generateSubSequentialAlphaAnimations
import com.bsoftwares.thebiblequiz.ui.screens.Routes
import com.bsoftwares.thebiblequiz.ui.screens.games.quiz.QuizStats
import com.bsoftwares.thebiblequiz.ui.screens.games.quiz.screens.ButtonWithHold
import com.bsoftwares.thebiblequiz.ui.screens.games.wordle.WordleStats
import com.bsoftwares.thebiblequiz.ui.theme.NovaGincanaBiblicaTheme
import com.bsoftwares.thebiblequiz.ui.theme.almostBlack
import com.bsoftwares.thebiblequiz.ui.theme.almostWhite
import com.bsoftwares.thebiblequiz.ui.theme.basicContainer
import com.bsoftwares.thebiblequiz.ui.theme.basicContainerClean
import com.bsoftwares.thebiblequiz.ui.theme.closeToBlack
import com.bsoftwares.thebiblequiz.ui.theme.container_in_container
import com.bsoftwares.thebiblequiz.ui.theme.contrastColor
import com.bsoftwares.thebiblequiz.ui.theme.darkGray
import com.bsoftwares.thebiblequiz.ui.theme.emptyString
import com.bsoftwares.thebiblequiz.ui.theme.gray
import com.bsoftwares.thebiblequiz.ui.theme.lessWhite
import com.bsoftwares.thebiblequiz.ui.theme.lighterGray
import com.bsoftwares.thebiblequiz.ui.theme.prettyMuchBlack
import com.bsoftwares.thebiblequiz.viewmodel.HomeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest


@Composable
fun InitializeProfileScreen(
    navController: NavHostController,
    homeViewModel: HomeViewModel,
    userId: String
) {
    val dialog by homeViewModel.displayDialog.collectAsStateWithLifecycle()
    val calculatedQuizData by homeViewModel.calculatedQuizData.collectAsStateWithLifecycle()
    val calculatedWordleData by homeViewModel.calculatedWordleData.collectAsStateWithLifecycle()
    val feedbackMessage by homeViewModel.feedbackMessage.collectAsStateWithLifecycle()
    val friendsRequests by homeViewModel.listOfFriendRequests.collectAsStateWithLifecycle()
    val friends by homeViewModel.listOfFriends.collectAsStateWithLifecycle()
    val listOfLeagues by homeViewModel.listOfLeague.collectAsStateWithLifecycle()
    val listOfLeagueInvitations by homeViewModel.listOfLeagueInvitation.collectAsStateWithLifecycle()
    val localSession by homeViewModel.localSession.collectAsStateWithLifecycle()

    var displaySession by remember {
        mutableStateOf(Session())
    }

    var listOfFriends by remember {
        mutableStateOf(listOf<Session>())
    }

    LaunchedEffect(Unit) {
        if (userId == localSession.userInfo.userId) {
            displaySession = localSession
            listOfFriends = friends
        } else {
            homeViewModel.updateVisibleSession(userId = userId).collectLatest {
                displaySession = it.first
                listOfFriends = it.second
            }
        }
    }

    LaunchedEffect(localSession) {
        if (!localSession.isReady()) {
            navController.navigate(Routes.LoginScreen.value) {
                popUpTo(Routes.Home.value) { inclusive = true }
            }
            homeViewModel.updateDialog(DialogType.EmptyValue)
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
            ProfileDialogType.Quiz -> {
                BasicDialog(onDismissRequest = {
                    homeViewModel.updateDialog()
                }) {
                    QuizStats(
                        data = displaySession.quizStats,
                        calculatedData = calculatedQuizData,
                        isFromProfileScreen = true
                    ) {
                        homeViewModel.updateDialog()
                    }
                }
            }

            ProfileDialogType.Wordle -> {
                BasicDialog(onDismissRequest = {
                    homeViewModel.updateDialog()
                }) {
                    WordleStats(
                        wordleStats = displaySession.wordle.wordleStats,
                        progresses = calculatedWordleData,
                        isFromProfileScreen = true
                    ) {
                        homeViewModel.updateDialog()
                    }
                }
            }

            ProfileDialogType.AddFriend -> {
                BasicDialog(onDismissRequest = {
                    homeViewModel.updateDialog()
                    homeViewModel.resetErrorMessage()
                }) {
                    AddFriendDialog(
                        errorMessage = feedbackMessage.get(),
                        goBackClick = {
                            homeViewModel.updateDialog()
                        },
                        addUser = {
                            homeViewModel.addFriend(it)
                        }) {
                        homeViewModel.resetErrorMessage()
                    }
                }
            }

            ProfileDialogType.RemoveFriend -> {
                BasicPositiveNegativeDialog(
                    dialogIcon = null,
                    title = stringResource(R.string.friend_removal),
                    description = stringResource(R.string.are_you_sure_you_want_to_remove_this_friend),
                    onDismissRequest = {
                        homeViewModel.updateDialog()
                    },
                    positiveFunction = {
                        homeViewModel.removeFriend(displaySession.userInfo.userId)
                    },
                    positiveIcon = painterResource(R.drawable.baseline_delete_24)
                )
            }

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

    LaunchedEffect(feedbackMessage) {
        if (feedbackMessage == FeedbackMessage.LeagueCreated) {
            navController.navigate(Routes.LeagueScreen.value)
        }
    }

    BasicScreenBox(
        feedbackMessage = feedbackMessage,
        conditionToDisplayFeedbackMessage = profileScreenFeedbackMessages.contains(feedbackMessage),
        dialogType = dialog
    ) {
        if (localSession.isReady() && displaySession.userInfo.userId.isNotEmpty()) {
            ProfileScreen(
                session = displaySession,
                calculateQuizData = { homeViewModel.calculateQuizData(session = displaySession) },
                calculateWordleData = { homeViewModel.calculateWordleData(session = displaySession) },
                displayDialogFunction = { isThisQuiz ->
                    homeViewModel.updateDialog(dialogType = isThisQuiz)
                },
                listOfFriendRequests = friendsRequests,
                listOfFriends = listOfFriends,
                updateFriendRequest = { hasAccepted, userId ->
                    homeViewModel.updateFriendRequest(hasAccepted, userId)
                },
                isFromLocalSession = userId == localSession.userInfo.userId,
                updateVisibleSession = {
                    if (it == null) {
                        navController.popBackStack(
                            Routes.Profile.withParameter(localSession.userInfo.userId),
                            inclusive = false
                        )
                    } else {
                        navController.navigate(Routes.Profile.withParameter(it.userInfo.userId))
                    }
                },
                removeFriend = {
                    homeViewModel.updateDialog(
                        dialogType = ProfileDialogType.RemoveFriend
                    )
                },
                possibleToAdd = homeViewModel.checkIfSessionIsNotFriendsWithLocal(displaySession),
                notFriendRequest = homeViewModel.checkIfSessionDoesntAlreadyHaveAFriendRequest(
                    displaySession
                ),
                createNewLeague = {
                    homeViewModel.createNewLeague()
                },
                listOfLeagues = listOfLeagues,
                openLeague = {
                    homeViewModel.setCurrentLeague(it)
                    navController.navigate(Routes.LeagueScreen.value) {
                        launchSingleTop = true
                    }
                },
                listOfLeagueInvitations = listOfLeagueInvitations,
                updateLeagueInvitation = { hasAccepted, leagueId ->
                    homeViewModel.updateLeagueInvitation(hasAccepted, leagueId)
                },
                addUser = {
                    displaySession.userInfo.userId.apply {
                        homeViewModel.addFriend(this)
                    }
                },
                backToHome = {
                    navController.popBackStack(Routes.Home.value, inclusive = false)
                }
            ) {
                homeViewModel.signOut()
            }
        }
    }

}

@Composable
fun ProfileScreen(
    session: Session,
    calculateQuizData: () -> Unit,
    calculateWordleData: () -> Unit,
    displayDialogFunction: (ProfileDialogType) -> Unit,
    listOfFriendRequests: List<Session>,
    listOfFriends: List<Session>,
    updateFriendRequest: (Boolean, String?) -> Unit,
    isFromLocalSession: Boolean,
    updateVisibleSession: (Session?) -> Unit,
    removeFriend: () -> Unit,
    possibleToAdd: Boolean,
    notFriendRequest: Boolean,
    createNewLeague: () -> Unit,
    listOfLeagues: List<League>,
    openLeague: (League) -> Unit,
    listOfLeagueInvitations: List<League>,
    updateLeagueInvitation: (Boolean, String) -> Unit,
    addUser: () -> Unit,
    backToHome: () -> Unit,
    signOut: () -> Unit
) {
    val clipboardManager: ClipboardManager = LocalClipboardManager.current

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {

                when {
                    isFromLocalSession -> {
                        BasicContainer(
                            modifier = Modifier
                                .align(Alignment.CenterStart), onClick = backToHome
                        ) {
                            Image(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                    .size(24.dp)
                                    .align(Alignment.Center),
                                painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                                contentDescription = null
                            )
                        }
                    }

                    !isFromLocalSession && possibleToAdd -> {
                        BasicContainer(
                            modifier = Modifier
                                .align(Alignment.CenterStart), onClick = addUser
                        ) {
                            Image(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                    .size(24.dp)
                                    .align(Alignment.Center),
                                painter = painterResource(id = R.drawable.baseline_person_add_24),
                                contentDescription = null
                            )
                        }
                    }

                    !isFromLocalSession && notFriendRequest -> {
                        BasicContainer(modifier = Modifier
                            .align(Alignment.CenterStart), onClick = {
                            removeFriend()
                        }) {
                            Image(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                    .size(24.dp)
                                    .align(Alignment.Center),
                                painter = painterResource(id = R.drawable.baseline_delete_24),
                                contentDescription = null
                            )
                        }
                    }
                }

                BasicContainer(
                    Modifier
                        .align(Alignment.CenterEnd)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            if (isFromLocalSession) {
                                signOut()
                            } else {
                                updateVisibleSession(null)
                            }
                        }) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Image(
                            modifier = Modifier
                                .size(24.dp),
                            painter = painterResource(id = if (isFromLocalSession) R.drawable.logout_24 else R.drawable.baseline_arrow_back_24),
                            contentDescription = null
                        )
                        BasicText(
                            modifier = Modifier.align(Alignment.CenterVertically),
                            text = if (isFromLocalSession) stringResource(R.string.logout) else stringResource(
                                R.string.go_back_to_your_profile
                            ),
                            fontSize = 16
                        )
                    }
                }
            }

            BasicContainer(
                onClick = {
                    session.userInfo.userId.apply {
                        clipboardManager.setText(AnnotatedString(this))
                    }
                },
                allowAnimation = false
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AsyncImage(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape),
                                model = session.userInfo.profilePictureUrl,
                                contentDescription = null
                            )
                            Column(
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                BasicText(text = session.userInfo.userName, fontSize = 22)
                                BasicText(
                                    text = stringResource(
                                        R.string.id,
                                        session.userInfo.userId
                                    ), fontSize = 8
                                )
                                if (session.premium) {
                                    BasicText(
                                        text = stringResource(R.string.premium_user),
                                        fontSize = 8
                                    )
                                } else {
                                    BasicText(
                                        text = stringResource(R.string.non_premium_user),
                                        fontSize = 8
                                    )
                                }
                            }
                        }

                        Image(
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(8.dp)
                                .size(32.dp),
                            painter = painterResource(id = R.drawable.baseline_content_copy_24),
                            contentDescription = null
                        )
                    }
                    if (!session.premium && isFromLocalSession) {
                        AnimatedBorderCard {
                            BasicContainer(
                                backGroundColor = colorResource(id = R.color.background_color),
                                onClick = {
                                    displayDialogFunction(ProfileDialogType.StartPremium)
                                }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Image(
                                        modifier = Modifier
                                            .size(24.dp),
                                        painter = painterResource(id = R.drawable.crown_svgrepo_com),
                                        contentDescription = null
                                    )
                                    BasicText(
                                        modifier = Modifier.align(Alignment.CenterVertically),
                                        text = stringResource(R.string.get_premium)
                                    )
                                }
                            }
                        }
                    }
                }
            }
            BasicText(text = "My stats", fontSize = 22)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                BasicContainer(modifier = Modifier.weight(1f), onClick = {
                    calculateQuizData()
                    displayDialogFunction(ProfileDialogType.Quiz)
                }) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Image(
                            modifier = Modifier
                                .size(24.dp),
                            painter = painterResource(id = R.drawable.baseline_menu_book_24),
                            contentDescription = null
                        )
                        BasicText(
                            modifier = Modifier.align(Alignment.CenterVertically),
                            text = stringResource(R.string.quiz)
                        )
                    }
                }
                BasicContainer(modifier = Modifier.weight(1f), onClick = {
                    calculateWordleData()
                    displayDialogFunction(ProfileDialogType.Wordle)
                }) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Image(
                            modifier = Modifier
                                .size(24.dp),
                            painter = painterResource(id = R.drawable.baseline_border_clear_24),
                            contentDescription = null
                        )
                        BasicText(
                            modifier = Modifier.align(Alignment.CenterVertically),
                            text = stringResource(R.string.wordle),
                            fontSize = 16
                        )
                    }
                }
            }

            if (isFromLocalSession) {
                BasicText(text = stringResource(R.string.leagues), fontSize = 22)
                BasicContainer {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ButtonWithHold(modifier = Modifier.fillMaxWidth(), holdAction = {
                            createNewLeague()
                        }) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    LeaguesIcon()
                                    BasicText(
                                        modifier = Modifier.align(Alignment.CenterVertically),
                                        text = stringResource(R.string.create_a_new_league_hold),
                                        fontSize = 22
                                    )
                                }
                            }
                        }
                        if (listOfLeagueInvitations.isNotEmpty()) {
                            BasicText(text = stringResource(R.string.leagues_invitations))
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                listOfLeagueInvitations.onEach {
                                    LeagueInvitation(it, openLeague, updateLeagueInvitation)
                                }
                            }
                        }
                        if (listOfLeagues.isNotEmpty()) {
                            BasicText(text = stringResource(R.string.your_leagues))
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                listOfLeagues.onEach {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(16.dp))
                                            .clickable {
                                                openLeague(it)
                                            }
                                            .background(gray)
                                            .padding(8.dp),
                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .clip(CircleShape)
                                                .background(
                                                    almostWhite
                                                )
                                        ) {
                                            Image(
                                                modifier = Modifier
                                                    .padding(8.dp)
                                                    .size(24.dp),
                                                painter = it.leagueIcon.getPainter(),
                                                contentDescription = null,
                                                colorFilter = ColorFilter.tint(closeToBlack)
                                            )
                                        }
                                        BasicText(
                                            modifier = Modifier.align(Alignment.CenterVertically),
                                            text = it.leagueName,
                                            fontSize = 22,
                                            fontColor = closeToBlack
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            BasicText(text = "Friends list", fontSize = 22)
            BasicContainer {
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .animateContentSize()
                        .clip(RoundedCornerShape(16.dp)),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (isFromLocalSession) {
                        AddFriendButton { dialogType ->
                            displayDialogFunction(dialogType)
                        }
                    }
                    if (listOfFriends.isNotEmpty() || listOfFriendRequests.isNotEmpty()) {
                        Column(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(container_in_container())
                                .padding(horizontal = 8.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (listOfFriendRequests.isNotEmpty() && isFromLocalSession) {
                                BasicText(
                                    text = stringResource(R.string.friend_requests)
                                )
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    listOfFriendRequests.forEach {
                                        FriendRequest(
                                            profilePicture = it.userInfo.profilePictureUrl,
                                            userName = it.userInfo.userName,
                                            updateVisibleSession = {
                                                updateVisibleSession(it)
                                            }
                                        ) { hasAccepted ->
                                            updateFriendRequest(
                                                hasAccepted,
                                                it.userInfo.userId
                                            )
                                        }
                                    }
                                }
                            }
                            if (listOfFriends.isNotEmpty()) {
                                BasicText(
                                    text = stringResource(R.string.friends)
                                )
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    listOfFriends.forEach {
                                        FriendItem(
                                            profilePicture = it.userInfo.profilePictureUrl,
                                            userName = it.userInfo.userName
                                        ) {
                                            updateVisibleSession(it)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddFriendButton(displayDialogFunction: (ProfileDialogType) -> Unit) {
    Box {
        Row(modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable {
                displayDialogFunction(
                    ProfileDialogType.AddFriend
                )
            }
            .padding(8.dp)) {
            Image(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .align(Alignment.CenterVertically),
                painter = painterResource(id = R.drawable.baseline_add_24),
                contentDescription = null
            )
            BasicText(
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.CenterVertically),
                text = stringResource(R.string.add_new_friend),
                fontSize = 18
            )
        }
    }
}

@Composable
fun FriendRequest(
    modifier: Modifier = Modifier,
    profilePicture: String?,
    userName: String?,
    updateVisibleSession: () -> Unit,
    updateFriendStatus: (Boolean) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable {
                updateVisibleSession()
            }
            .background(colorResource(id = R.color.basic_container_color))
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .fillMaxWidth()
        ) {

            Row(
                modifier = Modifier.align(Alignment.CenterStart),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AsyncImage(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape),
                    model = profilePicture,
                    contentDescription = null
                )
                BasicText(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.CenterVertically),
                    text = userName,
                    fontSize = 18
                )
            }

            Row(
                modifier = Modifier.align(Alignment.CenterEnd),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .size(24.dp)
                        .clickable {
                            updateFriendStatus(false)
                        },
                    painter = painterResource(id = R.drawable.baseline_close_24_bw),
                    contentDescription = emptyString
                )
                Image(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .size(24.dp)
                        .clickable {
                            updateFriendStatus(true)
                        },
                    painter = painterResource(id = R.drawable.baseline_check_24_bw),
                    contentDescription = emptyString
                )
            }
        }

    }
}

@Composable
fun LeaguesIcon(modifier: Modifier = Modifier) {

    var startAnimation by remember {
        mutableStateOf(true)
    }

    val animateHeight = generateSubSequentialAlphaAnimations(
        numberOfViews = 5,
        condition = startAnimation,
        initialDelay = -200
    )

    LaunchedEffect(Unit) {
        delay(500)
        startAnimation = false
    }

    Box(
        modifier = modifier
            .size(56.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(top = 18.dp)
                .align(Alignment.BottomEnd)
                .width(22.dp)
                .fillMaxHeight(animateHeight[0].value)
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomEnd = 8.dp))
                .background(lighterGray)
        ) {
            BasicText(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 4.dp), text = "3"
            )
        }
        Box(
            modifier = Modifier
                .padding(top = 9.dp)
                .align(Alignment.BottomStart)
                .width(22.dp)
                .fillMaxHeight(animateHeight[1].value)
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomStart = 8.dp))
                .background(darkGray)
        ) {
            BasicText(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 4.dp), text = "2", fontColor = almostWhite
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxHeight(animateHeight[2].value)
                .width(22.dp)
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .background(prettyMuchBlack)
        ) {
            BasicText(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 4.dp), text = "1", fontColor = almostWhite
            )
        }
    }
}

@Composable
fun FriendItem(
    modifier: Modifier = Modifier,
    profilePicture: String?,
    userName: String?,
    selectable: Boolean = false,
    updateVisibleSession: () -> Unit
) {

    var isSelected by remember {
        mutableStateOf(false)
    }

    val animateColorCheckBox by animateColor(
        condition = isSelected,
        startValue = prettyMuchBlack,
        endValue = almostWhite,
        duration = 200
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(basicContainer())
            .clickable {
                updateVisibleSession()
                if (selectable) {
                    isSelected = !isSelected
                }
            }
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        AsyncImage(
            modifier = Modifier
                .size(32.dp)
                .align(Alignment.CenterVertically)
                .clip(CircleShape),
            model = profilePicture,
            contentDescription = null
        )
        BasicText(
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.CenterVertically),
            text = userName,
            fontSize = 18
        )
        Spacer(Modifier.weight(1f)) // height and background only for demonstration
        if (selectable) {
            BasicContainer(
                modifier = Modifier.align(Alignment.CenterVertically),
                backGroundColor = animateColorCheckBox
            ) {
                Image(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(28.dp),
                    painter = painterResource(R.drawable.baseline_check_24),
                    contentDescription = emptyString,
                    colorFilter = ColorFilter.tint(almostWhite)
                )
            }
        }
    }

}

@Composable
fun LeagueInvitation(
    league: League,
    openLeague: (League) -> Unit,
    updateLeagueInvitation: (Boolean, String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable {
                openLeague(league)
            }
            .background(lessWhite)
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.align(Alignment.CenterStart),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(
                        almostWhite
                    )
            ) {
                Image(
                    modifier = Modifier
                        .padding(8.dp)
                        .size(24.dp),
                    painter = league.leagueIcon.getPainter(),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(closeToBlack)
                )
            }
            BasicText(
                modifier = Modifier.align(Alignment.CenterVertically),
                text = league.leagueName,
                fontSize = 22,
                fontColor = closeToBlack
            )
        }
        Row(
            modifier = Modifier.align(Alignment.CenterEnd),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .size(24.dp)
                    .clickable {
                        updateLeagueInvitation(false, league.leagueId)
                    },
                painter = painterResource(id = R.drawable.baseline_close_24_bw),
                contentDescription = emptyString,
                colorFilter = ColorFilter.tint(almostBlack)
            )
            Image(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .size(24.dp)
                    .clickable {
                        updateLeagueInvitation(true, league.leagueId)
                    },
                painter = painterResource(id = R.drawable.baseline_check_24_bw),
                contentDescription = emptyString,
                colorFilter = ColorFilter.tint(almostBlack)
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ProfilePreview() {
    NovaGincanaBiblicaTheme {
        ProfileScreen(
            session = Session(),
            calculateQuizData = { },
            calculateWordleData = { },
            displayDialogFunction = { _ -> },
            listOfFriendRequests = listOf(),
            listOfFriends = listOf(),
            updateFriendRequest = { _, _ -> },
            isFromLocalSession = false,
            updateVisibleSession = { _ -> },
            removeFriend = {},
            possibleToAdd = false,
            notFriendRequest = true,
            createNewLeague = {},
            addUser = {},
            signOut = {},
            openLeague = {},
            listOfLeagues = listOf(
                League(leagueName = "League test"),
                League(leagueName = "League test")
            ),
            listOfLeagueInvitations = listOf(),
            backToHome = {},
            updateLeagueInvitation = { _, _ -> }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewIcon() {
    LeaguesIcon()
}

@Preview(showBackground = true)
@Composable
fun PreviewViewLeagueInviationView() {
    LeagueInvitation(League(leagueName = "Test Name"), {}) { test, teste ->

    }
}

val profileScreenFeedbackMessages = listOf(
    FeedbackMessage.FriendRequestSent,
    FeedbackMessage.FriendRemoved,
    FeedbackMessage.YouHaveAlreadySent,
    FeedbackMessage.LeagueDeleted,
    FeedbackMessage.YouAreNotPremium
)