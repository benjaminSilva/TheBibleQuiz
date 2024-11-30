package com.bsoftwares.thebiblequiz.ui.screens.league

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.bsoftwares.thebiblequiz.R
import com.bsoftwares.thebiblequiz.data.models.League
import com.bsoftwares.thebiblequiz.data.models.LeagueDuration
import com.bsoftwares.thebiblequiz.data.models.LeagueRule
import com.bsoftwares.thebiblequiz.data.models.SessionInLeague
import com.bsoftwares.thebiblequiz.data.models.getString
import com.bsoftwares.thebiblequiz.data.models.state.DialogType
import com.bsoftwares.thebiblequiz.data.models.state.EditLeagueDialog
import com.bsoftwares.thebiblequiz.data.models.state.FeedbackMessage
import com.bsoftwares.thebiblequiz.data.models.state.LeagueImages
import com.bsoftwares.thebiblequiz.data.models.state.getPainter
import com.bsoftwares.thebiblequiz.data.models.state.getString
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicContainer
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicDialog
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicEditText
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicPositiveNegativeDialog
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicScreenBox
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicText
import com.bsoftwares.thebiblequiz.ui.screens.Routes
import com.bsoftwares.thebiblequiz.ui.screens.games.quiz.screens.BasicRadioButton
import com.bsoftwares.thebiblequiz.ui.theme.NovaGincanaBiblicaTheme
import com.bsoftwares.thebiblequiz.ui.theme.appBackground
import com.bsoftwares.thebiblequiz.ui.theme.basicContainerClean
import com.bsoftwares.thebiblequiz.ui.theme.emptyString
import com.bsoftwares.thebiblequiz.viewmodel.HomeViewModel

