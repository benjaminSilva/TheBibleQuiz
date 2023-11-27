package com.example.novagincanabiblica.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.example.novagincanabiblica.client.GoogleAuthUiClient
import com.example.novagincanabiblica.data.repositories.BaseRepository
import com.example.novagincanabiblica.data.repositories.BaseRepositoryImpl
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun getGoogleAuthClient(@ApplicationContext appContext: Context) = GoogleAuthUiClient(
        context = appContext,
        oneTapClient = Identity.getSignInClient(appContext)
    )

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("session_prefs", MODE_PRIVATE)
    }

    @Singleton
    @Provides
    fun getSoloModeRepo(
        googleAuthUiClient: GoogleAuthUiClient,
        sharedPreferences: SharedPreferences,
        @Named("baseDatabase") baseDatabase: FirebaseDatabase,
        @Named("usersDatabase") usersDatabase: FirebaseDatabase,
        @Named("dailyVerseDatabase") dailyVerseDatabase: FirebaseDatabase,
        @Named("wordleDatabase") wordleDatabase: FirebaseDatabase,
        @Named("quizDatabase") quizDatabase: FirebaseDatabase,
        @Named("englishWordsDatabase") englishWords: FirebaseDatabase,
        @Named("portugueseWordsDatabase") portugueseWords: FirebaseDatabase,
        @Named("suggestedQuestionsDatabase") suggestedQuestionsDatabase: FirebaseDatabase,
        @Named("leaguesDatabase") leaguesDatabase: FirebaseDatabase,
        firebaseMessaging: FirebaseMessaging
    ): BaseRepository = BaseRepositoryImpl(
        googleAuthUiClient = googleAuthUiClient,
        sharedPreferences = sharedPreferences,
        baseDatabase = baseDatabase,
        usersDatabase = usersDatabase,
        dailyVerseDatabase = dailyVerseDatabase,
        wordleDatabase = wordleDatabase,
        quizDatabase = quizDatabase,
        englishWords = englishWords,
        portugueseWords = portugueseWords,
        leaguesDatabase = leaguesDatabase,
        suggestedQuestionsDatabase = suggestedQuestionsDatabase,
        firebaseMessaging = firebaseMessaging
    )

    @Singleton
    @Provides
    @Named("baseDatabase")
    fun getFirebaseRealTimeDatabase() = FirebaseDatabase.getInstance()

    @Singleton
    @Provides
    @Named("usersDatabase")
    fun getUsersRealTimeDatabase() = FirebaseDatabase.getInstance("https://the-bible-quiz-users.firebaseio.com/")

    @Singleton
    @Provides
    @Named("dailyVerseDatabase")
    fun getDailyVerseDatabase() = FirebaseDatabase.getInstance("https://the-bible-quiz-daily-bible-verse.firebaseio.com/")

    @Singleton
    @Provides
    @Named("wordleDatabase")
    fun getWordleDatabase() = FirebaseDatabase.getInstance("https://the-bible-quiz-wordle.firebaseio.com/")

    @Singleton
    @Provides
    @Named("quizDatabase")
    fun getQuizDatabase() = FirebaseDatabase.getInstance("https://the-bible-quiz-questions.firebaseio.com/")

    @Singleton
    @Provides
    @Named("englishWordsDatabase")
    fun getAllEnglishWords() = FirebaseDatabase.getInstance("https://the-bible-quiz-list-of-words-english.firebaseio.com/")

    @Singleton
    @Provides
    @Named("portugueseWordsDatabase")
    fun getAllPortugueseWords() = FirebaseDatabase.getInstance("https://the-bible-quiz-list-of-words-portuguese.firebaseio.com/")

    @Singleton
    @Provides
    @Named("suggestedQuestionsDatabase")
    fun getSuggestedQuestionsDatabase() = FirebaseDatabase.getInstance("https://the-bible-quiz-suggested-questions.firebaseio.com/")

    @Singleton
    @Provides
    @Named("leaguesDatabase")
    fun getLeaguesDatabase() = FirebaseDatabase.getInstance("https://the-bible-quiz-leagues.firebaseio.com/")

    @Singleton
    @Provides
    fun getFirebaseMessaging() = FirebaseMessaging.getInstance()

}