package com.example.novagincanabiblica.viewmodel

import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.novagincanabiblica.client.GoogleAuthUiClient
import com.example.novagincanabiblica.data.models.SignInResult
import com.example.novagincanabiblica.data.models.SignInState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val googleAuthUiClient: GoogleAuthUiClient
) : ViewModel() {

    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    private val _signInResult = MutableStateFlow(SignInResult())
    val signInResult = _signInResult.asStateFlow()

    init {
        isUserSignedIn()
    }

    private fun isUserSignedIn() {
        val signedUser = googleAuthUiClient.getSignerUser()
        if (signedUser != null) {
            _signInResult.update {
                it.copy(data = signedUser, errorMessage = null)
            }
            _state.update {
                it.copy(isSignInSuccessful = true)
            }
        }
    }

    private fun onSignInResult(result: SignInResult) {
        _state.update {
            it.copy(isSignInSuccessful = result.data != null, signInError = result.errorMessage)
        }
        if (result.data != null) {
            _signInResult.value = result
        }
    }

    fun resetState() {
        _state.update {
            SignInState()
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

}