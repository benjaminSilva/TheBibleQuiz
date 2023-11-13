package com.example.novagincanabiblica.viewmodel

import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.viewModelScope
import com.example.novagincanabiblica.data.models.BibleVerse
import com.example.novagincanabiblica.data.models.Session
import com.example.novagincanabiblica.data.repositories.Repository
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

    private val _displayDialog = MutableStateFlow(Pair(false, false))
    val displayDialog = _displayDialog.asStateFlow()

    init {
        getDay()
    }

    fun checkGamesAvailability() = viewModelScope.launch {
        repo.isThisGameModeAvailable("hasPlayedQuizGame").collectLatest {
            _hasUserPlayedLocally.emit(it)
        }
    }

    fun refresh() = viewModelScope.launch {
        _isRefreshing.emit(true)
        delay(1000)
        checkGamesAvailability()
        getDay()
        _isRefreshing.emit(false)
    }

    private fun getDay() = viewModelScope.launch {
        repo.getDay().collectLatest { day ->
            loadSession()

            day.handleSuccessAndFailure {
                listenToBibleVerseUpdate(it)
            }
        }
    }

    private fun loadSession() = viewModelScope.launch {
        repo.getSession().collectLatest {
            it.handleSuccessAndFailure { session ->
                _localSession.emit(session)
            }
        }
    }

    private fun listenToBibleVerseUpdate(day: Int) = viewModelScope.launch {
        repo.getDailyBibleVerse(day).collectLatest {
            it.handleSuccessAndFailure { bibleVerse ->
                _dailyBibleVerse.emit(bibleVerse)
            }
        }
    }

    private fun resetState() {
        _localSession.update {
            Session()
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

    fun displayDialog(isItQuiz: Boolean, displayIt: Boolean) = viewModelScope.launch {
        _displayDialog.emit(Pair(isItQuiz, displayIt))
    }

}