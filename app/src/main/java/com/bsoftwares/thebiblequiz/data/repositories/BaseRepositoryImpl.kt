package com.bsoftwares.thebiblequiz.data.repositories

import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.Network
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.compose.ui.text.intl.Locale
import com.bsoftwares.thebiblequiz.client.GoogleAuthUiClient
import com.bsoftwares.thebiblequiz.data.models.BibleVerse
import com.bsoftwares.thebiblequiz.data.models.LeaderboardType
import com.bsoftwares.thebiblequiz.data.models.League
import com.bsoftwares.thebiblequiz.data.models.RankingData
import com.bsoftwares.thebiblequiz.data.models.Session
import com.bsoftwares.thebiblequiz.data.models.SessionInLeague
import com.bsoftwares.thebiblequiz.data.models.UserTitle
import com.bsoftwares.thebiblequiz.data.models.quiz.Question
import com.bsoftwares.thebiblequiz.data.models.quiz.QuestionDifficulty
import com.bsoftwares.thebiblequiz.data.models.state.ConnectivityStatus
import com.bsoftwares.thebiblequiz.data.models.state.FeedbackMessage
import com.bsoftwares.thebiblequiz.data.models.state.LogTypes
import com.bsoftwares.thebiblequiz.data.models.state.ResultOf
import com.bsoftwares.thebiblequiz.data.models.toRankedData
import com.bsoftwares.thebiblequiz.data.models.wordle.Wordle
import com.bsoftwares.thebiblequiz.data.models.wordle.WordleAttempt
import com.bsoftwares.thebiblequiz.data.models.wordle.WordleAttemptState
import com.bsoftwares.thebiblequiz.ui.theme.emptyString
import com.google.firebase.BuildConfig
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.messaging.FirebaseMessaging
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesError
import com.revenuecat.purchases.interfaces.ReceiveCustomerInfoCallback
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.UUID
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


