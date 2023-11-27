package com.example.novagincanabiblica.client

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.util.Log
import com.example.novagincanabiblica.R
import com.example.novagincanabiblica.data.models.Session
import com.example.novagincanabiblica.data.models.UserData
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import java.util.concurrent.CancellationException

class GoogleAuthUiClient(
    private val context: Context,
    private val oneTapClient: SignInClient
) {

    private val auth = Firebase.auth

    suspend fun signIn(): IntentSender? {
        val result = try {
            oneTapClient.beginSignIn(
                buildSignInRequest()
            ).await()
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            null
        }
        return result?.pendingIntent?.intentSender
    }

    suspend fun signInWithIntent(intent: Intent): Session {
        val credential = oneTapClient.getSignInCredentialFromIntent(intent)
        val googleIdToken = credential.googleIdToken
        val googleCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
        return try {
            val user = auth.signInWithCredential(googleCredential).await().user
            if (user != null) {
                Session(
                    userInfo = user.run {
                        if (displayName!=null && photoUrl!=null) {
                            UserData(
                                userId = uid,
                                userName = displayName!!,
                                profilePictureUrl = photoUrl!!.toString()
                            )
                        } else {
                            UserData()
                        }
                    }
                )
            } else {
                Session()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            Session()
        }
    }

    fun signOut() {
        try {
            auth.signOut()
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
        }
    }

    fun getSignerUserId(): String {
        if (auth.currentUser != null) {
            auth.currentUser?.run {
                return uid
            }
        }
        return ""
    }

    private fun buildSignInRequest(): BeginSignInRequest {
        return BeginSignInRequest.Builder().setGoogleIdTokenRequestOptions(
            GoogleIdTokenRequestOptions.builder().setSupported(true)
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(context.getString(R.string.google_web_client_id)).build()
        ).setAutoSelectEnabled(true).build()
    }
}