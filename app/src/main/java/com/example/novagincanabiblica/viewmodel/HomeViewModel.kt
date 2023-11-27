package com.example.novagincanabiblica.viewmodel

import android.content.Intent
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.viewModelScope
import com.example.novagincanabiblica.data.models.BibleVerse
import com.example.novagincanabiblica.data.models.League
import com.example.novagincanabiblica.data.models.LeagueRule
import com.example.novagincanabiblica.data.models.Session
import com.example.novagincanabiblica.data.models.SessionInLeague
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

    private var _listOfFriendsNotInLeague = MutableStateFlow(listOf<Session>())
    val listOfFriendsNotInLeague = _listOfFriendsNotInLeague.asStateFlow()

    private val _visibleSession = MutableStateFlow(Session())
    val visibleSession = _visibleSession.asStateFlow()

    private val _isFromLocalSession = MutableStateFlow(true)
    val isFromLocalSession = _isFromLocalSession.asStateFlow()

    private val _transitionAnimation = MutableStateFlow(false)
    val transitionAnimation = _transitionAnimation.asStateFlow()

    private val _notFriends = MutableStateFlow(false)
    val notFriends = _notFriends.asStateFlow()

    private val _notFriendRequest = MutableStateFlow(false)
    val notFriendRequest = _notFriendRequest.asStateFlow()

    private val _clickable = MutableStateFlow(true)
    val clickable = _clickable.asStateFlow()

    private val _navigate = Channel<Routes>()
    val navigate = _navigate.receiveAsFlow()

    private val _currentLeague = MutableStateFlow(League())
    val currentLeague = _currentLeague.asStateFlow()

    private val _listOfLeagues = MutableStateFlow(listOf<League>())
    val listOfLeague = _listOfLeagues.asStateFlow()

    private val _listOfLeagueInvitation = MutableStateFlow(listOf<League>())
    val listOfLeagueInvitation = _listOfLeagueInvitation.asStateFlow()

    private val _isFromLeague = MutableStateFlow(false)
    val isFromLeague = _isFromLeague.asStateFlow()

    private val _sessionInLeague = MutableStateFlow(SessionInLeague())
    val sessionInLeague = _sessionInLeague.asStateFlow()

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
                localSession.collectLatestAndApplyOnMain { session ->
                    val currentUserId = visibleSession.value.userInfo.userId
                    if ((currentUserId.isEmpty() || currentUserId == session.userInfo.userId) && session.userInfo.userId.isNotEmpty()) {
                        _visibleSession.emit(session)
                        _isFromLocalSession.emit(true)
                        loadFriendRequests(session)
                        loadLeagues(session)
                    }
                }
            }
        }
    }

    private fun loadLeagues(session: Session) = backGroundScope.launch {
        Log.i("League Test", "Leagues Loaded Function Called")
        autoCancellable {
            repo.loadLeagues(session).collectLatestAndApplyOnMain {
                it.handleSuccessAndFailure { (listOfLeagueInvitation, listOfLeagues) ->
                    _listOfLeagues.emit(listOfLeagues)
                    _listOfLeagueInvitation.emit(listOfLeagueInvitation)
                    Log.i("League Test", "Leagues loaded: $listOfLeagues")
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
            repo.loadFriendRequests(session.localFriendRequestList, session.localFriendList)
                .collectLatest {
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
        updateSession(Session())
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
                    updateSession(session)
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
                    val checkIfUserIsAddingFromFriendsProfile =
                        visibleSession.value.userInfo.userId != localSession.value.userInfo.userId
                    if (feedbackMessage == FeedbackMessage.FriendRequestSent || checkIfUserIsAddingFromFriendsProfile) {
                        emitFeedbackMessage(feedbackMessage = feedbackMessage)
                        updateDialog()
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
        val userSelectedHimself = session?.userInfo?.userId == localSession.value.userInfo.userId

        if (session == null || userSelectedHimself) {
            emitCurrentSession()
        } else {
            _isFromLocalSession.emit(false)
            _visibleSession.emit(session)
            _notFriends.emit(
                checkIfSessionIsNotFriendsWithLocal(session) && checkIfSessionDoesntAlreadyHaveAFriendRequest(
                    session
                )
            )
            _notFriendRequest.emit(
                checkIfSessionDoesntAlreadyHaveAFriendRequest(session)
            )
            loadFriendRequests(session = session)
        }
    }

    fun updateVisibleSession(userId: String) = backGroundScope.launch {
        val userSelectedHimself = userId == localSession.value.userInfo.userId

        if (userSelectedHimself) {
            emitCurrentSession()
        } else {
            repo.getSession(userId).collectLatestAndApplyOnMain {
                it.handleSuccessAndFailure { session ->
                    if (session.userInfo.userId == "") {
                        emitCurrentSession()
                    } else {
                        _visibleSession.emit(session)
                        _isFromLocalSession.emit(false)
                        _notFriends.emit(
                            checkIfSessionIsNotFriendsWithLocal(session) && checkIfSessionDoesntAlreadyHaveAFriendRequest(
                                session
                            )
                        )
                        _notFriendRequest.emit(
                            checkIfSessionDoesntAlreadyHaveAFriendRequest(session)
                        )
                        loadFriendRequests(session = session)
                    }
                }
            }

        }
    }

    private fun emitCurrentSession() = backGroundScope.launch {
        _isFromLocalSession.emit(true)
        _isFromLeague.emit(false)
        _visibleSession.emit(value = localSession.value)
        loadFriendRequests(session = localSession.value)
    }

    private fun checkIfSessionDoesntAlreadyHaveAFriendRequest(session: Session): Boolean =
        !localSession.value.localFriendRequestList.contains(session.userInfo.userId)

    private fun checkIfSessionIsNotFriendsWithLocal(session: Session): Boolean =
        !localSession.value.localFriendList.contains(session.userInfo.userId)


    fun finishTransitionAnimation() = backGroundScope.launch {
        _transitionAnimation.emit(false)
    }

    fun removeFriend() = backGroundScope.launch {
        autoCancellable {
            visibleSession.value.userInfo.userId.apply {
                repo.removeFriend(session = localSession.value, friendId = this).collectLatest {
                    it.handleSuccessAndFailure { feedbackMessage ->
                        _transitionAnimation.emit(true)
                        updateDialog()
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

    fun createNewLeague() = backGroundScope.launch {
        autoCancellable {
            repo.createNewLeague(localSession.value).collectLatestAndApplyOnMain {
                it.handleSuccessAndFailure { league ->
                    emitFeedbackMessage(feedbackMessage = FeedbackMessage.LeagueCreated, isFastDelete = true)
                    _currentLeague.emit(league)
                    loadLeagueUsers(league)
                }
            }
        }
    }

    private fun loadLeagueUsers(league: League) = backGroundScope.launch {
        autoCancellable {
            repo.loadLeagueUsers(league).collectLatestAndApplyOnMain { resultOf ->
                resultOf.handleSuccessAndFailure { league ->
                    _currentLeague.emit(league.copy(listOfUsers = league.listOfUsers.sortedByDescending {
                        when (league.leagueRule) {
                            LeagueRule.QUIZ_AND_WORDLE -> it.totalPoints
                            LeagueRule.QUIZ_ONLY -> it.pointsForQuiz
                            LeagueRule.WORDLE_ONLY -> it.pointsForWordle
                        }
                    }))
                    loadFriendsNotInLeague()
                    _isFromLeague.emit(true)
                    _sessionInLeague.emit(getSessionOfCurrentUser(league))
                }
            }
        }
    }

    fun setCurrentLeague(it: League) = backGroundScope.launch {
        _currentLeague.emit(it)
        loadLeagueUsers(it)
    }

    private fun getSessionOfCurrentUser(league: League): SessionInLeague =
        league.listOfUsers.first { it.userId == localSession.value.userInfo.userId }

    private fun loadFriendsNotInLeague() = backGroundScope.launch {
        _listOfFriendsNotInLeague.emit(listOfFriends.value.filter { currentLeague.value.listOfUsers.find { leagueUser -> leagueUser.userId == it.userInfo.userId } != null })
    }

    fun sendLeagueRequest(list: List<Session>) = backGroundScope.launch {
        if (list.isNotEmpty()) {
            autoCancellable {
                repo.sendLeagueRequest(list, currentLeague.value).collectLatestAndApplyOnMain {

                }
            }
        }
    }

    fun updateLeagueInvitation(hasAccepted: Boolean, leagueId: String) = backGroundScope.launch {
        autoCancellable {
            repo.updateLeagueInvitation(hasAccepted, session = localSession.value, leagueId)
                .collectLatestAndApplyOnMain {

                }
        }
    }

    fun updateIsFromLeague() = backGroundScope.launch {
        _isFromLeague.emit(false)
    }
}