package com.example.novagincanabiblica.viewmodel

import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.novagincanabiblica.client.GoogleAuthUiClient
import com.example.novagincanabiblica.data.models.Session
import com.example.novagincanabiblica.data.models.SessionCache
import com.example.novagincanabiblica.data.models.SignInState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val googleAuthUiClient: GoogleAuthUiClient,
    private val session: SessionCache
) : ViewModel() {

    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    private val _signInResult = MutableStateFlow(Session())
    val signInResult = _signInResult.asStateFlow()

    private val _localSession = MutableStateFlow(Session())
    val localSession = _localSession.asStateFlow()


    init {
        isUserSignedIn()
    }

    fun updateSession() {
        session.getActiveSession()?.apply {
            _localSession.value = this
        }
    }

    private fun isUserSignedIn() {
        val signedUser = googleAuthUiClient.getSignerUser()
        if (signedUser != null) {
            if (session.getActiveSession() == null) {
                session.saveSession(Session(data = signedUser))
            }
            _signInResult.update {
                it.copy(data = signedUser, errorMessage = null)
            }
            _state.update {
                it.copy(isSignInSuccessful = true)
            }
        }
    }

    private fun onSignInResult(result: Session) {
        _state.update {
            it.copy(isSignInSuccessful = result.data != null, signInError = result.errorMessage)
        }
        if (result.data != null) {
            session.saveSession(_signInResult.value)
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
        val signInLocalResult = googleAuthUiClient.signInWithIntent(
            intent = result.data ?: return@launch
        )
        onSignInResult(signInLocalResult)
    }

    fun signIn(launcher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>) =
        viewModelScope.launch {
            val signInIntentSender = googleAuthUiClient.signIn()
            launcher.launch(
                IntentSenderRequest.Builder(
                    signInIntentSender ?: return@launch
                ).build()
            )
        }

    fun signOut() = viewModelScope.launch {
        googleAuthUiClient.signOut()
        session.clearSession()
        resetState()
    }

}