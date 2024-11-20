package com.bsoftwares.thebiblequiz.data.repositories

import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import com.bsoftwares.thebiblequiz.data.models.BibleVerse
import com.bsoftwares.thebiblequiz.data.models.League
import com.bsoftwares.thebiblequiz.data.models.quiz.Question
import com.bsoftwares.thebiblequiz.data.models.Session
import com.bsoftwares.thebiblequiz.data.models.state.ConnectivityStatus
import com.bsoftwares.thebiblequiz.data.models.state.FeedbackMessage
import com.bsoftwares.thebiblequiz.data.models.state.ResultOf
import com.bsoftwares.thebiblequiz.data.models.wordle.Wordle
import com.bsoftwares.thebiblequiz.data.models.wordle.WordleAttempt
import kotlinx.coroutines.flow.Flow

interface BaseRepository {
    suspend fun signOut()
    suspend fun signIn(launcher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>)
    suspend fun getSession(result: Intent?): Flow<ResultOf<Session>>
    suspend fun getSignedInUserId(): String
    suspend fun updateHasPlayedBibleQuiz()
    suspend fun getSession(): Flow<ResultOf<Session>>
    suspend fun getSession(userId: String): Flow<ResultOf<Session>>
    suspend fun getDay(): Flow<ResultOf<Int>>
    suspend fun loadDailyQuestion(day: Int): Flow<ResultOf<Question>>
    suspend fun getDailyBibleVerse(day: Int): Flow<ResultOf<BibleVerse>>
    suspend fun updateStats(currentQuestion: Question, isCorrect: Boolean, session: Session, answerSelectedByTheUser: String = "You did not select an answer"): Flow<FeedbackMessage>
    suspend fun isThisGameModeAvailable(key: String): Flow<Boolean>
    fun updateGameModeValue(key: String, value: Boolean)
    suspend fun getWordle(day: Int): Flow<ResultOf<Wordle>>
    suspend fun updateWordleStats(userFoundTheWord: Boolean, session: Session, numberOfAttempt: List<WordleAttempt>): Flow<FeedbackMessage>
    suspend fun getAttempts(session: Session): Flow<ResultOf<List<WordleAttempt>>>
    suspend fun checkWord(word: String): Flow<ResultOf<String>>
    suspend fun updateWordleList(session: Session, attemptList: List<WordleAttempt>): Flow<FeedbackMessage>
    suspend fun sendFriendRequestV2(session: Session, friendId: String): Flow<ResultOf<FeedbackMessage>>
    suspend fun loadFriendRequests(friendRequests: List<String>, friends: List<String>): Flow<ResultOf<Pair<List<Session>,List<Session>>>>
    suspend fun updateFriendRequest(session: Session, hasAccepted: Boolean, friendId: String): Flow<ResultOf<Nothing>>
    suspend fun removeFriend(session: Session, friendId: String): Flow<ResultOf<FeedbackMessage>>
    suspend fun sendQuestionSuggestion(question: Question): Flow<ResultOf<FeedbackMessage>>
    suspend fun createNewLeague(session: Session): Flow<ResultOf<League>>
    suspend fun loadLeagueUsers(league: League): Flow<ResultOf<League>>
    suspend fun loadLeagues(session: Session): Flow<ResultOf<Pair<List<League>,List<League>>>>
    suspend fun sendLeagueRequest(list: List<Session>, league: League): Flow<ResultOf<FeedbackMessage>>
    suspend fun updateLeagueInvitation(hasAccepted: Boolean, session: Session, leagueId: String): Flow<ResultOf<FeedbackMessage>>
    suspend fun updateLeague(league: League, justIcon: Boolean, updateCycle: Boolean): Flow<ResultOf<FeedbackMessage>>
    suspend fun updateToken(token: String)
    suspend fun loadToken()
    suspend fun getConnectivityStatus(): Flow<ConnectivityStatus>
    suspend fun updateUserPremiumStatus(session: Session, newValue: Boolean): Flow<FeedbackMessage>
    suspend fun getUserPremiumStatus(): Flow<ResultOf<Boolean>>
    suspend fun observeThisLeague(currentLeague: League): Flow<ResultOf<League>>
    suspend fun deleteLeague(leagueId: String): Flow<ResultOf<FeedbackMessage>>
}