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
import com.example.novagincanabiblica.data.models.quiz.Question
import com.example.novagincanabiblica.data.models.quiz.QuestionDifficulty
import com.example.novagincanabiblica.data.models.Session
import com.example.novagincanabiblica.data.models.UserData
import com.example.novagincanabiblica.data.models.wordle.WordleCheck
import com.example.novagincanabiblica.data.models.state.ResultOf
import com.example.novagincanabiblica.data.models.wordle.Wordle
import com.example.novagincanabiblica.data.models.wordle.WordleAttempState
import com.example.novagincanabiblica.data.models.wordle.WordleAttempt
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject


class SoloModeRepoImpl @Inject constructor(
    private val googleAuthUiClient: GoogleAuthUiClient,
    private val wordleService: WordleService,
    private val sharedPreferences: SharedPreferences,
    private val baseDatabase: FirebaseDatabase,
    private val usersDatabase: FirebaseDatabase,
    private val dailyVerseDatabase: FirebaseDatabase,
    private val wordleDatabase: FirebaseDatabase,
    private val quizDatabase: FirebaseDatabase
    ) : SoloModeRepo {

    private val firebaseRef = baseDatabase.reference
    private val usersReference = usersDatabase.reference
    private val bibleVerseReference = dailyVerseDatabase.reference
    private val wordleReference = wordleDatabase.reference
    private val quizReference = quizDatabase.reference

    override suspend fun loadDailyQuestion(day: Int): Flow<ResultOf<Question>> = callbackFlow {
        val ref = quizReference.child(Locale.current.language).child("day$day")
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.getValue<Question>()?.apply {
                    trySend(ResultOf.Success(this))
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                trySend(ResultOf.Failure(databaseError.message))
            }
        }
        ref.addListenerForSingleValueEvent(postListener)
        awaitClose { ref.removeEventListener(postListener) }
    }

    override suspend fun updateStats(
        currentQuestion: Question,
        isCorrect: Boolean,
        session: Session
    ): Flow<String> =
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
                                Log.i("Stats update", "Error message: $this")
                                trySend(this)
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
                        this@apply.apply {
                            usersReference.child(this).setValue(session)
                        }
                        trySend(ResultOf.Success(session))
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    trySend(ResultOf.Failure(databaseError.message))
                }
            }
            test.addListenerForSingleValueEvent(postListener)
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

    override suspend fun getDay(): Flow<ResultOf<Int>> = callbackFlow {
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
                trySend(ResultOf.Failure(databaseError.message))
            }
        }
        ref.addValueEventListener(postListener)
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
                trySend(ResultOf.Failure(databaseError.message))
            }
        }
        ref.addValueEventListener(postListener)
        awaitClose { ref.removeEventListener(postListener) }
    }

    override suspend fun checkWord(word: String): Flow<ResultOf<String>> = callbackFlow {
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
                        trySend(ResultOf.Failure("Word doens't exist"))
                    }
                }

                override fun onFailure(call: Call<List<WordleCheck>>, t: Throwable) {
                    t.message?.apply {
                        trySend(ResultOf.Failure(this))
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
                trySend(ResultOf.Failure(databaseError.message))
            }
        }
        ref.addValueEventListener(postListener)
        awaitClose { ref.removeEventListener(postListener) }
    }

    override suspend fun updateWordleStats(
        userFoundTheWord: Boolean,
        session: Session,
        numberOfAttempt: List<WordleAttempt>
    ): Flow<String> = channelFlow {
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
                        lost += 1
                    }
                }
                val thisUser = usersReference.child(userId)
                thisUser.child("wordle").child("wordleStats")
                    .setValue(currentUserStats)
                    .addOnFailureListener {
                        it.message?.apply {
                            trySend(this)
                        }
                    }

                thisUser.child("hasPlayerWordleGame").setValue(true).addOnFailureListener {
                    it.message?.apply {
                        trySend(this)
                    }
                }
            }
        }
    }

    override suspend fun updateWordleList(
        session: Session,
        attemptList: List<WordleAttempt>
    ): Flow<String> = channelFlow {
        Log.i("Wordle Test", "Inside Channel Flow")
        session.userInfo?.userId?.let { userId ->
            Log.i("Wordle Test", "User Id $userId")
            Log.i("Wordle Test", "Attempt List $attemptList")
            usersReference.child(userId).child("wordle").child("listOfAttemps")
                .setValue(attemptList)
                .addOnFailureListener {
                    it.message?.apply {
                        trySend(this)
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
                        trySend(ResultOf.Failure(databaseError.message))
                    }
                }
                ref.addListenerForSingleValueEvent(postListener)
                awaitClose { ref.removeEventListener(postListener) }
            }

        }

}