package com.example.novagincanabiblica.viewmodel

import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.viewModelScope
import com.example.novagincanabiblica.data.models.BibleVerse
import com.example.novagincanabiblica.data.models.Session
import com.example.novagincanabiblica.data.models.state.FeedbackMessage
import com.example.novagincanabiblica.data.repositories.Repository
import com.example.novagincanabiblica.ui.screens.ProfileDialogType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: Repository
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

    private val _displayDialog = MutableStateFlow(Pair(ProfileDialogType.IRRELEVANT, false))
    val displayDialog = _displayDialog.asStateFlow()

    private val _visibleSession = MutableStateFlow(Session())
    val visibleSession = _visibleSession.asStateFlow()

    private val _isFromLocalSession = MutableStateFlow(true)
    val isFromLocalSession = _isFromLocalSession.asStateFlow()

    private val _transitionAnimation = MutableStateFlow(false)
    val transitionAnimation = _transitionAnimation.asStateFlow()


    init {
        loadToken()
        initHomeViewModel()
    }

    private fun loadToken() = viewModelScope.launch {
        repo.loadToken()
    }

    private fun initHomeViewModel() = viewModelScope.launch {
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

    fun checkGamesAvailability() = viewModelScope.launch {
        repo.isThisGameModeAvailable("hasPlayedQuizGame").collectLatest {
            _hasUserPlayedLocally.emit(it)
        }
    }

    private fun loadFriendRequests(session: Session) = viewModelScope.launch {
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

    fun refresh() = viewModelScope.launch {
        _isRefreshing.emit(true)
        delay(1000)
        checkGamesAvailability()
        collectDay(onlyOnce = false)
        _isRefreshing.emit(false)
    }

    private fun listenToBibleVerseUpdate(day: Int) = viewModelScope.launch {
        repo.getDailyBibleVerse(day).collectLatest {
            it.handleSuccessAndFailure { bibleVerse ->
                _dailyBibleVerse.emit(bibleVerse)
            }
        }
    }

    private fun resetState() = viewModelScope.launch {
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

    fun signInSomething(result: ActivityResult) = viewModelScope.launch {
        repo.getSession(result).collectLatest {
            it.handleSuccessAndFailure { session ->
                _localSession.emit(session)
            }
        }
    }

    fun signIn(launcher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>) =
        viewModelScope.launch {
            repo.signIn(launcher)
        }

    fun signOut() = viewModelScope.launch {
        repo.signOut()
        resetState()
    }

    fun displayDialog(profileDialogType: ProfileDialogType, displayIt: Boolean) =
        viewModelScope.launch {
            _displayDialog.emit(Pair(profileDialogType, displayIt))
        }

    fun addFriend(userId: String) = viewModelScope.launch {
        repo.sendFriendRequestV2(localSession.value, userId).collectLatest {
            it.handleSuccessAndFailure { feedbackMessage ->
                if (feedbackMessage == FeedbackMessage.FriendRequestSent) {
                    emitFeedbackMessage(feedbackMessage = feedbackMessage, isAutoDelete = true)
                    displayDialog(ProfileDialogType.IRRELEVANT, false)
                } else {
                    emitFeedbackMessage(feedbackMessage = feedbackMessage, isAutoDelete = false)
                }
            }
        }
    }

    fun updateFriendRequest(hasAccepted: Boolean, userId: String?) = viewModelScope.launch {
        userId?.apply {
            repo.updateFriendRequest(localSession.value, hasAccepted, this).collectLatest {
                it.handleSuccessAndFailure {

                }
            }
        }
    }

    fun updateVisibleSession(session: Session?) = viewModelScope.launch {
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
            loadFriendRequests(session = session)
        }
    }

    fun finishTransitionAnimation() = viewModelScope.launch {
        _transitionAnimation.emit(false)
    }

    fun removeFriend() = viewModelScope.launch {
        visibleSession.value.userInfo?.userId?.apply {
            repo.removeFriend(session = localSession.value, friendId = this).collectLatest {
                it.handleSuccessAndFailure { feedbackMessage ->
                    _transitionAnimation.emit(true)
                    displayDialog(ProfileDialogType.IRRELEVANT, false)
                    emitFeedbackMessage(feedbackMessage)
                    _visibleSession.emit(localSession.value)
                }
            }
        }
    }

}