class BaseRepositoryImpl @Inject constructor(
    private val googleAuthUiClient: GoogleAuthUiClient,
    private val sharedPreferences: SharedPreferences,
    baseDatabase: FirebaseDatabase,
    usersDatabase: FirebaseDatabase,
    dailyVerseDatabase: FirebaseDatabase,
    wordleDatabase: FirebaseDatabase,
    quizDatabase: FirebaseDatabase,
    englishWords: FirebaseDatabase,
    portugueseWords: FirebaseDatabase,
    suggestedQuestionsDatabase: FirebaseDatabase,
    leaguesDatabase: FirebaseDatabase,
    val firebaseMessaging: FirebaseMessaging,
    val connectivityManager: ConnectivityManager
) : BaseRepository {

    private val firebaseRef = baseDatabase.reference
    private val usersReference = usersDatabase.reference
    private val bibleVerseReference = dailyVerseDatabase.reference
    private val wordleReference = wordleDatabase.reference
    private val quizReference = quizDatabase.reference
    private val englishWordsReference = englishWords.reference
    private val portugueseWordsReference = portugueseWords.reference
    private val suggestedQuestionsReference = suggestedQuestionsDatabase.reference
    private val leaguesDatabaseReference = leaguesDatabase.reference
    private var globalToken = ""

    private val stringLeagueInvitation = "leagueInvitation"
    private val stringLeagues = "leagues"
    private val stringLeagueUsers = "leagueUsers"
    private val stringPointsForQuiz = "pointsForQuiz"
    private val stringPointsForWordle = "pointsForWordle"
    private val stringFcmToken = "fcmToken"
    private val hasPlayedWordleGame = "hasPlayedWordleGame"
    private val hasPlayedQuizGame = "hasPlayedQuizGame"
    private val quizStats = "quizStats"
    private val premium = "premium"
    private val friendList = "friendList"
    private val friendListRequest = "friendRequests"
    private val listOfAttempts = "listOfAttempts"
    private val wordlePath = "wordle"
    private val wordleStatsPath = "wordleStats"
    private val day = "day"
    val String.path: String
        get() = if (BuildConfig.DEBUG) "test/$this" else this

    private val TAG = "SessionChangesListener"


    override suspend fun loadDailyQuestion(day: Int): Flow<ResultOf<Question>> = callbackFlow {
        val ref = quizReference.child(Locale.current.language.path).child("day$day")
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.getValue<Question>()?.apply {
                    trySend(ResultOf.Success(this))
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                trySend(ResultOf.Failure(FeedbackMessage.InternetIssues))
            }
        }
        ref.addListenerForSingleValueEvent(postListener)
        awaitClose { ref.removeEventListener(postListener) }
    }

    override suspend fun updateStats(
        currentQuestion: Question,
        isCorrect: Boolean,
        session: Session,
        answerSelectedByTheUser: String
    ): Flow<ResultOf<Nothing>> =
        channelFlow {

            if (session.userInfo.userId.isNotEmpty()) {
                var pointsToUpdateLeagues = 0

                val currentUserStats = session.quizStats.apply {
                    answerSelected = answerSelectedByTheUser
                    if (isCorrect) {
                        streak += 1
                        when (currentQuestion.difficulty) {
                            QuestionDifficulty.EASY -> {
                                pointsToUpdateLeagues += 1
                                easyCorrect += 1
                            }

                            QuestionDifficulty.MEDIUM -> {
                                pointsToUpdateLeagues += 2
                                mediumCorrect += 1
                            }

                            QuestionDifficulty.HARD -> {
                                pointsToUpdateLeagues += 3
                                hardCorrect += 1
                            }

                            QuestionDifficulty.IMPOSSIBLE -> {
                                pointsToUpdateLeagues += 5
                                impossibleCorrect += 1
                            }
                        }
                    } else {
                        streak = 0
                        when (currentQuestion.difficulty) {
                            QuestionDifficulty.EASY -> easyWrong += 1
                            QuestionDifficulty.MEDIUM -> {
                                pointsToUpdateLeagues -= 1
                                mediumWrong += 1
                            }

                            QuestionDifficulty.HARD -> {
                                pointsToUpdateLeagues -= 2
                                hardWrong += 1
                            }

                            QuestionDifficulty.IMPOSSIBLE -> {
                                pointsToUpdateLeagues -= 3
                                impossibleWrong += 1
                            }
                        }
                    }
                    quizTotalPointsAllTime += pointsToUpdateLeagues
                    quizTotalPointsForTheMonths += pointsToUpdateLeagues
                }

                try {
                    if (session.localListLeagues.isNotEmpty() && pointsToUpdateLeagues != 0) {
                        session.localListLeagues.forEach {
                            val pointsRef =
                                leaguesDatabaseReference.child(it.path).child(stringLeagueUsers)
                                    .child(session.userInfo.userId).child(stringPointsForQuiz)
                            val oldPoints = pointsRef.get().addOnFailureListener { error ->
                                throw error
                            }.await().value as Long

                            pointsRef.setValue(oldPoints + pointsToUpdateLeagues)
                                .addOnFailureListener { error ->
                                    throw error
                                }.await()
                        }
                    }
                    val newLeaderboardData = mapOf(
                        "leaderboardTotalAllTime" to session.leaderboardTotalAllTime + pointsToUpdateLeagues,
                        "leaderboardTotalMonthly" to session.leaderboardTotalMonthly + pointsToUpdateLeagues
                    )
                    usersReference.child(session.userInfo.userId.path).child(quizStats)
                        .setValue(currentUserStats)
                        .addOnFailureListener {
                            throw it
                        }.await()
                    usersReference.child(session.userInfo.userId.path).updateChildren(newLeaderboardData)
                        .addOnFailureListener {
                            throw it
                        }.await()
                } catch (exception: Exception) {
                    trySend(
                        ResultOf.LogMessage(
                            LogTypes.FIREBASE_ERROR,
                            exception.message.toString()
                        )
                    )
                }
            }
        }

    override suspend fun loadLeaderboards(leaderboardType: LeaderboardType): Flow<ResultOf<List<RankingData>>> =
        channelFlow {
            usersReference.orderByChild(leaderboardType.path.path).limitToLast(30)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val topList = mutableListOf<Session>()
                        val childrenReversed = snapshot.children.reversed()
                        for (childSnapshot in childrenReversed) {
                            val session = childSnapshot.getValue(Session::class.java)
                            if (session != null) {
                                topList.add(session)
                            }
                        }
                        trySend(ResultOf.Success(topList.toRankedData(leaderboardType)))
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })

            awaitClose {}
        }

    override fun updateGameModeValue(key: String, value: Boolean) {
        sharedPreferences.edit().apply {
            putBoolean(key, value)
        }.apply()
    }

    override suspend fun isThisGameModeAvailable(key: String): Flow<Boolean> = flow {
        emit(sharedPreferences.getBoolean(key, false))
    }

    override suspend fun signIn(launcher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>) =
        flow {
            val signInIntentSender = googleAuthUiClient.signIn()
            if (signInIntentSender == null) {
                emit(FeedbackMessage.NoGoogleAccountFoundOnDevice)
            } else {
                launcher.launch(
                    IntentSenderRequest.Builder(
                        signInIntentSender
                    ).build()
                )
            }
        }

    override suspend fun signOut(): Flow<ResultOf<FeedbackMessage>> = channelFlow {
        try {
            usersReference.child(getSignedInUserId().path).child(stringFcmToken).setValue("").await()
            googleAuthUiClient.signOut()
            trySend(ResultOf.Success(FeedbackMessage.NoMessage))
        } catch (e: Exception) {
            trySend(ResultOf.LogMessage(LogTypes.FIREBASE_ERROR, e.message.toString()))
        }
    }

    override suspend fun updateUserPremiumStatus(
        session: Session,
        newValue: Boolean
    ): Flow<FeedbackMessage> = channelFlow {
        usersReference.child(session.userInfo.userId.path).child(premium).setValue(newValue)
            .addOnSuccessListener {
                if (newValue) {
                    trySend(FeedbackMessage.YouAreNowPremium)
                }
            }.addOnFailureListener {
                it.message?.apply {
                    trySend(FeedbackMessage.Error(this))
                }
            }
        awaitClose { channel.close() }
    }

    override suspend fun getSession(): Flow<ResultOf<Session>> = channelFlow {
        var handleSessionJob: Job? = null

        Log.d(TAG, "Log before the authStateFlow")

        googleAuthUiClient.authStateFlow.distinctUntilChangedBy { it.userInfo.userId }
            .collectLatest { session ->
                val userId = session.userInfo.userId
                if (userId.isEmpty()) { // No authenticated user
                    Log.d(TAG, "No authenticated user found; stopping any ongoing session handling")
                    handleSessionJob?.cancel() // Cancel the previous job if it's running
                    handleSessionJob = null
                    trySend(ResultOf.Success(session))
                } else {
                    Log.d(TAG, "getSession called for already logged-in user")
                    handleSessionJob?.cancel() // Cancel the previous job if it's running
                    handleSessionJob = launch {
                        handleSessionIfItExists(session).collectLatest {
                            trySend(it)
                        }
                    }
                }
            }
    }

    override suspend fun signIn(intent: Intent?) {
        if (intent != null) {
            // Handle the case where login just occurred
            googleAuthUiClient.signInWithIntent(intent = intent)
        }
    }

    private fun handleSessionIfItExists(session: Session): Flow<ResultOf<Session>> = channelFlow {
        val userId = session.userInfo.userId
        val result = createOrUpdateSession(session)

        if (result is ResultOf.Success) {
            listenToSessionChanges(userId).collectLatest { trySend(it) }
        } else {
            trySend(result)
        }
    }


    private fun createValueEventListener(
        onSuccess: (Session) -> Unit,
        onFailure: (DatabaseError) -> Unit
    ): ValueEventListener {
        return object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    dataSnapshot.getValue<Session>()?.withLoadedFriends(dataSnapshot)
                        ?.let(onSuccess)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                onFailure(databaseError)
            }
        }
    }

    private fun listenToSessionChanges(userId: String): Flow<ResultOf<Session>> = channelFlow {
        Log.d(TAG, "Coroutine started for userId: $userId")

        val userRef = usersReference.child(userId.path)

        val postListener = createValueEventListener(
            onSuccess = { session ->
                Log.d(TAG, "Data received for userId: $userId - Session: $session")
                trySend(ResultOf.Success(session))
            },
            onFailure = { databaseError ->
                Log.e(TAG, "Error received for userId: $userId - Error: ${databaseError.message}")
                trySend(ResultOf.Failure(FeedbackMessage.Error(databaseError.message)))
            }
        )

        userRef.addValueEventListener(postListener)

        awaitClose {
            Log.d(TAG, "Coroutine canceled for userId: $userId")
            userRef.removeEventListener(postListener)
        }
    }.onStart {
        Log.d(TAG, "Flow started for userId: $userId")
    }.onCompletion {
        Log.d(TAG, "Flow completed for userId: $userId")
    }

    // Function to create a new session if it doesn't exist
    private suspend fun createOrUpdateSession(session: Session): ResultOf<Session> {
        val userRef = usersReference.child(session.userInfo.userId.path)
        val existingSession = userRef.get().await().getValue<Session>()

        return if (existingSession == null) {
            // Create a new session if it doesn't exist
            userRef.setValue(
                session.copy(
                    fcmToken = globalToken,
                    language = Locale.current.language
                )
            ).await()
            ResultOf.Success(session)
        } else {
            // Update the language if it has changed
            if (existingSession.language != Locale.current.language) {
                userRef.child("language").setValue(Locale.current.language).await()
            }
            ResultOf.Success(existingSession)
        }
    }

    override suspend fun getSignedInUserId(): String = googleAuthUiClient.getSignerUserId()

    override suspend fun updateHasPlayedBibleQuiz() {
        usersReference.child(getSignedInUserId().path).child(hasPlayedQuizGame).setValue(true)
        updateGameModeValue(hasPlayedQuizGame, true)
    }

    override suspend fun deleteLeague(leagueId: String): Flow<ResultOf<FeedbackMessage>> =
        channelFlow {
            leaguesDatabaseReference.child(leagueId).ref.removeValue().addOnSuccessListener {
                trySend(ResultOf.Success(FeedbackMessage.LeagueDeleted))
            }.addOnFailureListener { message ->
                trySend(ResultOf.LogMessage(LogTypes.PERMISSION, message.message.toString()))
            }
            awaitClose { channel.close() }
        }

    override suspend fun observeThisLeague(currentLeague: League): Flow<ResultOf<League>> =
        callbackFlow {
            val ref = leaguesDatabaseReference.child(currentLeague.leagueId.path)
            val postListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dataSnapshot.getValue<League>()?.apply {
                        trySend(ResultOf.Success(this.run {
                            copy(endCycleString = transformEndCycleToString())
                        }))
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    trySend(ResultOf.Failure(FeedbackMessage.InternetIssues))
                }
            }
            ref.addValueEventListener(postListener)
            awaitClose { ref.removeEventListener(postListener) }
        }

    override suspend fun getUserPremiumStatus(): Flow<ResultOf<Boolean>> = callbackFlow {
        val userId = getSignedInUserId()
        Log.d("getUserPremiumStatus", "Initiating premium status check for userId: $userId")
        val instance = Purchases.sharedInstance

        instance.getCustomerInfo(
            callback = object : ReceiveCustomerInfoCallback {
                override fun onError(error: PurchasesError) {
                    Log.e(
                        "getUserPremiumStatus",
                        "Error retrieving customer info for userId: $userId. Error: ${error.message}"
                    )
                    trySend(ResultOf.Failure(FeedbackMessage.Error(error.message)))
                }

                override fun onReceived(customerInfo: CustomerInfo) {
                    val isActive = customerInfo.entitlements["Premium"]?.isActive ?: false
                    Log.d(
                        "getUserPremiumStatus",
                        "Customer info retrieved for userId: $userId. Is Premium Active: $isActive"
                    )
                    trySend(ResultOf.Success(isActive))
                }
            }
        )

        Log.d("getUserPremiumStatus", "Awaiting closure of premium status flow for userId: $userId")

        awaitClose {
            Log.d("getUserPremiumStatus", "Cleaning up resources for userId: $userId")
            instance.removeUpdatedCustomerInfoListener()
        }
    }

    override suspend fun getSession(userId: String): Flow<ResultOf<Session>> = flow {
        val sessionRef = usersReference.child(userId.path)
        val session = sessionRef.get().await().getValue<Session>()
        if (session != null) {
            emit(ResultOf.Success(session.withLoadedFriends(sessionRef.get().await())))
        } else {
            emit(ResultOf.Success(Session()))
        }
    }

    fun Session.withLoadedFriends(dataSnapshot: DataSnapshot): Session =
        copy(localFriendList = dataSnapshot.child(friendList).children.mapNotNull {
            it.key
        },
            localFriendRequestList = dataSnapshot.child(friendListRequest).children.mapNotNull {
                it.key
            },
            localListLeagues = dataSnapshot.child(stringLeagues).children.mapNotNull {
                it.key
            },
            localLeagueRequestList = dataSnapshot.child(stringLeagueInvitation).children.mapNotNull {
                it.key
            })


    override suspend fun getDay(): Flow<ResultOf<Pair<Int, Boolean>>> = callbackFlow {
        val ref = firebaseRef.child(day.path)
        val currentDay = sharedPreferences.getInt(day, 0)
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.getValue<Int>()?.apply {
                    val isNewDay = this != currentDay
                    if (isNewDay) {
                        sharedPreferences.edit().putInt(day, this).apply()
                    }
                    trySend(ResultOf.Success(this to isNewDay))
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                trySend(ResultOf.Failure(FeedbackMessage.InternetIssues))
            }
        }
        ref.addValueEventListener(postListener)
        awaitClose { ref.removeEventListener(postListener) }
    }.distinctUntilChanged()

    override suspend fun getDailyBibleVerse(day: Int): Flow<ResultOf<BibleVerse>> = callbackFlow {
        val ref = bibleVerseReference.child(Locale.current.language.path).child("day$day")
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.getValue<BibleVerse>()?.apply {
                    trySend(ResultOf.Success(this))
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                trySend(ResultOf.Failure(FeedbackMessage.InternetIssues))
            }
        }
        ref.limitToLast(2).addValueEventListener(postListener)
        awaitClose { ref.removeEventListener(postListener) }
    }

    override suspend fun getWordle(day: Int): Flow<ResultOf<Wordle>> = callbackFlow {
        val ref = wordleReference.child(Locale.current.language.path).child("day$day")
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.getValue<Wordle>()?.apply {
                    trySend(ResultOf.Success(this))
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                trySend(ResultOf.Failure(FeedbackMessage.InternetIssues))
            }
        }
        ref.addListenerForSingleValueEvent(postListener)
        awaitClose { ref.removeEventListener(postListener) }
    }

    override suspend fun updateWordleStats(
        userFoundTheWord: Boolean,
        session: Session,
        numberOfAttempt: List<WordleAttempt>
    ): Flow<FeedbackMessage> = channelFlow {
        updateGameModeValue(key = hasPlayedWordleGame, true)
        if (session.userInfo.userId.isNotEmpty()) {
            var pointsToUpdateLeagues = 0
            val getWhichTry =
                numberOfAttempt.indexOfLast { it.attemptState == WordleAttemptState.USER_HAS_TRIED }
            val currentUserStats = session.wordle.wordleStats.apply {
                if (userFoundTheWord) {
                    when (getWhichTry) {
                        0 -> {
                            pointsToUpdateLeagues = 6
                            winOnFirst += 1
                        }

                        1 -> {
                            pointsToUpdateLeagues = 5
                            winOnSecond += 1
                        }

                        2 -> {
                            pointsToUpdateLeagues = 3
                            winOnThird += 1
                        }

                        3 -> {
                            pointsToUpdateLeagues = 2
                            winOnForth += 1
                        }

                        4 -> {
                            pointsToUpdateLeagues = 2
                            winOnFirth += 1
                        }

                        5 -> {
                            pointsToUpdateLeagues = 1
                            winOnSixth += 1
                        }
                    }
                    streak += 1
                } else {
                    pointsToUpdateLeagues = -3
                    streak = 0
                    lost += 1
                }
                wordleTotalPointsAllTime += pointsToUpdateLeagues
                wordleTotalPointsForTheMonth += pointsToUpdateLeagues
                playing = false
            }
            val thisUser = usersReference.child(session.userInfo.userId.path)
            thisUser.child(wordlePath).child(wordleStatsPath)
                .setValue(currentUserStats)
                .addOnFailureListener {
                    it.message?.apply {
                        channel.trySend(FeedbackMessage.Error(this))
                    }
                }

            val newLeaderboardData = mapOf(
                "leaderboardTotalAllTime" to session.leaderboardTotalAllTime + pointsToUpdateLeagues,
                "leaderboardTotalMonthly" to session.leaderboardTotalMonthly + pointsToUpdateLeagues
            )

            thisUser.updateChildren(newLeaderboardData)
                .addOnFailureListener {
                    it.message?.apply {
                        channel.trySend(FeedbackMessage.Error(this))
                    }
                }.await()

            if (session.localListLeagues.isNotEmpty()) {
                session.localListLeagues.forEach {
                    val pointsRef = leaguesDatabaseReference.child(it.path).child(stringLeagueUsers)
                        .child(session.userInfo.userId).child(stringPointsForWordle)
                    val oldPoints = pointsRef.get().await().value as Long
                    pointsRef.setValue(oldPoints + pointsToUpdateLeagues)
                }
            }

            thisUser.child(hasPlayedWordleGame).setValue(true).addOnFailureListener {
                it.message?.apply {
                    channel.trySend(FeedbackMessage.Error(this))
                }
            }
            awaitClose { channel.close() }
        }
    }

    override suspend fun updateWordleList(
        session: Session,
        attemptList: List<WordleAttempt>
    ): Flow<FeedbackMessage> = channelFlow {
        if (session.userInfo.userId.isNotEmpty()) {
            try {
                val userWordleRef = usersReference.child(session.userInfo.userId.path).child(wordlePath)
                userWordleRef.child(listOfAttempts)
                    .setValue(attemptList)
                    .addOnFailureListener {
                        throw Exception(it.message.toString())
                    }
                userWordleRef.child(wordleStatsPath).child("playing").setValue(true).addOnFailureListener {
                    throw Exception(it.message.toString())
                }
            } catch (e:Exception) {
                e.message?.apply {
                    channel.trySend(FeedbackMessage.Error(this))
                }
            }

            awaitClose { channel.close() }
        }
    }

    override suspend fun getAttempts(session: Session): Flow<ResultOf<List<WordleAttempt>>> =
        callbackFlow {
            val ref =
                usersReference.child(session.userInfo.userId.path).child(wordlePath)
                    .child(listOfAttempts)
            val postListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dataSnapshot.getValue<List<WordleAttempt>>()?.apply {
                        trySend(ResultOf.Success(this))
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    trySend(ResultOf.Failure(FeedbackMessage.InternetIssues))
                }
            }
            ref.addListenerForSingleValueEvent(postListener)
            awaitClose { ref.removeEventListener(postListener) }
        }

    override suspend fun checkWord(word: String): Flow<ResultOf<String>> = callbackFlow {
        val ref = when (Locale.current.language) {
            "en" -> {
                englishWordsReference.child(word.lowercase())
            }

            else -> {
                portugueseWordsReference.child(word.lowercase())
            }
        }
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    trySend(ResultOf.Success(word))
                } else {
                    trySend(ResultOf.Failure(FeedbackMessage.WordNotIntList))
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                trySend(ResultOf.Failure(FeedbackMessage.InternetIssues))
            }
        }
        ref.addListenerForSingleValueEvent(postListener)
        awaitClose { ref.removeEventListener(postListener) }
    }

    override suspend fun sendFriendRequestV2(
        session: Session,
        friendId: String
    ): Flow<ResultOf<FeedbackMessage>> = callbackFlow {
        val userId = session.userInfo.userId

        // Validate input early
        when {
            friendId.isEmpty() -> {
                trySend(ResultOf.Success(FeedbackMessage.EmptyUser))
                close()
                return@callbackFlow
            }

            userId == friendId -> {
                trySend(ResultOf.Success(FeedbackMessage.CantAddYourself))
                close()
                return@callbackFlow
            }
        }

        val friendRef = usersReference.child(friendId.path)
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                when {
                    !dataSnapshot.exists() -> {
                        trySend(ResultOf.Success(FeedbackMessage.UserDoesntExist))
                    }

                    dataSnapshot.child(friendList).child(userId).exists() -> {
                        trySend(ResultOf.Success(FeedbackMessage.YouAreFriendsAlready))
                    }

                    dataSnapshot.child(friendListRequest).hasChild(userId) -> {
                        trySend(ResultOf.Success(FeedbackMessage.YouHaveAlreadySent))
                    }

                    else -> {
                        // Attempt to send the friend request
                        dataSnapshot.child(friendListRequest).child(userId).ref.setValue(userId)
                            .addOnSuccessListener {
                                trySend(ResultOf.Success(FeedbackMessage.FriendRequestSent))
                                close()
                            }
                            .addOnFailureListener { e ->
                                trySend(
                                    ResultOf.Failure(
                                        FeedbackMessage.Error(
                                            e.message ?: "Unknown error"
                                        )
                                    )
                                )
                                close()
                            }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                trySend(ResultOf.Failure(FeedbackMessage.InternetIssues))
                close()
            }
        }

        // Attach the listener and clean up properly
        friendRef.addListenerForSingleValueEvent(postListener)
        awaitClose {
            friendRef.removeEventListener(postListener)
        }
    }

    override suspend fun createNewLeague(
        session: Session,
        initialLeagueName: String
    ): Flow<ResultOf<League>> =
        channelFlow {
            val leagueId = UUID.randomUUID().toString()
            val userId = session.userInfo.userId
            val newLeagueSession = SessionInLeague(
                userId = userId,
                profileImage = session.userInfo.profilePictureUrl,
                userName = session.userInfo.userName,
                title = UserTitle.ADMIN,
                adminUser = true
            )
            val league = League(
                leagueId = leagueId,
                leagueName = initialLeagueName,
                startCycleDate = System.currentTimeMillis()
            )
            val ref = leaguesDatabaseReference.child(leagueId.path)
            ref.setValue(league).addOnFailureListener {
                it.message?.apply {
                    channel.trySend(ResultOf.Failure(FeedbackMessage.Error(message = this)))
                }
            }.addOnSuccessListener {
                usersReference.child(userId).child(stringLeagues).child(leagueId).setValue(leagueId)
                ref.child(stringLeagueUsers).child(userId).setValue(newLeagueSession)
                    .addOnFailureListener {
                        it.message?.apply {
                            channel.trySend(ResultOf.Failure(FeedbackMessage.Error(message = this)))
                        }
                    }
                channel.trySend(ResultOf.Success(league))
            }

            awaitClose { channel.close() }
        }

    override suspend fun loadLeagueUsers(league: League): Flow<ResultOf<League>> = channelFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                channel.trySend(ResultOf.Success(league.copy(
                    listOfUsers = snapshot.child(stringLeagueUsers).children.mapNotNull {
                        it.getValue<SessionInLeague>()
                    }
                )))
            }

            override fun onCancelled(error: DatabaseError) {

            }
        }
        val ref = leaguesDatabaseReference.child(league.leagueId.path)
        ref.addListenerForSingleValueEvent(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    override suspend fun loadLeagues(session: Session): Flow<ResultOf<Pair<List<League>, List<League>>>> =
        channelFlow {
            val userId = session.userInfo.userId
            val listOfLeagues =
                usersReference.child(userId.path).child(stringLeagues).get().await().children
            val listOfInvitation =
                usersReference.child(userId.path).child(stringLeagueInvitation).get().await().children
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    trySend(ResultOf.Success(Pair(listOfInvitation.mapNotNull {
                        it.key?.run {
                            snapshot.child(this).getValue<League>()?.run {
                                copy(endCycleString = transformEndCycleToString())
                            }
                        }
                    }, listOfLeagues.mapNotNull {
                        it.key?.run {
                            snapshot.child(this).getValue<League>()?.run {
                                copy(endCycleString = transformEndCycleToString())
                            }
                        }
                    })))
                }

                override fun onCancelled(error: DatabaseError) {
                    trySend(
                        ResultOf.LogMessage(
                            reference = LogTypes.PERMISSION,
                            errorMessage = error.message
                        )
                    )
                }

            }
            val ref = leaguesDatabaseReference
            ref.addListenerForSingleValueEvent(listener)
            awaitClose {
                ref.removeEventListener(listener)
            }
        }

    fun League.transformEndCycleToString(): String = kotlin.run {
        if (endCycleDate == 0L) {
            return@run "No end"
        }
        val formatter = SimpleDateFormat("dd/MMM/yyyy", java.util.Locale.ENGLISH)
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = endCycleDate
        formatter.format(calendar.time)
    }

    override suspend fun updateLeague(
        league: League,
        justIcon: Boolean,
        updateCycle: Boolean
    ): Flow<ResultOf<FeedbackMessage>> = channelFlow {
        val update = if (justIcon) {
            mutableMapOf<String, Any?>(
                "leagueIcon" to league.leagueIcon,
            )
        } else {
            mutableMapOf<String, Any?>(
                "leagueName" to league.leagueName,
            )
        }
        if (updateCycle) {
            update.putAll(
                mapOf(
                    "leagueRule" to league.leagueRule,
                    "leagueDuration" to league.leagueDuration,
                    "startCycleDate" to System.currentTimeMillis()
                )
            )
        }

        leaguesDatabaseReference.child(league.leagueId.path).updateChildren(update)
            .addOnSuccessListener {
                channel.trySend(
                    ResultOf.Success(
                        if (justIcon) {
                            FeedbackMessage.ImageUpdated
                        } else {
                            FeedbackMessage.LeagueUpdated
                        }
                    )
                )
            }.addOnFailureListener {
                it.message?.apply {
                    channel.trySend(ResultOf.Failure(FeedbackMessage.Error(this)))
                }
            }
        awaitClose {
            channel.close()
        }
    }

    override suspend fun updateToken(token: String) {
        globalToken = token
        delay(2000)
        val userId = getSignedInUserId()
        if (userId.isNotEmpty()) {
            usersReference.child(userId.path).child(stringFcmToken).setValue(token)
        }
    }

    override suspend fun loadToken() {
        globalToken = FirebaseMessaging.getInstance().token.await()
        if (getSignedInUserId() != emptyString) {
            usersReference.child(getSignedInUserId().path).child(stringFcmToken).setValue(globalToken)
        }
    }

    override suspend fun getConnectivityStatus(): Flow<ConnectivityStatus> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                trySend(ConnectivityStatus.AVAILABLE)
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                trySend(ConnectivityStatus.LOST)

            }

            override fun onUnavailable() {
                super.onUnavailable()
                trySend(ConnectivityStatus.UNAVAILABLE)
            }
        }

        connectivityManager.registerDefaultNetworkCallback(callback)
        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }.distinctUntilChanged()

    override suspend fun updateLeagueInvitation(
        hasAccepted: Boolean,
        session: Session,
        leagueId: String
    ): Flow<ResultOf<FeedbackMessage>> = channelFlow {
        val userId = session.userInfo.userId
        val userRef = usersReference.child(userId.path)
        userRef.child(stringLeagueInvitation).removeValue()
        if (hasAccepted) {
            userRef.child(stringLeagues).child(leagueId).setValue(leagueId)
            leaguesDatabaseReference.child(leagueId).child(stringLeagueUsers).child(userId)
                .setValue(
                    SessionInLeague(
                        userId = userId,
                        profileImage = session.userInfo.profilePictureUrl,
                        userName = session.userInfo.userName
                    )
                )
        }
        awaitClose { channel.close() }
    }

    override suspend fun userLeaveLeague(
        user: SessionInLeague,
        leagueId: String,
        isFromCurrentSession: Boolean
    ): Flow<ResultOf<FeedbackMessage>> = channelFlow {
        try {
            // Await both database operations
            leaguesDatabaseReference
                .child(leagueId.path)
                .child(stringLeagueUsers)
                .child(user.userId)
                .ref
                .awaitRemoveValue() // Custom suspend function for removeValue

            usersReference
                .child(user.userId.path)
                .child(stringLeagues)
                .child(leagueId)
                .ref
                .awaitRemoveValue() // Custom suspend function for removeValue

            // If both operations succeed, send success
            if (isFromCurrentSession) {
                trySend(ResultOf.Success(FeedbackMessage.LeftLeagueSuccessfully))
            } else {
                trySend(ResultOf.Success(FeedbackMessage.RemovedUserSuccessfully.apply {
                    extraData = arrayOf(user.userName)
                }))
            }
        } catch (exception: Exception) {
            // If any operation fails, send failure and log the error
            trySend(ResultOf.Failure(FeedbackMessage.InternetIssues))
            trySend(ResultOf.LogMessage(LogTypes.LEAGUE_ERROR, exception.message.toString()))
        } finally {
            awaitClose { channel.close() }
        }
    }

    override suspend fun updateTitle(
        userId: String,
        leagueId: String,
        userTitle: UserTitle
    ): Flow<ResultOf<FeedbackMessage>> = callbackFlow {

        leaguesDatabaseReference.child(leagueId.path).child(stringLeagueUsers).child(userId)
            .child("title").setValue(userTitle).addOnSuccessListener {
            trySend(ResultOf.Success(FeedbackMessage.TitleUpdated))
        }.addOnFailureListener {
            trySend(ResultOf.Failure(FeedbackMessage.InternetIssues))
        }
        awaitClose { channel.close() }
    }

    private suspend fun DatabaseReference.awaitRemoveValue() {
        suspendCancellableCoroutine { continuation ->
            this.removeValue()
                .addOnSuccessListener { continuation.resume(Unit) }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }
    }

    override suspend fun sendLeagueRequest(
        list: List<Session>,
        league: League
    ): Flow<ResultOf<FeedbackMessage>> = channelFlow {

        list.onEach {

            usersReference.child(it.userInfo.userId.path).child(stringLeagueInvitation)
                .child(league.leagueId)
                .setValue(league.leagueId).addOnSuccessListener {
                    trySend(ResultOf.Success(FeedbackMessage.FriendInvited))
                }
        }

        awaitClose { channel.close() }
    }

    override suspend fun sendQuestionSuggestion(question: Question): Flow<ResultOf<FeedbackMessage>> =
        channelFlow {
            val ref = suggestedQuestionsReference.child(UUID.randomUUID().toString())
            ref.setValue(question).addOnFailureListener {
                it.message?.apply {
                    channel.trySend(ResultOf.Failure(FeedbackMessage.Error(message = this)))
                }
            }.addOnSuccessListener {
                channel.trySend(ResultOf.Success(FeedbackMessage.QuestionSuggestionSent))
            }
            awaitClose { channel.close() }
        }

    override suspend fun loadFriendRequests(
        friendRequests: List<String>,
        friends: List<String>
    ): Flow<ResultOf<Pair<List<Session>, List<Session>>>> = callbackFlow {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val listOfRequests = mutableListOf<Session>()
                val listOfFriends = mutableListOf<Session>()
                friendRequests.forEach { userId ->
                    if (dataSnapshot.hasChild(userId)) {
                        dataSnapshot.child(userId.path).getValue<Session>()?.apply {
                            listOfRequests.add(this.withLoadedFriends(dataSnapshot.child(userId)))
                        }
                    }
                }
                friends.forEach { userId ->
                    if (dataSnapshot.hasChild(userId)) {
                        dataSnapshot.child(userId.path).getValue<Session>()?.apply {
                            listOfFriends.add(this.withLoadedFriends(dataSnapshot.child(userId)))
                        }
                    }
                }
                trySend(ResultOf.Success(Pair(listOfRequests, listOfFriends)))
            }

            override fun onCancelled(databaseError: DatabaseError) {
                trySend(ResultOf.Failure(FeedbackMessage.InternetIssues))
            }
        }
        usersReference.addListenerForSingleValueEvent(postListener)
        awaitClose { usersReference.removeEventListener(postListener) }
    }

    override suspend fun loadFriendRequests(
        friends: List<String>
    ): Flow<ResultOf<List<Session>>> = callbackFlow {
        val listOfFriends = mutableListOf<Session>()

        try {
            friends.forEach { friendId ->
                val dataSnapshot = usersReference.child(friendId.path).get().await()
                if (dataSnapshot.exists()) {
                    // Safely map the data snapshot to a Session object
                    val session = dataSnapshot.getValue(Session::class.java)
                    if (session != null) {
                        listOfFriends.add(session)
                    }
                }
            }

            // If all operations succeed, send the result
            trySend(ResultOf.Success(listOfFriends))
        } catch (e: Exception) {
            // Handle exceptions during data fetching or mapping
            trySend(ResultOf.LogMessage(LogTypes.FIREBASE_ERROR, e.message.toString()))
        }

        // Ensure the flow is properly closed
        awaitClose { channel.close() }
    }

    override suspend fun updateFriendRequest(
        session: Session,
        hasAccepted: Boolean,
        friendId: String
    ): Flow<ResultOf<Nothing>> = callbackFlow {
        val userId = session.userInfo.userId
        val ref = usersReference
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val friendsRef = dataSnapshot.child(userId.path).child(friendList)
                val friendRequest = dataSnapshot.child(userId.path).child(friendListRequest)
                val friendsListOfRequester = dataSnapshot.child(friendId.path).child(friendList)

                // Remove from the requests
                friendRequest.child(friendId).ref.removeValue()
                if (hasAccepted) {
                    friendsRef.child(friendId).ref.setValue(friendId)
                    friendsListOfRequester.child(userId).ref.setValue(userId)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                trySend(ResultOf.Failure(FeedbackMessage.InternetIssues))
            }
        }
        ref.addListenerForSingleValueEvent(postListener)
        awaitClose { ref.removeEventListener(postListener) }

    }

    override suspend fun removeFriend(
        session: Session,
        friendId: String
    ): Flow<ResultOf<FeedbackMessage>> =
        callbackFlow {
            val userId = session.userInfo.userId
            val postListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val usersList = dataSnapshot.child(userId.path).child(friendList)
                    val friendsList = dataSnapshot.child(friendId.path).child(friendList)
                    usersList.child(friendId).ref.removeValue()
                    friendsList.child(userId).ref.removeValue()
                    trySend(ResultOf.Success(FeedbackMessage.FriendRemoved))
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    trySend(ResultOf.Failure(FeedbackMessage.InternetIssues))
                }
            }
            usersReference.addListenerForSingleValueEvent(postListener)
            awaitClose { usersReference.removeEventListener(postListener) }
        }

}