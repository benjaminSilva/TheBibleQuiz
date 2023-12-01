package com.bsoftwares.thebiblequiz.ui.screens.league

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.bsoftwares.thebiblequiz.R
import com.bsoftwares.thebiblequiz.data.models.League
import com.bsoftwares.thebiblequiz.data.models.LeagueDuration
import com.bsoftwares.thebiblequiz.data.models.LeagueRule
import com.bsoftwares.thebiblequiz.data.models.SessionInLeague
import com.bsoftwares.thebiblequiz.data.models.getString
import com.bsoftwares.thebiblequiz.data.models.state.DialogType
import com.bsoftwares.thebiblequiz.data.models.state.EditLeagueDialog
import com.bsoftwares.thebiblequiz.data.models.state.LeagueImages
import com.bsoftwares.thebiblequiz.data.models.state.getPainter
import com.bsoftwares.thebiblequiz.data.models.state.getString
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicContainer
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicDialog
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicEditText
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicScreenBox
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicText
import com.bsoftwares.thebiblequiz.ui.screens.games.quiz.screens.BasicRadioButton
import com.bsoftwares.thebiblequiz.ui.theme.NovaGincanaBiblicaTheme
import com.bsoftwares.thebiblequiz.ui.theme.gray
import com.bsoftwares.thebiblequiz.ui.theme.lessWhite
import com.bsoftwares.thebiblequiz.viewmodel.HomeViewModel

@Composable
fun InitializeLeagueEditScreen(navController: NavController, viewModel: HomeViewModel) {
    val league by viewModel.currentLeague.collectAsStateWithLifecycle()
    val sessionInLeague by viewModel.sessionInLeague.collectAsStateWithLifecycle()
    val dialog by viewModel.displayDialog.collectAsStateWithLifecycle()
    val feedbackMessage by viewModel.feedbackMessage.collectAsStateWithLifecycle()

    var displayDialog by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(dialog) {
        if (dialog != DialogType.EmptyValue) {
            displayDialog = true
        }
    }

    if (displayDialog) {
        when (dialog) {
            is DialogType.Loading -> {
                DialogType.Loading.generate(modifier = Modifier.fillMaxSize())
            }

            is EditLeagueDialog.ConfirmSave -> {
                BasicDialog(onDismissRequest = {
                    viewModel.updateDialog()
                }) {
                    ConfirmSave(deny = {
                        viewModel.updateDialog()
                    }) {
                        viewModel.updateLeague(
                            league = (dialog as EditLeagueDialog.ConfirmSave).getLeague(),
                            updateTime = true
                        )
                    }
                }
            }

            is EditLeagueDialog.SelectNewIcon -> {
                BasicDialog(onDismissRequest = {
                    viewModel.updateDialog()
                }) {
                    SelectNewIcon(league = league) {
                        viewModel.updateLeague(league.copy(leagueIcon = it), justIcon = true)
                    }
                }
            }

            else -> Unit
        }
    }

    BasicScreenBox(feedbackMessage = feedbackMessage) {
        EditLeagueScreen(league = league, sessionInLeague = sessionInLeague, createDialog = {
            viewModel.updateDialog(it)
        }) { updatedLeague, updateCycle ->
            viewModel.updateLeague(league = updatedLeague, updateTime = updateCycle)
        }
    }
}

@Composable
fun EditLeagueScreen(
    league: League,
    sessionInLeague: SessionInLeague,
    createDialog: (DialogType) -> Unit,
    updateLeague: (League, Boolean) -> Unit
) {

    var leagueName by remember {
        mutableStateOf(league.leagueName)
    }

    var leagueDuration by remember {
        mutableStateOf(league.leagueDuration)
    }

    var leagueRule by remember {
        mutableStateOf(league.leagueRule)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            BasicText(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = "League Settings:",
                fontSize = 32
            )
            BasicContainer(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(modifier = Modifier.align(Alignment.CenterVertically)) {
                        BasicContainer(
                            modifier = Modifier
                                .padding(8.dp),onClick = {
                                if (sessionInLeague.adminUser) {
                                    createDialog(EditLeagueDialog.SelectNewIcon)
                                }
                            }
                        ) {
                            Image(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .size(48.dp),
                                painter = league.leagueIcon.getPainter(),
                                contentDescription = null
                            )
                        }
                        if (sessionInLeague.adminUser) {
                            Image(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(gray)
                                    .padding(2.dp),
                                painter = painterResource(id = R.drawable.baseline_edit_24),
                                contentDescription = null
                            )
                        }
                    }
                    if (sessionInLeague.adminUser) {
                        BasicEditText(
                            modifier = Modifier.align(Alignment.CenterVertically),
                            text = leagueName,
                            updateText = { leagueName = it })
                    } else {
                        BasicText(
                            modifier = Modifier.align(Alignment.CenterVertically),
                            text = league.leagueName,
                            fontSize = 22
                        )
                    }
                }
            }
            BasicText(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = "League Resetting Cycle:",
                fontSize = 22
            )
            if (sessionInLeague.adminUser) {
                LeagueDurationView(league = league) {
                    leagueDuration = it
                }
            } else {
                BasicText(
                    Modifier.padding(horizontal = 16.dp),
                    text = league.leagueDuration.getString()
                )
            }
            BasicText(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = "League Points:",
                fontSize = 22
            )
            if (sessionInLeague.adminUser) {
                LeaguePoints(league = league) {
                    leagueRule = it
                }
            } else {
                BasicText(
                    Modifier.padding(horizontal = 16.dp),
                    text = league.leagueRule.getString()
                )
            }
            BasicText(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = "Date for Recycle: ${league.endCycleString}",
                fontSize = 22
            )
        }
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (sessionInLeague.adminUser) {
                BasicContainer {
                    Image(
                        modifier = Modifier.padding(16.dp),
                        painter = painterResource(id = R.drawable.baseline_delete_24),
                        contentDescription = null
                    )
                }
            }
            BasicContainer {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.baseline_format_list_bulleted_24),
                        contentDescription = null
                    )
                    BasicText(modifier = Modifier.align(Alignment.CenterVertically), text = "Logs")
                }
            }
            if (sessionInLeague.adminUser) {
                BasicContainer(onClick = {
                    if (league.leagueRule != leagueRule || league.leagueDuration != leagueDuration) {
                        createDialog(
                            EditLeagueDialog.ConfirmSave(
                                league.copy(
                                    leagueName = leagueName,
                                    leagueRule = leagueRule,
                                    leagueDuration = leagueDuration
                                )
                            )
                        )
                    } else {
                        updateLeague(
                            league.copy(
                                leagueName = leagueName
                            ),
                            false
                        )
                    }

                }) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.baseline_save_alt_24),
                            contentDescription = null
                        )
                        BasicText(
                            modifier = Modifier.align(Alignment.CenterVertically),
                            text = "Save"
                        )
                    }
                }
            }
        }
    }

}

