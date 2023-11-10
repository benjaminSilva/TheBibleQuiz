package com.example.novagincanabiblica.data.repositories

import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import com.example.novagincanabiblica.data.models.BibleVerse
import com.example.novagincanabiblica.data.models.quiz.Question
import com.example.novagincanabiblica.data.models.Session
import com.example.novagincanabiblica.data.models.UserData
import com.example.novagincanabiblica.data.models.state.ResultOf
import com.example.novagincanabiblica.data.models.wordle.Wordle
import com.example.novagincanabiblica.data.models.wordle.WordleAttempt
import kotlinx.coroutines.flow.Flow

interface SoloModeRepo {
    suspend fun signOut()
    suspend fun signIn(launcher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>)
    suspend fun getSession(result: ActivityResult): Flow<ResultOf<Session>>
    suspend fun getSignedInUser(): UserData?
    suspend fun updateHasPlayedBibleQuiz()
    suspend fun getSession(): Flow<ResultOf<Session>>
    suspend fun getDay(): Flow<ResultOf<Int>>
    suspend fun checkWord(word: String): Flow<ResultOf<String>>
    suspend fun loadDailyQuestion(day: Int): Flow<ResultOf<Question>>
    suspend fun getDailyBibleVerse(day: Int): Flow<ResultOf<BibleVerse>>
    suspend fun updateStats(currentQuestion: Question, isCorrect: Boolean, session: Session): Flow<String>
    suspend fun isThisGameModeAvailable(key: String): Flow<Boolean>
    fun updateGameModeValue(key: String, value: Boolean)
    suspend fun getWordle(day: Int): Flow<ResultOf<Wordle>>
    suspend fun updateWordleStats(userFoundTheWord: Boolean, session: Session, numberOfAttempt: List<WordleAttempt>): Flow<String>
    suspend fun updateWordleList(
        session: Session,
        attemptList: List<WordleAttempt>
    ): Flow<String>

    suspend fun getAttemps(session: Session): Flow<ResultOf<List<WordleAttempt>>>
}