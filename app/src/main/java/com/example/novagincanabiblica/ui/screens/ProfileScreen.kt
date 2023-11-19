package com.example.novagincanabiblica.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.novagincanabiblica.R
import com.example.novagincanabiblica.data.models.Session
import com.example.novagincanabiblica.data.models.state.FeedbackMessage
import com.example.novagincanabiblica.data.models.state.ProfileDialogType
import com.example.novagincanabiblica.ui.basicviews.AddFriendDialog
import com.example.novagincanabiblica.ui.basicviews.BasicText
import com.example.novagincanabiblica.ui.basicviews.FeedbackMessage
import com.example.novagincanabiblica.ui.basicviews.QuizStats
import com.example.novagincanabiblica.ui.basicviews.RemoveFriendDialog
import com.example.novagincanabiblica.ui.basicviews.animateAlpha
import com.example.novagincanabiblica.ui.screens.games.wordle.WordleStats
import com.example.novagincanabiblica.ui.theme.NovaGincanaBiblicaTheme
import com.example.novagincanabiblica.ui.theme.almostWhite
import com.example.novagincanabiblica.ui.theme.lessWhite
import com.example.novagincanabiblica.viewmodel.HomeViewModel
import kotlinx.coroutines.delay


@Composable
fun InitializeProfileScreen(navController: NavHostController, homeViewModel: HomeViewModel) {
    val userData by homeViewModel.visibleSession.collectAsStateWithLifecycle()
    val isFromMainUser by homeViewModel.isFromLocalSession.collectAsStateWithLifecycle()
    val displayData by homeViewModel.displayDialog.collectAsStateWithLifecycle()
    val calculatedQuizData by homeViewModel.calculatedQuizData.collectAsStateWithLifecycle()
    val calculatedWordleData by homeViewModel.calculatedWordleData.collectAsStateWithLifecycle()
    val feedbackMessage by homeViewModel.feedbackMessage.collectAsStateWithLifecycle()
    val friendsRequests by homeViewModel.listOfFriendRequests.collectAsStateWithLifecycle()
    val friends by homeViewModel.listOfFriends.collectAsStateWithLifecycle()
    val transitionAnimation by homeViewModel.transitionAnimation.collectAsStateWithLifecycle()
    val notFriends by homeViewModel.notFriends.collectAsStateWithLifecycle()

    BackHandler {
        navController.popBackStack()
        homeViewModel.updateVisibleSession(null)
    }

    val (dialogType, displayDialog) = displayData

    if (displayDialog) {
        when (dialogType as ProfileDialogType) {
            ProfileDialogType.Quiz -> {
                Dialog(onDismissRequest = {
                    homeViewModel.displayDialog(
                        displayIt = false
                    )
                }) {
                    QuizStats(
                        data = userData.quizStats,
                        calculatedData = calculatedQuizData,
                        isFromProfileScreen = true
                    ) {
                        homeViewModel.displayDialog(
                            displayIt = false
                        )
                    }
                }
            }

            ProfileDialogType.Wordle -> {
                Dialog(onDismissRequest = {
                    homeViewModel.displayDialog(
                        displayIt = false
                    )
                }) {
                    WordleStats(
                        wordleStats = userData.wordle.wordleStats,
                        progresses = calculatedWordleData,
                        isFromProfileScreen = true
                    ) {
                        homeViewModel.displayDialog(
                            displayIt = false
                        )
                    }
                }
            }

            ProfileDialogType.AddFriend -> {
                Dialog(onDismissRequest = {
                    homeViewModel.displayDialog(
                        displayIt = false
                    )
                }) {
                    AddFriendDialog(
                        errorMessage = stringResource(id = feedbackMessage.messageId),
                        goBackClick = {
                            homeViewModel.displayDialog(
                                displayIt = false
                            )
                        },
                        addUser = {
                            homeViewModel.addFriend(it)
                        }) {
                        homeViewModel.resetErrorMessage()
                    }
                }
            }

            ProfileDialogType.RemoveFriend -> {
                Dialog(onDismissRequest = {
                    homeViewModel.displayDialog(
                        displayIt = false
                    )
                }) {
                    RemoveFriendDialog(
                        goBackClick = {
                            homeViewModel.displayDialog(
                                displayIt = false
                            )
                        }) {
                        homeViewModel.removeFriend()
                    }
                }
            }
        }
    }

    val alphaAnimation by animateAlpha(condition = transitionAnimation, duration = 300, delay = 0)

    LaunchedEffect(transitionAnimation) {
        if (transitionAnimation) {
            delay(300)
            homeViewModel.finishTransitionAnimation()
        }
    }

    ProfileScreen(
        modifier = Modifier.alpha(alphaAnimation),
        session = userData,
        calculateQuizData = { homeViewModel.calculateQuizData(session = userData) },
        calculateWordleData = { homeViewModel.calculateWordleData(session = userData) },
        displayDialogFunction = { isThisQuiz, displayIt ->
            homeViewModel.displayDialog(dialogType = isThisQuiz, displayIt = displayIt)
        },
        listOfFriendRequests = friendsRequests,
        listOfFriends = friends,
        updateFriendRequest = { hasAccepted, userId ->
            homeViewModel.updateFriendRequest(hasAccepted, userId)
        },
        feedbackMessage = feedbackMessage,
        isFromLocalSession = isFromMainUser,
        updateVisibleSession = {
            homeViewModel.updateVisibleSession(it)
        },
        removeFriend = {
            homeViewModel.displayDialog(
                dialogType = ProfileDialogType.RemoveFriend,
                displayIt = true
            )
        },
        possibleToAdd = notFriends,
        addUser = {
            userData.userInfo?.userId?.apply {
                homeViewModel.addFriend(this)
            }
        }
    ) {
        homeViewModel.signOut()
        navController.popBackStack()
    }

}

