package com.bsoftwares.thebiblequiz.viewmodel

import android.content.Intent
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.bsoftwares.thebiblequiz.data.models.BibleVerse
import com.bsoftwares.thebiblequiz.data.models.League
import com.bsoftwares.thebiblequiz.data.models.LeagueRule
import com.bsoftwares.thebiblequiz.data.models.Session
import com.bsoftwares.thebiblequiz.data.models.SessionInLeague
import com.bsoftwares.thebiblequiz.data.models.state.DialogType
import com.bsoftwares.thebiblequiz.data.models.state.FeedbackMessage
import com.bsoftwares.thebiblequiz.data.repositories.BaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: BaseRepository,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel(repo) {

    private var visibleLeagueId: String
        get() = savedStateHandle["league"] ?: ""
        set(value) {
            savedStateHandle["league"] = value
        }

    private val _dailyBibleVerse by lazy { MutableStateFlow(BibleVerse()) }
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

    private var leagueJob: Job? = null

    init {
        initHomeViewModel()
        initLocalSessionListener()
    }

    private fun initLocalSessionListener() = backGroundScope.launch {
        localSession.collectLatest { session ->
            if (session.userInfo.userId.isNotEmpty()) {
                loadPremiumStatus()
                val currentUserId = visibleSession.value.userInfo.userId
                if ((currentUserId.isEmpty() || currentUserId == session.userInfo.userId)) {
                    _visibleSession.emit(session)
                    _isFromLocalSession.emit(true)
                    loadFriendRequests(session)
                    loadLeagues()
                }
            }
        }
    }

    private fun initHomeViewModel() = backGroundScope.launch {
        day.collectLatest {
            if (it != -1) {
                listenToBibleVerseUpdate(it)
            }
        }
    }

    private fun loadPremiumStatus() = backGroundScope.launch {
        repo.getUserPremiumStatus().collectLatest { result ->
            result.handleSuccessAndFailure { isSubscribed ->
                if (isSubscribed != localSession.value.premium) {
                    repo.updateUserPremiumStatus(session = localSession.value, isSubscribed)
                        .collectLatest {
                            emitFeedbackMessage(it)
                        }
                }
            }
        }
    }

    private fun loadLeagues(currentLeagueId: String = "") = backGroundScope.launch {
        repo.loadLeagues(localSession.value).collectLatestAndApplyOnMain {
            it.handleSuccessAndFailure { (listOfLeagueInvitation, listOfLeagues) ->
                _listOfLeagueInvitation.emit(listOfLeagueInvitation)
                _listOfLeagues.emit(listOfLeagues)
                listOfLeagues.firstOrNull { league -> league.leagueId == visibleLeagueId }?.apply {
                    setCurrentLeague(this)
                }
                if (currentLeagueId.isNotEmpty()) {
                    val league = listOfLeagues.find { league ->
                        league.leagueId == currentLeagueId
                    }
                    league?.apply {
                        updateDialog()
                        _currentLeague.emit(this)
                        loadLeagueUsers(this)
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
        _visibleSession.update {
            Session()
        }
        _listOfFriendRequests.update {
            listOf()
        }
        _listOfFriends.update {
            listOf()
        }
    }

    fun signInSomething(intent: Intent?) {
        backGroundScope.launch {
            repo.signIn(intent)
        }
    }

    fun signIn(launcher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>) =
        viewModelScope.launch {
            repo.signIn(launcher)
        }

    fun signOut() = backGroundScope.launch {
        updateDialog(DialogType.Loading)
        repo.signOut().collectLatest {
            it.handleSuccessAndFailure(failureAction = {
                updateDialog()
            }) {
                resetState()
            }
        }
    }

    fun addFriend(userId: String) = backGroundScope.launch {
        repo.sendFriendRequestV2(localSession.value, userId).collectLatest {
            it.handleSuccessAndFailure { feedbackMessage ->
                val checkIfUserIsAddingFromFriendsProfile =
                    visibleSession.value.userInfo.userId != localSession.value.userInfo.userId
                if (feedbackMessage == FeedbackMessage.FriendRequestSent || checkIfUserIsAddingFromFriendsProfile) {
                    emitFeedbackMessage(feedbackMessage = feedbackMessage)
                } else {
                    emitFeedbackMessage(
                        feedbackMessage = feedbackMessage,
                        isAutoDelete = false
                    )
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
        val userSelectedHimself =
            session?.userInfo?.userId == localSession.value.userInfo.userId

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
        _transitionAnimation.emit(true)
        delay(300)
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

    fun updateClickable() = mainScope.launch {
        _clickable.emit(false)
        delay(1000)
        _clickable.emit(true)
    }

    fun createNewLeague() = backGroundScope.launch {
        if (!localSession.value.premium && localSession.value.localListLeagues.isNotEmpty()) {
            emitFeedbackMessage(FeedbackMessage.YouAreNotPremium)
            return@launch
        }
        updateDialog(DialogType.Loading)
        repo.createNewLeague(localSession.value).collectLatestAndApplyOnMain {
            it.handleSuccessAndFailure { league ->
                emitFeedbackMessage(
                    feedbackMessage = FeedbackMessage.LeagueCreated,
                    isFastDelete = true
                )
                setCurrentLeague(league)
            }
            updateDialog(DialogType.EmptyValue)
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

    fun setCurrentLeague(league: League) {
        leagueJob?.cancel()
        leagueJob = backGroundScope.launch {
            repo.observeThisLeague(league).collectLatest {
                it.handleSuccessAndFailure { league ->
                    _currentLeague.emit(league)
                    visibleLeagueId = league.leagueId
                    loadLeagueUsers(league)
                }
            }
        }
    }

    private fun getSessionOfCurrentUser(league: League): SessionInLeague = try {
        league.listOfUsers.first { it.userId == localSession.value.userInfo.userId }
    } catch (e: Exception) {
        SessionInLeague()
    }

    private fun loadFriendsNotInLeague() = backGroundScope.launch {
        _listOfFriendsNotInLeague.emit(listOfFriends.value.filter { currentLeague.value.listOfUsers.find { leagueUser -> leagueUser.userId == it.userInfo.userId } == null })
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
        if (hasAccepted && !localSession.value.premium && localSession.value.localListLeagues.isNotEmpty()) {
            emitFeedbackMessage(FeedbackMessage.YouAreNotPremium)
            return@launch
        }
        autoCancellable {
            repo.updateLeagueInvitation(hasAccepted, session = localSession.value, leagueId)
                .collectLatestAndApplyOnMain {

                }
        }
    }

    fun updateIsFromLeague() = backGroundScope.launch {
        _isFromLeague.emit(false)
    }

    fun updateLeague(league: League, justIcon: Boolean = false, updateTime: Boolean = false) =
        backGroundScope.launch {
            if (!justIcon && !updateTime) {
                emitFeedbackMessage(FeedbackMessage.NoChange)
                return@launch
            }
            updateDialog(DialogType.Loading)
            autoCancellable {
                repo.updateLeague(league = league, justIcon = justIcon, updateCycle = updateTime)
                    .collectLatest {
                        it.handleSuccessAndFailure { feedbackMessage ->
                            delay(2000)
                            loadLeagues(currentLeagueId = league.leagueId)
                            updateDialog(DialogType.EmptyValue)
                            emitFeedbackMessage(feedbackMessage)
                        }
                    }
            }
        }

    fun updateToPremium() = backGroundScope.launch {
        repo.updateUserPremiumStatus(localSession.value, true).collectLatest {
            emitFeedbackMessage(it)
        }
    }

    fun deleteLeague(league: League) = backGroundScope.launch {
        repo.deleteLeague(leagueId = league.leagueId).collectLatest {
            it.handleSuccessAndFailure { fbm ->
                updateDialog(DialogType.Loading)
                delay(10000)
                updateDialog(DialogType.EmptyValue)
                emitFeedbackMessage(fbm)
            }
        }
    }

    fun leaveLeague(user: SessionInLeague = sessionInLeague.value) = backGroundScope.launch {
        repo.userLeaveLeague(
            user,
            currentLeague.value.leagueId,
            isFromCurrentSession = user.userId == localSession.value.userInfo.userId
        ).collectLatest {
            it.handleSuccessAndFailure { fbm ->
                updateDialog(DialogType.Loading)
                if (fbm == FeedbackMessage.RemovedUserSuccessfully) {
                    emitFeedbackMessage(fbm)
                } else {
                    delay(10000)
                    emitFeedbackMessage(fbm)
                }
                updateDialog(DialogType.EmptyValue)
            }
        }
    }
}