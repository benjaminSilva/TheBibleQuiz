package com.bsoftwares.thebiblequiz.data.models.quiz

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.bsoftwares.thebiblequiz.R

enum class QuestionDifficulty {
    EASY,
    MEDIUM,
    HARD,
    IMPOSSIBLE
}

@Composable
fun QuestionDifficulty.getName(): String = stringResource(
    when (this) {
        QuestionDifficulty.EASY -> R.string.easy
        QuestionDifficulty.MEDIUM -> R.string.medium
        QuestionDifficulty.HARD -> R.string.hard
        QuestionDifficulty.IMPOSSIBLE -> R.string.impossible
    }
)