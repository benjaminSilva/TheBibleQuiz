package com.example.novagincanabiblica.ui.screens.league

import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.novagincanabiblica.R
import com.example.novagincanabiblica.data.models.League
import com.example.novagincanabiblica.data.models.LeagueDuration
import com.example.novagincanabiblica.data.models.LeagueRule
import com.example.novagincanabiblica.data.models.getString
import com.example.novagincanabiblica.data.models.state.getPainter
import com.example.novagincanabiblica.ui.basicviews.BasicContainer
import com.example.novagincanabiblica.ui.basicviews.BasicEditText
import com.example.novagincanabiblica.ui.basicviews.BasicText
import com.example.novagincanabiblica.ui.screens.games.quiz.screens.BasicRadioButton
import com.example.novagincanabiblica.ui.theme.NovaGincanaBiblicaTheme
import com.example.novagincanabiblica.viewmodel.HomeViewModel

@Composable
fun InitializeLeagueEditScreen(navController: NavController, viewModel: HomeViewModel) {
    val league by viewModel.currentLeague.collectAsStateWithLifecycle()

    EditLeagueScreen(league = league)
}

@Composable
fun EditLeagueScreen(league: League) {

    var editText by remember {
        mutableStateOf(league.leagueName)
    }

    Box (modifier = Modifier.fillMaxSize()) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            BasicContainer {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Image(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .align(Alignment.CenterVertically),
                        painter = league.leagueIcon.getPainter(),
                        contentDescription = null
                    )
                    BasicEditText(text = editText, updateText = { editText = it })
                }
            }
            BasicText(text = "League Reseting Cycle", fontSize = 22)
            LeagueDurationView(league = league)
            BasicText(text = "League Points", fontSize = 22)
            LeaguePoints(league = league)
            BasicText(text = "Date for Recycle: ${league.endCycleString}", fontSize = 22)
        }
        BasicContainer (modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(16.dp)) {
            Row (modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Image(painter = painterResource(id = R.drawable.baseline_save_alt_24), contentDescription = null)
            }
            BasicText(text = "Save")
        }
    }

}

@Composable
fun LeagueDurationView(league: League) {

    val listOfLeagueDurationOptions = listOf(
        LeagueDuration.WEEKLY,
        LeagueDuration.TWO_WEEKS,
        LeagueDuration.MONTHLY,
        LeagueDuration.THREE_MONTHS,
        LeagueDuration.SIX_MONTHS,
        LeagueDuration.YEARLY,
        LeagueDuration.NO_END,
    )

    val (selectedOption, onOptionSelected) = remember {
        mutableStateOf(
            league.leagueDuration
        )
    }

    Row(modifier =Modifier.horizontalScroll(rememberScrollState())) {
        listOfLeagueDurationOptions.onEach {
            BasicRadioButton(selected = it == selectedOption, updateRadioButton = { onOptionSelected(it) }) {
                BasicText(modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.Center), text = it.getString())
            }
        }
    }
}

@Composable
fun LeaguePoints(league: League) {

    val listOfLeagueDurationOptions = listOf(
        LeagueRule.QUIZ_AND_WORDLE,
        LeagueRule.QUIZ_ONLY,
        LeagueRule.WORDLE_ONLY
    )

    val (selectedOption, onOptionSelected) = remember {
        mutableStateOf(
            league.leagueRule
        )
    }

    Row(modifier =Modifier.horizontalScroll(rememberScrollState())) {
        listOfLeagueDurationOptions.onEach {
            BasicRadioButton(selected = it == selectedOption, updateRadioButton = { onOptionSelected(it) }) {
                BasicText(modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.Center), text = it.getString())
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewLeagueEdit() {
    NovaGincanaBiblicaTheme {
        EditLeagueScreen(League())
    }
}