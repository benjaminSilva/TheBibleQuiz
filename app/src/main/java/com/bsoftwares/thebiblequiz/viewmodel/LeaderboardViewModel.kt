package com.bsoftwares.thebiblequiz.viewmodel

import com.bsoftwares.thebiblequiz.data.models.LeaderboardType
import com.bsoftwares.thebiblequiz.data.models.RankingData
import com.bsoftwares.thebiblequiz.data.repositories.BaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    val repo: BaseRepository
) : BaseViewModel(repo) {

    val allQuiz = mutableListOf<RankingData>()
    val allWordle = mutableListOf<RankingData>()
    val allQuizWordle = mutableListOf<RankingData>()
    val monthQuiz = mutableListOf<RankingData>()
    val monthWordle = mutableListOf<RankingData>()
    val monthQuizWordle = mutableListOf<RankingData>()

    private var _rankingData = MutableStateFlow<List<RankingData>>(listOf())
    val rankingData = _rankingData.asStateFlow()

    private val _transitionAnimation = MutableStateFlow(true)
    val transitionAnimation = _transitionAnimation.asStateFlow()

    init {
        updateLeaderboard()
    }

    fun updateLeaderboard(leaderType: LeaderboardType = LeaderboardType.ALL_TIME_QUIZ_WORDLE) = backGroundScope.launch {
        delay(250)
        when (leaderType) {
            LeaderboardType.MONTHLY_QUIZ -> if (monthQuiz.isNotEmpty()) {
                _rankingData.emit(monthQuiz)
                return@launch
            }
            LeaderboardType.MONTHLY_WORDLE -> if (monthWordle.isNotEmpty()) {
                _rankingData.emit(monthWordle)
                return@launch
            }
            LeaderboardType.MONTHLY_QUIZ_WORDLE ->if (monthQuizWordle.isNotEmpty()) {
                _rankingData.emit(monthQuizWordle)
                return@launch
            }
            LeaderboardType.ALL_TIME_QUIZ -> if (allQuiz.isNotEmpty()) {
                _rankingData.emit(allQuiz)
                return@launch
            }
            LeaderboardType.ALL_TIME_WORDLE ->if (allWordle.isNotEmpty()) {
                _rankingData.emit(allWordle)
                return@launch
            }
            LeaderboardType.ALL_TIME_QUIZ_WORDLE -> if (allQuizWordle.isNotEmpty()) {
                _rankingData.emit(allQuizWordle)
                return@launch
            }
        }

        repo.loadLeaderboards(leaderType).collectLatest {
            it.handleSuccessAndFailure { list ->
                when (leaderType) {
                    LeaderboardType.MONTHLY_QUIZ -> monthQuiz.addAll(list)
                    LeaderboardType.MONTHLY_WORDLE -> monthWordle.addAll(list)
                    LeaderboardType.MONTHLY_QUIZ_WORDLE -> monthQuizWordle.addAll(list)
                    LeaderboardType.ALL_TIME_QUIZ -> allQuiz.addAll(list)
                    LeaderboardType.ALL_TIME_WORDLE -> allWordle.addAll(list)
                    LeaderboardType.ALL_TIME_QUIZ_WORDLE -> allQuizWordle.addAll(list)
                }
                _rankingData.emit(list)
            }
        }
    }

    fun finishAnimation() = backGroundScope.launch {
        _transitionAnimation.emit(false)
    }

}