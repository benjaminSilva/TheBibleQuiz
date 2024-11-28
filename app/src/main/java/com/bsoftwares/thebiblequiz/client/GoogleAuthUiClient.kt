package com.bsoftwares.thebiblequiz.client

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.util.Log
import com.bsoftwares.thebiblequiz.R
import com.bsoftwares.thebiblequiz.data.models.Session
import com.bsoftwares.thebiblequiz.data.models.UserData
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.logInWith
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CancellationException
import kotlin.Exception

class GoogleAuthUiClient(
    private val context: Context,
    private val oneTapClient: SignInClient
) {

    companion object {
        private const val TAG = "Auth Listener"
    }

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

    val authStateFlow: Flow<Session> = callbackFlow {
        Log.d(TAG, "authStateFlow started")

        val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                Log.d(
                    TAG,
                    "User signed in: userId=${user.uid}, userName=${user.displayName}, profilePictureUrl=${user.photoUrl?.toString()}"
                )
                trySend(
                    Session(
                        userInfo = UserData(
                            userId = user.uid,
                            userName = user.displayName.orEmpty(),
                            profilePictureUrl = user.photoUrl?.toString().orEmpty()
                        )
                    )
                ).onFailure {
                    Log.e(TAG, "Failed to send signed-in user session: $it")
                }
            } else {
                Log.d(TAG, "User signed out")
                trySend(Session()).onFailure {
                    Log.e(TAG, "Failed to send signed-out session: $it")
                }
            }
        }

        auth.addAuthStateListener(authStateListener)
        Log.d(TAG, "AuthStateListener added")

        awaitClose {
            Log.d(TAG, "authStateFlow closing, removing AuthStateListener")
            auth.removeAuthStateListener(authStateListener)
        }
    }

    suspend fun signInWithIntent(intent: Intent) {
        val credential = oneTapClient.getSignInCredentialFromIntent(intent)
        val googleIdToken = credential.googleIdToken
        val googleCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
        try {
            val user = auth.signInWithCredential(googleCredential).await().user
            if (user != null) {
                Purchases.sharedInstance.logInWith(
                    appUserID = user.uid,
                    onSuccess = { _, _ -> }
                )
            } else {
                throw NullUser("User is null")
            }
        } catch (e: Exception) {
            Log.d(TAG, e.message.toString())
            throw e
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

    fun getSignerUserId(): String = auth.currentUser?.uid ?: ""

    private fun buildSignInRequest(): BeginSignInRequest {
        return BeginSignInRequest.Builder().setGoogleIdTokenRequestOptions(
            GoogleIdTokenRequestOptions.builder().setSupported(true)
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(context.getString(R.string.google_web_client_id)).build()
        ).setAutoSelectEnabled(true).build()
    }

    class NullUser(message: String) : Exception(message)
}