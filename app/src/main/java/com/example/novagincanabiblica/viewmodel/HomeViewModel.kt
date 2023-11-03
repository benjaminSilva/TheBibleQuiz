package com.example.novagincanabiblica.viewmodel

import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.viewModelScope
import com.example.novagincanabiblica.data.models.BibleVerse
import com.example.novagincanabiblica.data.models.Session
import com.example.novagincanabiblica.data.models.SignInState
import com.example.novagincanabiblica.data.repositories.SoloModeRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: SoloModeRepo
) : BaseViewModel(repo) {

    /*private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()*/

    /*private val _signInResult = MutableStateFlow(Session())
    val signInResult = _signInResult.asStateFlow()*/

    private val _dailyBibleVerse = MutableStateFlow(BibleVerse())
    val dailyBibleVerse = _dailyBibleVerse.asStateFlow()

    init {
        getDay()
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
            _localSession.emit(it)
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
        repo.getSession().collectLatest {

        }
        repo.getSession(result).collectLatest {
            it.handleSuccessAndFailure { session ->
                //onSignInResult(session)
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

}