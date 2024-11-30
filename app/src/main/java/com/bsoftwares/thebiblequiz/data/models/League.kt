package com.bsoftwares.thebiblequiz.data.models

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.bsoftwares.thebiblequiz.R
import com.bsoftwares.thebiblequiz.data.models.state.LeagueImages
import com.google.firebase.database.Exclude

data class League(
    val leagueId: String = "",
    val leagueName: String = "Your new League",
    val leagueIcon: LeagueImages = LeagueImages.SHIELD_CROSS,
    val leagueRule: LeagueRule = LeagueRule.QUIZ_AND_WORDLE,
    val leagueDuration: LeagueDuration = LeagueDuration.NO_END,
    val firstPlace: SessionInLeague = SessionInLeague(),
    val startCycleDate: Long = 0,
    val listOfLogs: List<LeagueLog> = listOf(),
    val endCycleDate: Long = 0,
    @Exclude
    val endCycleString: String = "",
    @Exclude
    val listOfUsers: List<SessionInLeague> = listOf(),
)

data class LeagueLog(
    val title: String = "",
    val message: String = "",
    //val icon: LeagueLogIcon = LeagueLogIcon.CONFIG_CHANGE
)

enum class LeagueLogIcon {
    CONFIG_CHANGE,
    CYCLE_WINNER
}

@Composable
fun LeagueLogIcon.toPaint(): Painter = painterResource(
    id = when (this) {
        LeagueLogIcon.CONFIG_CHANGE -> R.drawable.baseline_settings_24
        LeagueLogIcon.CYCLE_WINNER -> R.drawable.crown_svgrepo_com
    }
)

enum class LeagueRule {
    QUIZ_AND_WORDLE,
    QUIZ_ONLY,
    WORDLE_ONLY
}

@Composable
fun LeagueRule.getString(): String = stringResource(
    id = when (this) {
        LeagueRule.QUIZ_AND_WORDLE -> R.string.quiz_and_wordle
        LeagueRule.QUIZ_ONLY -> R.string.quiz_only
        LeagueRule.WORDLE_ONLY -> R.string.wordle_only
    }
)

enum class LeagueDuration {
    WEEKLY,
    TWO_WEEKS,
    MONTHLY,
    THREE_MONTHS,
    SIX_MONTHS,
    YEARLY,
    NO_END
}

@Composable
fun LeagueDuration.getString(): String = stringResource(
    id = when (this) {
        LeagueDuration.WEEKLY -> R.string.weekly
        LeagueDuration.TWO_WEEKS -> R.string.two_weeks
        LeagueDuration.MONTHLY -> R.string.monthly
        LeagueDuration.THREE_MONTHS -> R.string.three_months
        LeagueDuration.SIX_MONTHS -> R.string.six_months
        LeagueDuration.YEARLY -> R.string.yearly
        LeagueDuration.NO_END -> R.string.no_end
    })
