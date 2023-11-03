package com.example.novagincanabiblica.data.repositories

import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.compose.ui.text.intl.Locale
import com.example.novagincanabiblica.client.GoogleAuthUiClient
import com.example.novagincanabiblica.client.WordleService
import com.example.novagincanabiblica.data.models.Answer
import com.example.novagincanabiblica.data.models.BibleVerse
import com.example.novagincanabiblica.data.models.Question
import com.example.novagincanabiblica.data.models.QuestionDifficulty
import com.example.novagincanabiblica.data.models.Session
import com.example.novagincanabiblica.data.models.UserData
import com.example.novagincanabiblica.data.models.WordleCheck
import com.example.novagincanabiblica.data.models.state.ResultOf
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject


class SoloModeRepoImpl @Inject constructor(
    private val googleAuthUiClient: GoogleAuthUiClient,
    private val firebaseDatabase: FirebaseDatabase,
    private val wordleService: WordleService
) : SoloModeRepo {

    private val usersReference = firebaseDatabase.reference.child("users")
    private val bibleVerseReference = firebaseDatabase.reference.child("dailyBibleVerse")
    private val questionReference = firebaseDatabase.reference.child("dailyQuiz")

    override suspend fun loadDailyQuestion(day: Int): Flow<ResultOf<Question>> = callbackFlow {
        val ref = questionReference.child(Locale.current.language).child(day.toString())
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.child("listOfAnswers").getValue<List<Answer>>()?.apply {
                    this
                }
                dataSnapshot.child("listOfAnswers").child("isCorrectAnswer").value
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
            session.let { user ->
                user.userInfo?.userId?.let { userId ->
                    val currentUserStats = user.userStats.apply {
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

                    usersReference.child(userId).child("userStats").setValue(currentUserStats)
                        .addOnFailureListener {
                            it.message?.apply {
                                trySend(this)
                            }
                        }
                }

            }
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
                        session.userInfo.userId.apply {
                            val ref = firebaseDatabase.reference
                            ref.child("/users/$this").setValue(session)
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
    }

    override suspend fun getSession(): Flow<Session> = channelFlow {
        getSignedInUser()?.userId?.apply {
            val test = usersReference.child(this)

            val postListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dataSnapshot.getValue<Session>()?.apply {
                        trySend(this)
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
        val ref = firebaseDatabase.reference.child("day")
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.getValue<Int>()?.apply {
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
        val ref = bibleVerseReference.child(Locale.current.language).child(day.toString())
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
                            trySend(ResultOf.Success(this[0].word))
                        }
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

}