package com.example.novagincanabiblica.data.repositories

import android.content.SharedPreferences
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.compose.ui.text.intl.Locale
import com.example.novagincanabiblica.client.GoogleAuthUiClient
import com.example.novagincanabiblica.client.WordleService
import com.example.novagincanabiblica.data.models.BibleVerse
import com.example.novagincanabiblica.data.models.Session
import com.example.novagincanabiblica.data.models.UserData
import com.example.novagincanabiblica.data.models.quiz.Question
import com.example.novagincanabiblica.data.models.quiz.QuestionDifficulty
import com.example.novagincanabiblica.data.models.state.FeedbackMessage
import com.example.novagincanabiblica.data.models.state.ResultOf
import com.example.novagincanabiblica.data.models.wordle.Wordle
import com.example.novagincanabiblica.data.models.wordle.WordleAttempState
import com.example.novagincanabiblica.data.models.wordle.WordleAttempt
import com.example.novagincanabiblica.data.models.wordle.WordleCheck
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject


class RepositoryImpl @Inject constructor(
    private val googleAuthUiClient: GoogleAuthUiClient,
    private val wordleService: WordleService,
    private val sharedPreferences: SharedPreferences,
    baseDatabase: FirebaseDatabase,
    usersDatabase: FirebaseDatabase,
    dailyVerseDatabase: FirebaseDatabase,
    wordleDatabase: FirebaseDatabase,
    quizDatabase: FirebaseDatabase,
    englishWords: FirebaseDatabase,
    val firebaseMessaging: FirebaseMessaging
) : Repository {

    private val firebaseRef = baseDatabase.reference
    private val usersReference = usersDatabase.reference
    private val bibleVerseReference = dailyVerseDatabase.reference
    private val wordleReference = wordleDatabase.reference
    private val quizReference = quizDatabase.reference
    private val englishWordsReference = englishWords.reference
    private var globalToken = ""

    override suspend fun loadToken() {
        val token = firebaseMessaging.token.await()
        token?.apply {
            globalToken = this
        }
    }

    override suspend fun loadDailyQuestion(day: Int): Flow<ResultOf<Question>> = callbackFlow {
        val ref = quizReference.child(Locale.current.language).child("day$day")
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
        session: Session
    ): Flow<FeedbackMessage> =
        channelFlow {
            Log.i("Stats update", "Channel flow")
            session.let { user ->
                Log.i("Stats update", "User $user")
                user.userInfo?.userId?.let { userId ->
                    Log.i("Stats update", "User Id: $userId")
                    val currentUserStats = user.quizStats.apply {
                        Log.i("Stats update", "Question Stats $this")
                        if (isCorrect) {
                            streak += 1
                            when (currentQuestion.difficulty) {
                                QuestionDifficulty.EASY -> easyCorrect += 1
                                QuestionDifficulty.MEDIUM -> mediumCorrect += 1
                                QuestionDifficulty.HARD -> hardCorrect += 1
                                QuestionDifficulty.IMPOSSIBLE -> impossibleCorrect += 1
                            }
                        } else {
                            streak = 0
                            when (currentQuestion.difficulty) {
                                QuestionDifficulty.EASY -> easyWrong += 1
                                QuestionDifficulty.MEDIUM -> mediumWrong += 1
                                QuestionDifficulty.HARD -> hardWrong += 1
                                QuestionDifficulty.IMPOSSIBLE -> impossibleWrong += 1
                            }
                        }
                    }

                    usersReference.child(userId).child("quizStats").setValue(currentUserStats)
                        .addOnFailureListener {
                            it.message?.apply {
                                trySend(FeedbackMessage.InternetIssues)
                            }
                        }
                }

            }
        }

    override fun updateGameModeValue(key: String, value: Boolean) {
        sharedPreferences.edit().apply {
            putBoolean(key, value)
        }.apply()
    }

    override suspend fun isThisGameModeAvailable(key: String): Flow<Boolean> = flow {
        emit(sharedPreferences.getBoolean(key, false))
    }

    override suspend fun signIn(launcher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>) {
        val signInIntentSender = googleAuthUiClient.signIn()
        launcher.launch(
            IntentSenderRequest.Builder(
                signInIntentSender ?: return
            ).build()
        )
    }

    override suspend fun signOut() {
        googleAuthUiClient.signOut()
    }

    override suspend fun getSession(result: ActivityResult): Flow<ResultOf<Session>> = channelFlow {
        googleAuthUiClient.signInWithIntent(
            intent = result.data ?: return@channelFlow
        ).also { session ->
            handleSessionIfItExists(session).collectLatest {
                trySend(it)
            }
        }
    }

    private fun handleSessionIfItExists(session: Session): Flow<ResultOf<Session>> = channelFlow {
        session.userInfo?.userId?.apply {
            val test = usersReference.child(this)
            val postListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        dataSnapshot.getValue<Session>()?.apply {
                            trySend(ResultOf.Success(this))
                        }
                    } else {
                        val updatedSession = session.copy(fcmToken = globalToken)
                        this@apply.apply {
                            usersReference.child(this).setValue(updatedSession)
                        }
                        trySend(ResultOf.Success(updatedSession))
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    trySend(ResultOf.Failure(FeedbackMessage.InternetIssues))
                }
            }
            test.addValueEventListener(postListener)
            awaitClose { test.removeEventListener(postListener) }
        }
    }

    override suspend fun getSignedInUser(): UserData? = googleAuthUiClient.getSignerUser()

    override suspend fun updateHasPlayedBibleQuiz() {
        getSignedInUser()?.userId?.apply {
            usersReference.child(this).child("hasPlayedQuizGame").setValue(true)
        }
        updateGameModeValue("hasPlayedQuizGame", true)
    }

    override suspend fun getSession(): Flow<ResultOf<Session>> = channelFlow {
        getSignedInUser()?.userId?.apply {
            val test = usersReference.child(this)

            val postListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dataSnapshot.getValue<Session>()?.apply {
                        trySend(ResultOf.Success(this))
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            }
            test.addValueEventListener(postListener)
            awaitClose { test.removeEventListener(postListener) }
        }
    }

    override suspend fun getDay(onlyOnce: Boolean): Flow<ResultOf<Int>> = callbackFlow {
        val ref = firebaseRef.child("day")
        val currentDay = sharedPreferences.getInt("day", 0)
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.getValue<Int>()?.apply {
                    if (this > currentDay) { //Update Sharedpref
                        sharedPreferences.edit().putInt("day", this).apply()
                        updateGameModeValue("hasUserPlayedWordle", false)
                        updateGameModeValue("hasPlayedQuizGame", false)
                    }
                    trySend(ResultOf.Success(this))
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                trySend(ResultOf.Failure(FeedbackMessage.InternetIssues))
            }
        }
        if (onlyOnce) {
            ref.addListenerForSingleValueEvent(postListener)
        } else {
            ref.addValueEventListener(postListener)
        }
        awaitClose { ref.removeEventListener(postListener) }
    }

    override suspend fun getDailyBibleVerse(day: Int): Flow<ResultOf<BibleVerse>> = callbackFlow {
        val ref = bibleVerseReference.child(Locale.current.language).child("day$day")
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
        ref.addValueEventListener(postListener)
        awaitClose { ref.removeEventListener(postListener) }
    }

    override suspend fun checkWord(word: String): Flow<ResultOf<String>> = callbackFlow {
        val test = System.currentTimeMillis()
        Log.i("Check Word", "Start at: $test")
        wordleService.checkWord(word)?.apply {
            enqueue(object : Callback<List<WordleCheck>> {
                override fun onResponse(
                    call: Call<List<WordleCheck>>,
                    response: Response<List<WordleCheck>>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.apply {
                            trySend(ResultOf.Success(word))
                        }
                    } else {
                        trySend(ResultOf.Failure(FeedbackMessage.WordNotIntList))
                    }
                    Log.i("Check Word", "Ended at:${System.currentTimeMillis() - test}")
                }

                override fun onFailure(call: Call<List<WordleCheck>>, t: Throwable) {
                    t.message?.apply {
                        trySend(ResultOf.Failure(FeedbackMessage.InternetIssues))
                    }
                }

            })
            awaitClose { cancel() }
        }

    }

    override suspend fun getWordle(day: Int): Flow<ResultOf<Wordle>> = callbackFlow {
        val ref = wordleReference.child(Locale.current.language).child("day$day")
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
        updateGameModeValue(key = "hasPlayedWordleGame", true)
        session.let { user ->
            user.userInfo?.userId?.let { userId ->
                val getWhichTry =
                    numberOfAttempt.indexOfLast { it.attemptState == WordleAttempState.USER_HAS_TRIED }
                val currentUserStats = user.wordle.wordleStats.apply {
                    if (userFoundTheWord) {
                        when (getWhichTry) {
                            0 -> winOnFirst += 1
                            1 -> winOnSecond += 1
                            2 -> winOnThird += 1
                            3 -> winOnForth += 1
                            4 -> winOnFirth += 1
                            5 -> winOnSixth += 1
                        }
                        streak += 1
                    } else {
                        streak = 0
                        lost += 1
                    }
                }
                val thisUser = usersReference.child(userId)
                thisUser.child("wordle").child("wordleStats")
                    .setValue(currentUserStats)
                    .addOnFailureListener {
                        it.message?.apply {
                            trySend(FeedbackMessage.InternetIssues)
                        }
                    }

                thisUser.child("hasPlayerWordleGame").setValue(true).addOnFailureListener {
                    it.message?.apply {
                        trySend(FeedbackMessage.InternetIssues)
                    }
                }
            }
        }
    }

    override suspend fun updateWordleList(
        session: Session,
        attemptList: List<WordleAttempt>
    ): Flow<FeedbackMessage> = channelFlow {
        Log.i("Wordle Test", "Inside Channel Flow")
        session.userInfo?.userId?.let { userId ->
            Log.i("Wordle Test", "User Id $userId")
            Log.i("Wordle Test", "Attempt List $attemptList")
            usersReference.child(userId).child("wordle").child("listOfAttemps")
                .setValue(attemptList)
                .addOnFailureListener {
                    it.message?.apply {
                        trySend(FeedbackMessage.InternetIssues)
                    }
                }

        }
    }

    override suspend fun getAttemps(session: Session): Flow<ResultOf<List<WordleAttempt>>> =
        callbackFlow {
            session.userInfo?.userId?.let { userId ->
                val ref = usersReference.child(userId).child("wordle").child("listOfAttemps")
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
                ref.addValueEventListener(postListener)
                awaitClose { ref.removeEventListener(postListener) }
            }

        }

    override suspend fun checkWordV2(word: String): Flow<ResultOf<String>> = callbackFlow {
        val ref = englishWordsReference.child(word.lowercase())
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
        ref.addValueEventListener(postListener)
        awaitClose { ref.removeEventListener(postListener) }
    }

    override suspend fun verifyIfFriendExists(friendId: String): Flow<ResultOf<Boolean>> =
        callbackFlow {
            val ref = usersReference.child(friendId)
            val postListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        trySend(ResultOf.Success(false))
                    } else {
                        trySend(ResultOf.Success(true))
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    trySend(ResultOf.Failure(FeedbackMessage.InternetIssues))
                }
            }
            ref.addValueEventListener(postListener)
            awaitClose { ref.removeEventListener(postListener) }
        }

    override suspend fun sendFriendRequestV2(
        session: Session,
        friendId: String
    ): Flow<ResultOf<FeedbackMessage>> =
        callbackFlow {
            session.userInfo?.userId?.let { userId ->
                val postListener = object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (userId == friendId) {
                            trySend(ResultOf.Success(FeedbackMessage.CantAddYourself))
                            return
                        }
                        if (!dataSnapshot.exists()) {
                            trySend(ResultOf.Success(FeedbackMessage.UserDoesntExist))
                            return
                        }
                        val friendsFriendList = dataSnapshot.child("friendList")
                        if (friendsFriendList.exists()) {
                            friendsFriendList.getValue<List<String>>()?.apply {
                                if (contains(userId)) {
                                    trySend(ResultOf.Success(FeedbackMessage.YouAreFriendsAlready))
                                    return
                                }
                            }
                        }
                        val friendsFriendRequestList = dataSnapshot.child("friendRequests")
                        if (!friendsFriendRequestList.exists()) {
                            friendsFriendRequestList.ref.setValue(listOf(userId))
                        } else {
                            friendsFriendRequestList.getValue<List<String>>()?.apply {
                                if (contains(userId)) {
                                    trySend(ResultOf.Success(FeedbackMessage.YouHaveAlreadySent))
                                    return
                                }
                                val mutableList = this.toMutableList()
                                mutableList.add(userId)
                                friendsFriendRequestList.ref.setValue(mutableList)
                            }
                        }
                        trySend(ResultOf.Success(FeedbackMessage.FriendRequestSent))
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        trySend(ResultOf.Failure(FeedbackMessage.InternetIssues))
                    }
                }
                usersReference.child(friendId).addValueEventListener(postListener)
                awaitClose { usersReference.removeEventListener(postListener) }
            }
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
                        dataSnapshot.child(userId).getValue<Session>()?.apply {
                            listOfRequests.add(this)
                        }
                    }
                }
                friends.forEach { userId ->
                    if (dataSnapshot.hasChild(userId)) {
                        dataSnapshot.child(userId).getValue<Session>()?.apply {
                            listOfFriends.add(this)
                        }
                    }
                }
                trySend(ResultOf.Success(Pair(listOfRequests, listOfFriends)))
            }

            override fun onCancelled(databaseError: DatabaseError) {
                trySend(ResultOf.Failure(FeedbackMessage.InternetIssues))
            }
        }
        usersReference.addValueEventListener(postListener)
        awaitClose { usersReference.removeEventListener(postListener) }
    }

    override suspend fun updateFriendRequest(
        session: Session,
        hasAccepted: Boolean,
        friendId: String
    ): Flow<ResultOf<Nothing>> = callbackFlow {
        session.userInfo?.userId?.let { userId ->
            val ref = usersReference
            val postListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val friendsRef = dataSnapshot.child(userId).child("friendList")
                    val friendRequest = dataSnapshot.child(userId).child("friendRequests")
                    val friendsListOfRequester = dataSnapshot.child(friendId).child("friendList")

                    // Remove from the requests
                    friendRequest.getValue<List<String>>()?.apply {
                        val mutableList = this.toMutableList()
                        mutableList.remove(friendId)
                        friendRequest.ref.setValue(mutableList)
                    }
                    if (hasAccepted) {
                        if (!friendsListOfRequester.exists()) {
                            friendsListOfRequester.ref.setValue(listOf(userId))
                        } else {
                            friendsListOfRequester.getValue<List<String>>()?.apply {
                                if (!this.contains(userId)) {
                                    val mutableList = toMutableList().apply {
                                        add(userId)
                                    }
                                    friendsListOfRequester.ref.setValue(mutableList)
                                }
                            }
                        }
                        if (!friendsRef.exists()) {
                            friendsRef.ref.setValue(listOf(friendId))
                        } else {
                            friendsRef.getValue<List<String>>()?.apply {
                                if (!this.contains(friendId)) {
                                    val mutableList = toMutableList().apply {
                                        add(friendId)
                                    }
                                    friendsRef.ref.setValue(mutableList)
                                }
                            }
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    trySend(ResultOf.Failure(FeedbackMessage.InternetIssues))
                }
            }
            ref.addListenerForSingleValueEvent(postListener)
            awaitClose { ref.removeEventListener(postListener) }
        }
    }

    override suspend fun sendFriendRequest(
        session: Session,
        friendId: String
    ): Flow<ResultOf<Boolean>> =
        callbackFlow {
            session.userInfo?.userId?.let { userId ->
                Log.i("Add friend", friendId)
                val ref = usersReference.child(friendId).child("friendRequests")
                val postListener = object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        Log.i("Add friend", dataSnapshot.toString())
                        if (!dataSnapshot.exists()) {
                            ref.setValue(listOf(userId))
                        } else {

                            dataSnapshot.getValue<List<String>>()?.apply {
                                val mutableList = this.toMutableList()
                                mutableList.add(userId)
                                ref.setValue(mutableList)
                            }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        trySend(ResultOf.Failure(FeedbackMessage.InternetIssues))
                    }
                }
                ref.addListenerForSingleValueEvent(postListener)
                awaitClose { ref.removeEventListener(postListener) }
            }

        }

    override suspend fun removeFriend(
        session: Session,
        friendId: String
    ): Flow<ResultOf<FeedbackMessage>> =
        callbackFlow {
            session.userInfo?.userId?.let { userId ->
                Log.i("Remove Friend", friendId)
                val postListener = object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val usersList = dataSnapshot.child(userId).child("friendList")
                        val friendsList = dataSnapshot.child(friendId).child("friendList")
                        friendsList.getValue<List<String>>()?.apply {
                            val mutableList = this.toMutableList()
                            mutableList.remove(userId)
                            friendsList.ref.setValue(mutableList)
                        }
                        usersList.getValue<List<String>>()?.apply {
                            val mutableList = this.toMutableList()
                            mutableList.remove(friendId)
                            usersList.ref.setValue(mutableList)
                        }
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
}