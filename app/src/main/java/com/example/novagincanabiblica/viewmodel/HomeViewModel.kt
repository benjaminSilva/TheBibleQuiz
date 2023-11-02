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
) : BaseViewModel() {

    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    private val _signInResult = MutableStateFlow(Session())
    val signInResult = _signInResult.asStateFlow()

    private val _localSession = MutableStateFlow(Session())
    val localSession = _localSession.asStateFlow()

    private val _dailyBibleVerse = MutableStateFlow(BibleVerse())
    val dailyBibleVerse = _dailyBibleVerse.asStateFlow()

    init {
        isUserSignedIn()
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

    private fun isUserSignedIn() = viewModelScope.launch {
        val signedUser = repo.getSignedInUser()
        if (signedUser != null) {
            _signInResult.update {
                it.copy(userInfo = signedUser, errorMessage = null)
            }
            _state.update {
                it.copy(isSignInSuccessful = true)
            }
        }
    }


    private fun onSignInResult(result: Session) {
        _state.update {
            it.copy(isSignInSuccessful = result.userInfo != null, signInError = result.errorMessage)
        }
        if (result.userInfo != null) {
            _signInResult.value = result
        }
    }

    private fun resetState() {
        _state.update {
            SignInState()
        }
        _signInResult.update {
            Session()
        }
    }

    fun signInSomething(result: ActivityResult) = viewModelScope.launch {
        onSignInResult(repo.getSession(result))
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