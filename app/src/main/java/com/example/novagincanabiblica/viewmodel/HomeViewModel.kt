package com.example.novagincanabiblica.viewmodel

import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.viewModelScope
import com.example.novagincanabiblica.data.models.BibleVerse
import com.example.novagincanabiblica.data.models.Session
import com.example.novagincanabiblica.data.models.state.FeedbackMessage
import com.example.novagincanabiblica.data.repositories.BaseRepository
import com.example.novagincanabiblica.ui.screens.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: BaseRepository
) : BaseViewModel(repo) {

    private val _dailyBibleVerse = MutableStateFlow(BibleVerse())
    val dailyBibleVerse = _dailyBibleVerse.asStateFlow()

    private val _hasUserPlayedLocally = MutableStateFlow(false)
    val hasUserPlayedLocally = _hasUserPlayedLocally.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private var _listOfFriendRequests = MutableStateFlow(listOf<Session>())
    val listOfFriendRequests = _listOfFriendRequests.asStateFlow()

    private var _listOfFriends = MutableStateFlow(listOf<Session>())
    val listOfFriends = _listOfFriends.asStateFlow()

    private val _visibleSession = MutableStateFlow(Session())
    val visibleSession = _visibleSession.asStateFlow()

    private val _isFromLocalSession = MutableStateFlow(true)
    val isFromLocalSession = _isFromLocalSession.asStateFlow()

    private val _transitionAnimation = MutableStateFlow(false)
    val transitionAnimation = _transitionAnimation.asStateFlow()

    private val _notFriends = MutableStateFlow(false)
    val notFriends = _notFriends.asStateFlow()

    private val _clickable = MutableStateFlow(true)
    val clickable = _clickable.asStateFlow()

    private val _navigate = Channel<Routes>()
    val navigate = _navigate.receiveAsFlow()

    private var loginSession: Job? = null

    init {
        loadToken()
        initHomeViewModel()
    }

    private fun loadToken() = backGroundScope.launch {
        repo.loadToken()
    }

    private fun initHomeViewModel() = backGroundScope.launch {
        day.collectLatest {
            if (it != -1) {
                listenToBibleVerseUpdate(it)
                localSession.collectLatest { session ->
                    val currentUserId = visibleSession.value.userInfo?.userId
                    if (currentUserId.isNullOrBlank() || currentUserId == session.userInfo?.userId) {
                        _isFromLocalSession.emit(true)
                        _visibleSession.emit(session)
                        loadFriendRequests(session)
                    }
                }
            }
        }
    }

    fun checkGamesAvailability() = backGroundScope.launch {
        repo.isThisGameModeAvailable("hasPlayedQuizGame").collectLatest {
            _hasUserPlayedLocally.emit(it)
        }
    }

    private fun loadFriendRequests(session: Session) = backGroundScope.launch {
        autoCancellable {
            repo.loadFriendRequests(session.friendRequests, session.friendList).collectLatest {
                it.handleSuccessAndFailure { (requests, friends) ->
                    _listOfFriendRequests.update {
                        requests
                    }
                    _listOfFriends.update {
                        friends
                    }
                }
            }
        }
    }

    fun refresh() = backGroundScope.launch {
        _isRefreshing.emit(true)
        checkGamesAvailability()
        collectDay(onlyOnce = false)
        delay(1000)
        _isRefreshing.emit(false)
    }

    private fun listenToBibleVerseUpdate(day: Int) = backGroundScope.launch {
        repo.getDailyBibleVerse(day).collectLatest {
            it.handleSuccessAndFailure { bibleVerse ->
                _dailyBibleVerse.emit(bibleVerse)
            }
        }
    }

    private fun resetState() = backGroundScope.launch {
        cancelSubscriptions()
        _visibleSession.update {
            Session()
        }
        _localSession.update {
            Session()
        }
        _listOfFriendRequests.update {
            listOf()
        }
        _listOfFriends.update {
            listOf()
        }
    }

    private fun cancelSubscriptions() {
        loginSession?.cancel()
        cancelCollectSession()
    }

    fun signInSomething(intent: Intent?) {
        loginSession = backGroundScope.launch {
            repo.getSession(intent).collectLatest {
                it.handleSuccessAndFailure { session ->
                    _localSession.emit(session)
                }
            }
        }
    }

    fun signIn(launcher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>) =
        viewModelScope.launch {
            repo.signIn(launcher)
        }

    fun signOut() = backGroundScope.launch {
        repo.signOut()
        resetState()
    }

    fun addFriend(userId: String) = backGroundScope.launch {
        autoCancellable {
            repo.sendFriendRequestV2(localSession.value, userId).collectLatest {
                it.handleSuccessAndFailure { feedbackMessage ->
                    val checkIfUserIsAddingFromFriendsProfile = visibleSession.value.userInfo?.userId != localSession.value.userInfo?.userId
                    if (feedbackMessage == FeedbackMessage.FriendRequestSent || checkIfUserIsAddingFromFriendsProfile) {
                        emitFeedbackMessage(feedbackMessage = feedbackMessage, isAutoDelete = true)
                        displayDialog(displayIt = false)
                    } else {
                        emitFeedbackMessage(feedbackMessage = feedbackMessage, isAutoDelete = false)
                    }
                }
            }
        }
    }

    fun updateFriendRequest(hasAccepted: Boolean, userId: String?) = backGroundScope.launch {
        autoCancellable {
            userId?.apply {
                repo.updateFriendRequest(localSession.value, hasAccepted, this).collectLatest {
                    it.handleSuccessAndFailure {

                    }
                }
            }
        }
    }

    fun updateVisibleSession(session: Session?) = backGroundScope.launch {
        _transitionAnimation.emit(true)
        delay(300)
        val userSelectedHimself = session?.userInfo?.userId == localSession.value.userInfo?.userId

        if (session == null || userSelectedHimself) {
            _isFromLocalSession.emit(true)
            _visibleSession.emit(value = localSession.value)
            loadFriendRequests(session = localSession.value)
        } else {
            _isFromLocalSession.emit(false)
            _visibleSession.emit(session)
            _notFriends.emit(
                checkIfSessionIsNotFriendsWithLocal(session) && checkIfSessionDoesntAlreadyHaveAFriendRequest(
                    session
                )
            )

            loadFriendRequests(session = session)
        }
    }

    private fun checkIfSessionDoesntAlreadyHaveAFriendRequest(session: Session): Boolean =
        !localSession.value.friendRequests.contains(session.userInfo?.userId)

    private fun checkIfSessionIsNotFriendsWithLocal(session: Session): Boolean =
        !localSession.value.friendList.contains(session.userInfo?.userId)


    fun finishTransitionAnimation() = backGroundScope.launch {
        _transitionAnimation.emit(false)
    }

    fun removeFriend() = backGroundScope.launch {
        autoCancellable {
            visibleSession.value.userInfo?.userId?.apply {
                repo.removeFriend(session = localSession.value, friendId = this).collectLatest {
                    it.handleSuccessAndFailure { feedbackMessage ->
                        _transitionAnimation.emit(true)
                        displayDialog(displayIt = false)
                        emitFeedbackMessage(feedbackMessage)
                        _visibleSession.emit(localSession.value)
                    }
                }
            }
        }
    }

    fun updateClickable(route: Routes) = mainScope.launch {
        _clickable.emit(false)
        _navigate.send(route)
        delay(1000)
        _clickable.emit(true)
    }
}