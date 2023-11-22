package com.example.novagincanabiblica.data.repositories

import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import com.example.novagincanabiblica.data.models.BibleVerse
import com.example.novagincanabiblica.data.models.quiz.Question
import com.example.novagincanabiblica.data.models.Session
import com.example.novagincanabiblica.data.models.UserData
import com.example.novagincanabiblica.data.models.state.FeedbackMessage
import com.example.novagincanabiblica.data.models.state.ResultOf
import com.example.novagincanabiblica.data.models.wordle.Wordle
import com.example.novagincanabiblica.data.models.wordle.WordleAttempt
import kotlinx.coroutines.flow.Flow

interface BaseRepository {
    suspend fun signOut()
    suspend fun signIn(launcher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>)
    suspend fun getSession(result: Intent?): Flow<ResultOf<Session>>
    suspend fun getSignedInUser(): UserData?
    suspend fun updateHasPlayedBibleQuiz()
    suspend fun getSession(): Flow<ResultOf<Session>>
    suspend fun getDay(onlyOnce: Boolean): Flow<ResultOf<Int>>
    suspend fun loadDailyQuestion(day: Int): Flow<ResultOf<Question>>
    suspend fun getDailyBibleVerse(day: Int): Flow<ResultOf<BibleVerse>>
    suspend fun updateStats(currentQuestion: Question, isCorrect: Boolean, session: Session): Flow<FeedbackMessage>
    suspend fun isThisGameModeAvailable(key: String): Flow<Boolean>
    fun updateGameModeValue(key: String, value: Boolean)
    suspend fun getWordle(day: Int): Flow<ResultOf<Wordle>>
    suspend fun updateWordleStats(userFoundTheWord: Boolean, session: Session, numberOfAttempt: List<WordleAttempt>): Flow<FeedbackMessage>
    suspend fun getAttemps(session: Session): Flow<ResultOf<List<WordleAttempt>>>
    suspend fun checkWord(word: String): Flow<ResultOf<String>>
    suspend fun updateWordleList(session: Session, attemptList: List<WordleAttempt>): Flow<FeedbackMessage>
    suspend fun sendFriendRequestV2(session: Session, friendId: String): Flow<ResultOf<FeedbackMessage>>
    suspend fun loadFriendRequests(friendRequests: List<String>, friends: List<String>): Flow<ResultOf<Pair<List<Session>,List<Session>>>>
    suspend fun updateFriendRequest(session: Session, hasAccepted: Boolean, friendId: String): Flow<ResultOf<Nothing>>
    suspend fun removeFriend(session: Session, friendId: String): Flow<ResultOf<FeedbackMessage>>
    suspend fun loadToken()
    suspend fun sendQuestionSuggestion(question: Question): Flow<ResultOf<FeedbackMessage>>
}