@Composable
fun InitializeLeagueEditScreen(navController: NavHostController, viewModel: HomeViewModel) {
    val league by viewModel.currentLeague.collectAsStateWithLifecycle()
    val sessionInLeague by viewModel.sessionInLeague.collectAsStateWithLifecycle()
    val dialog by viewModel.displayDialog.collectAsStateWithLifecycle()
    val feedbackMessage by viewModel.feedbackMessage.collectAsStateWithLifecycle()
    val enabled by viewModel.clickable.collectAsStateWithLifecycle()

    LaunchedEffect(enabled) {
        if (!enabled.first) {
            enabled.second.invoke()
        }
    }

    var displayDialog by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(dialog) {
        if (dialog != DialogType.EmptyValue) {
            displayDialog = true
        }
    }

    LaunchedEffect(feedbackMessage) {
        if (feedbackMessage == FeedbackMessage.LeagueDeleted || feedbackMessage == FeedbackMessage.LeftLeagueSuccessfully) {
            navController.popBackStack(Routes.Profile.withParameter(sessionInLeague.userId), inclusive = false)
        }
    }

    if (displayDialog) {
        when (dialog) {
            is DialogType.Loading -> {
                DialogType.Loading.Generate(modifier = Modifier.fillMaxSize())
            }

            is EditLeagueDialog.ConfirmSave -> {

                BasicPositiveNegativeDialog(onDismissRequest = {
                    viewModel.updateDialog()
                }, positiveFunction = {
                    viewModel.updateLeague(
                        league = (dialog as EditLeagueDialog.ConfirmSave).getLeague(),
                        updateTime = true
                    )
                },
                    title = stringResource(R.string.league_changes),
                    description = stringResource(R.string.are_you_sure)
                )
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

            is EditLeagueDialog.DeleteLeague -> {
                BasicPositiveNegativeDialog(
                    onDismissRequest = {
                        viewModel.updateDialog()
                    },
                    dialogIcon = league.leagueIcon.getPainter(),
                    title = stringResource(R.string.delete_league),
                    description = stringResource(R.string.are_you_sure_you_want_to_delete_this_league),
                    positiveFunction = {
                        viewModel.deleteLeague(league)
                    })
            }

            is EditLeagueDialog.LeaveLeague -> {
                BasicPositiveNegativeDialog(
                    onDismissRequest = {
                        viewModel.updateDialog()
                    },
                    dialogIcon = league.leagueIcon.getPainter(),
                    title = stringResource(R.string.leave_league),
                    description = stringResource(R.string.are_you_sure_you_want_to_leave_this_league),
                    positiveFunction = {
                        viewModel.leaveLeague()
                    })
            }

            is EditLeagueDialog.HowItWorks -> {
                val extraTextForAdminOnly = stringResource(R.string.admin_extra_rules)
                BasicPositiveNegativeDialog(onDismissRequest = {
                    viewModel.updateDialog()
                }, title = stringResource(R.string.how_does_league_work),
                    description = stringResource(
                        R.string.league_explained,
                        if (sessionInLeague.adminUser) extraTextForAdminOnly else emptyString
                    ),
                    dialogIcon = null,
                    negativeString = null,
                    positiveString = "Got it")
            }

            else -> Unit
        }
    }

    BasicScreenBox(feedbackMessage = feedbackMessage, dialogType = dialog, enabled = enabled.first) {
        EditLeagueScreen(league = league, sessionInLeague = sessionInLeague, createDialog = {
            viewModel.updateDialog(it)
        }, deleteLeague = {
            viewModel.updateDialog(EditLeagueDialog.DeleteLeague)
        }, leaveLeague = {
            viewModel.updateDialog(EditLeagueDialog.LeaveLeague)
        }, goBack = {
            navController.popBackStack()
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
    deleteLeague: () -> Unit,
    leaveLeague: () -> Unit,
    goBack: () -> Unit,
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
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                BasicContainer(
                    modifier = Modifier
                        .align(Alignment.CenterVertically), onClick = goBack
                ) {
                    Image(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .size(24.dp)
                            .align(Alignment.Center),
                        painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                        contentDescription = null
                    )
                }
                BasicText(
                    modifier = Modifier,
                    text = stringResource(R.string.league_settings),
                    fontSize = 32
                )
            }

            BasicContainer {
                Row(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(modifier = Modifier.align(Alignment.CenterVertically)) {
                        BasicContainer(
                            modifier = Modifier
                                .padding(8.dp),
                            backGroundColor = appBackground(), onClick = {
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
                                    .background(basicContainerClean())
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
                text = stringResource(R.string.league_resetting_cycle),
                fontSize = 22
            )
            if (sessionInLeague.adminUser) {
                LeagueDurationView(league = league) {
                    leagueDuration = it
                }
            } else {
                BasicText(
                    text = league.leagueDuration.getString()
                )
            }
            BasicText(
                text = stringResource(R.string.league_points),
                fontSize = 22
            )
            if (sessionInLeague.adminUser) {
                LeaguePoints(league = league) {
                    leagueRule = it
                }
            } else {
                BasicText(
                    text = league.leagueRule.getString()
                )
            }
            BasicText(
                text = stringResource(R.string.date_for_recycle, league.endCycleString),
                fontSize = 22
            )
            BasicText(
                text = stringResource(
                    R.string.admin,
                    league.listOfUsers.find { it.adminUser }?.userName ?: emptyString
                ),
                fontSize = 22
            )
        }
        BasicContainer(modifier = Modifier
            .align(Alignment.BottomStart),onClick = {
            createDialog(EditLeagueDialog.HowItWorks)
        }) {
            Image(
                modifier = Modifier.padding(16.dp),
                painter = painterResource(id = R.drawable.question_mark_24dp),
                contentDescription = null
            )
        }
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BasicContainer(onClick = {
                if (sessionInLeague.adminUser) {
                    deleteLeague()
                } else {
                    leaveLeague()
                }
            }) {
                Image(
                    modifier = Modifier.padding(16.dp),
                    painter = painterResource(id = R.drawable.baseline_delete_24),
                    contentDescription = null
                )
            }
            /* To Be implemented after release
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
            }*/
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
                            text = stringResource(R.string.save)
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
        LeagueDuration.NO_END,
        LeagueDuration.WEEKLY,
        LeagueDuration.TWO_WEEKS,
        LeagueDuration.MONTHLY,
        LeagueDuration.THREE_MONTHS,
        LeagueDuration.SIX_MONTHS,
        LeagueDuration.YEARLY
    )

    val (selectedOption, onOptionSelected) = remember {
        mutableStateOf(
            league.leagueDuration
        )
    }

    BasicContainer {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 100.dp), // Dynamically fit items
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(listOfLeagueDurationOptions) { option ->
                BasicRadioButton(
                    modifier = Modifier.height(50.dp),
                    selected = option == selectedOption,
                    updateRadioButton = {
                        onOptionSelected(option)
                        updateDuration(option)
                    }
                ) {
                    BasicText(
                        modifier = Modifier
                            .padding(8.dp)
                            .align(Alignment.Center),
                        text = option.getString(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
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

    BasicContainer {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 120.dp), // Adjust minSize as needed
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(listOfLeagueDurationOptions) { option ->
                BasicRadioButton(
                    modifier = Modifier.height(50.dp),
                    selected = option == selectedOption,
                    updateRadioButton = {
                        onOptionSelected(option)
                        updateRule(option)
                    }
                ) {
                    BasicText(
                        modifier = Modifier
                            .padding(8.dp)
                            .align(Alignment.Center),
                        text = option.getString()
                    )
                }
            }
        }
    }
}

@Composable
fun SelectNewIcon(league: League, updateLeagueIcon: (LeagueImages) -> Unit) {

    val listOfIcons = LeagueImages.values()

    val (selectedOption, onOptionSelected) = remember {
        mutableStateOf(
            league.leagueIcon
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        BasicContainer {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                BasicText(text = stringResource(R.string.select_your_icon), fontSize = 22)
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
                                    .background(basicContainerClean())
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
                BasicText(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = selectedOption.getString(),
                    fontSize = 22
                )
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
                    text = stringResource(R.string.change_it),
                )
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun PreviewLeagueEdit() {
    NovaGincanaBiblicaTheme {
        EditLeagueScreen(
            League(endCycleString = "11/08/2024"),
            SessionInLeague(adminUser = true),
            {}, {}, {}, {}) { _, _ ->

        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLeagueEditNonAdmin() {
    NovaGincanaBiblicaTheme {
        EditLeagueScreen(League(), SessionInLeague(adminUser = false), {}, {}, {}, {}) { _, _ ->

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