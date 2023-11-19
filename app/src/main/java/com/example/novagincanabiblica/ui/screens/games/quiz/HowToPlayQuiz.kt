package com.example.novagincanabiblica.ui.screens.games.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.novagincanabiblica.ui.basicviews.BasicContainer
import com.example.novagincanabiblica.ui.basicviews.BasicText
import com.example.novagincanabiblica.ui.theme.NovaGincanaBiblicaTheme

@Composable
fun HowToPlayQuizDialog() {
    BasicContainer {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            BasicText(text = "How to play The Bible Quiz?", fontSize = 22)
            BasicText(text = "In this game, you have 30 seconds to answer one question. You can choose 1 out of 4 answers. There are 4 difficulties in this game. They are:\n\n- Easy\n- Medium\n- Hard\n- Impossible\n\nIt is only one questions per day. Answer it wisely.")
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