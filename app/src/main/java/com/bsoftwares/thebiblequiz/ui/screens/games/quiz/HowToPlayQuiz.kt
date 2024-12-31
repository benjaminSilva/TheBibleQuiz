package com.bsoftwares.thebiblequiz.ui.screens.games.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicContainer
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicText

@Composable
fun HowToPlayDialog(gameMode: String, rules: String) {
    BasicContainer {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            BasicText(text = gameMode, fontSize = 28)
            BasicText(text = rules)
        }
    }
}