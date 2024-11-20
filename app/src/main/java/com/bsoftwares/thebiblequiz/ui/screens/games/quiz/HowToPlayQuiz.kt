package com.bsoftwares.thebiblequiz.ui.screens.games.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bsoftwares.thebiblequiz.R
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicContainer
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicText
import com.bsoftwares.thebiblequiz.ui.theme.NovaGincanaBiblicaTheme

@Composable
fun HowToPlayQuizDialog() {
    BasicContainer {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            BasicText(text = stringResource(R.string.how_to_play_the_bible_quiz), fontSize = 28)
            BasicText(text = stringResource(R.string.bible_quiz_description))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHowToPlayDialog() {
    NovaGincanaBiblicaTheme {
        HowToPlayQuizDialog()
    }
}