@Composable
fun LeagueDurationView(league: League, updateDuration: (LeagueDuration) -> Unit) {

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

    Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
        Spacer(modifier = Modifier.width(16.dp))
        listOfLeagueDurationOptions.onEach {
            BasicRadioButton(selected = it == selectedOption, updateRadioButton = {
                onOptionSelected(it)
                updateDuration(it)
            }) {
                BasicText(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.Center), text = it.getString()
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
    }
}

@Composable
fun LeaguePoints(league: League, updateRule: (LeagueRule) -> Unit) {

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

    Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
        Spacer(modifier = Modifier.width(16.dp))
        listOfLeagueDurationOptions.onEach {
            BasicRadioButton(
                selected = it == selectedOption,
                updateRadioButton = {
                    onOptionSelected(it)
                    updateRule(it)
                }) {
                BasicText(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.Center), text = it.getString()
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
    }
}

@Composable
fun ConfirmSave(deny: () -> Unit, confirm: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        BasicContainer {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                BasicText(text = "Are you sure you want this change?", fontSize = 22)
                BasicText(text = "This will reset the points of all the participants of this League and a new end cycle data will be set.")
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BasicContainer(modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
                onClick = {
                    deny()
                }
            ) {

                Row(
                    modifier = Modifier
                        .padding(16.dp)
                ) {

                    Image(
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.CenterVertically),
                        painter = painterResource(id = R.drawable.baseline_close_24),
                        contentDescription = null
                    )

                    BasicText(
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 8.dp),
                        text = "Better Not",
                    )
                }
            }

            BasicContainer(modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
                onClick = {
                    confirm()
                }
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                ) {

                    Image(
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.CenterVertically),
                        painter = painterResource(id = R.drawable.baseline_check_24),
                        contentDescription = null
                    )

                    BasicText(
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 8.dp),
                        text = "Yes",
                    )
                }
            }
        }
    }
}

@Composable
fun SelectNewIcon(league: League, updateLeagueIcon: (LeagueImages) -> Unit) {

    val listOfIcons = listOf(
        LeagueImages.SHIELD_CROSS,
        LeagueImages.FISH,
        LeagueImages.THE_REDEEMER,
        LeagueImages.CHURCH,
        LeagueImages.BE_NOT_AFRAID
    )

    val (selectedOption, onOptionSelected) = remember {
        mutableStateOf(
            league.leagueIcon
        )
    }

    Column (verticalArrangement = Arrangement.spacedBy(16.dp)) {
        BasicContainer {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                BasicText(text = "Select your icon", fontSize = 22)
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 56.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(listOfIcons) {
                        BasicRadioButton(
                            selected = it == selectedOption,
                            updateRadioButton = {
                                onOptionSelected(it)
                            }) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .align(Alignment.Center)
                                    .background(gray)
                                    .padding(8.dp)
                            ) {
                                Image(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .align(Alignment.Center),
                                    painter = it.getPainter(),
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }
                BasicText(modifier = Modifier.align(Alignment.CenterHorizontally), text = selectedOption.getString(), fontSize = 22)
            }
        }
        BasicContainer(modifier = Modifier
            .fillMaxWidth(.5f)
            .align(Alignment.End),
            onClick = {
                updateLeagueIcon(selectedOption)
            }
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
            ) {

                Image(
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.CenterVertically),
                    painter = painterResource(id = R.drawable.baseline_check_24),
                    contentDescription = null
                )

                BasicText(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(start = 8.dp),
                    text = "Change it",
                )
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun PreviewLeagueEdit() {
    NovaGincanaBiblicaTheme {
        EditLeagueScreen(League(), SessionInLeague(adminUser = true), {}) { _, _ ->

        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLeagueEditNonAdmin() {
    NovaGincanaBiblicaTheme {
        EditLeagueScreen(League(), SessionInLeague(adminUser = false), {}) { _, _ ->

        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLeagueIconDialog() {
    NovaGincanaBiblicaTheme {
        SelectNewIcon(league = League(), updateLeagueIcon = {})
    }
}