@Composable
fun ProfileScreen(
    modifier: Modifier,
    session: Session,
    calculateQuizData: () -> Unit,
    calculateWordleData: () -> Unit,
    displayDialogFunction: (ProfileDialogType, Boolean) -> Unit,
    listOfFriendRequests: List<Session>,
    listOfFriends: List<Session>,
    updateFriendRequest: (Boolean, String?) -> Unit,
    feedbackMessage: FeedbackMessage,
    isFromLocalSession: Boolean,
    updateVisibleSession: (Session?) -> Unit,
    removeFriend: () -> Unit,
    possibleToAdd: Boolean,
    addUser: () -> Unit,
    signOut: () -> Unit
) {
    val clipboardManager: ClipboardManager = LocalClipboardManager.current

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                when {
                    !isFromLocalSession && possibleToAdd -> {
                        Box(modifier = Modifier
                            .shadow(20.dp)
                            .align(Alignment.CenterStart)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable {
                                addUser()
                            }
                            .background(almostWhite)
                            .padding(horizontal = 16.dp, vertical = 8.dp)) {
                            Image(
                                modifier = Modifier
                                    .size(24.dp)
                                    .align(Alignment.Center),
                                painter = painterResource(id = R.drawable.baseline_person_add_24),
                                contentDescription = null
                            )
                        }
                    }

                    !isFromLocalSession -> {
                        Box(modifier = Modifier
                            .shadow(20.dp)
                            .align(Alignment.CenterStart)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable {
                                removeFriend()
                            }
                            .background(almostWhite)
                            .padding(horizontal = 16.dp, vertical = 8.dp)) {
                            Image(
                                modifier = Modifier
                                    .size(24.dp)
                                    .align(Alignment.Center),
                                painter = painterResource(id = R.drawable.baseline_delete_24),
                                contentDescription = null
                            )
                        }
                    }
                }
                Box(modifier = Modifier
                    .shadow(20.dp)
                    .align(Alignment.CenterEnd)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable {
                        if (isFromLocalSession) {
                            signOut()
                        } else {
                            updateVisibleSession(null)
                        }
                    }
                    .background(almostWhite)) {
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
                            text = if (isFromLocalSession) "Logout" else "Go back to your Profile",
                            fontSize = 16
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            session.userInfo?.userId?.apply {
                                clipboardManager.setText(AnnotatedString(this))
                            }
                        }
                        .background(almostWhite)
                        .padding(8.dp),
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
                            model = session.userInfo?.profilePictureUrl,
                            contentDescription = null
                        )
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            BasicText(text = session.userInfo?.userName, fontSize = 22)
                            BasicText(text = "id: ${session.userInfo?.userId}", fontSize = 8)
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
            }

            BasicText(text = "My stats", fontSize = 22)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .shadow(20.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            calculateQuizData()
                            displayDialogFunction(ProfileDialogType.Quiz, true)
                        }
                        .background(almostWhite)
                ) {
                    Image(
                        modifier = Modifier
                            .padding(16.dp)
                            .size(24.dp),
                        painter = painterResource(id = R.drawable.baseline_menu_book_24),
                        contentDescription = null
                    )
                    BasicText(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        text = "Quiz", fontSize = 16
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .shadow(20.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            calculateWordleData()
                            displayDialogFunction(ProfileDialogType.Wordle, true)
                        }
                        .background(almostWhite)
                ) {
                    Image(
                        modifier = Modifier
                            .padding(16.dp)
                            .size(24.dp),
                        painter = painterResource(id = R.drawable.baseline_border_clear_24),
                        contentDescription = null
                    )
                    BasicText(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        text = "Wordle",
                        fontSize = 16
                    )
                }
            }
            BasicText(text = "Friends list", fontSize = 22)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(20.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(almostWhite)
                    .animateContentSize()
            ) {
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .animateContentSize()
                        .clip(RoundedCornerShape(16.dp)),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (isFromLocalSession) {
                        AddFriendButton { dialogType, shouldDisplay ->
                            displayDialogFunction(dialogType, shouldDisplay)
                        }
                    }
                    if (listOfFriends.isNotEmpty() || listOfFriendRequests.isNotEmpty()) {
                        Column(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(lessWhite)
                                .padding(horizontal = 8.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (listOfFriendRequests.isNotEmpty() && isFromLocalSession) {
                                BasicText(
                                    text = "Friend Requests"
                                )
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    listOfFriendRequests.forEach {
                                        FriendRequest(
                                            profilePicture = it.userInfo?.profilePictureUrl,
                                            userName = it.userInfo?.userName,
                                            updateVisibleSession = {
                                                updateVisibleSession(it)
                                            }
                                        ) { hasAccepted ->
                                            updateFriendRequest(
                                                hasAccepted,
                                                it.userInfo?.userId
                                            )
                                        }
                                    }
                                }
                            }
                            if (listOfFriends.isNotEmpty()) {
                                BasicText(text = "Friends")
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    listOfFriends.forEach {
                                        FriendItem(
                                            profilePicture = it.userInfo?.profilePictureUrl,
                                            userName = it.userInfo?.userName
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
        if (feedbackMessage == FeedbackMessage.FriendRequestSent || feedbackMessage == FeedbackMessage.FriendRemoved || feedbackMessage == FeedbackMessage.YouHaveAlreadySent) {
            FeedbackMessage(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp),
                errorMessage = feedbackMessage
            )
        }
    }
}

@Composable
fun AddFriendButton(displayDialogFunction: (ProfileDialogType, Boolean) -> Unit) {
    Box {
        Row(modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable {
                displayDialogFunction(
                    ProfileDialogType.AddFriend,
                    true
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
                text = "Add new friend",
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
            .background(almostWhite)
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
                    painter = painterResource(id = R.drawable.baseline_close_24),
                    contentDescription = ""
                )
                Image(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .size(24.dp)
                        .clickable {
                            updateFriendStatus(true)
                        },
                    painter = painterResource(id = R.drawable.baseline_check_24),
                    contentDescription = ""
                )
            }
        }

    }
}

@Composable
fun FriendItem(
    modifier: Modifier = Modifier,
    profilePicture: String?,
    userName: String?,
    updateVisibleSession: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(almostWhite)
            .clickable {
                updateVisibleSession()
            }
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
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ProfilePreview() {
    NovaGincanaBiblicaTheme {
        /*ProfileScreen(
            session = Session(),
            calculateQuizData = { },
            calculateWordleData = { },
            displayDialogFunction = { one, two ->

            },
            listOf()
        ) {

        }*/
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFriendRequest() {
    NovaGincanaBiblicaTheme {
        //FriendRequest(profilePicture = "", userName = "Benjamin")
    }
}