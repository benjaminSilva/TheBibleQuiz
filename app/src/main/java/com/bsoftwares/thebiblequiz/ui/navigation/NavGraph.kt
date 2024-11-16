package com.bsoftwares.thebiblequiz.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.get
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import com.bsoftwares.thebiblequiz.ui.screens.profile.InitializeProfileScreen
import com.bsoftwares.thebiblequiz.ui.screens.Routes
import com.bsoftwares.thebiblequiz.ui.screens.games.quiz.screens.InitSuggestQuestionScreen
import com.bsoftwares.thebiblequiz.ui.screens.games.quiz.screens.InitializeSoloResultScreen
import com.bsoftwares.thebiblequiz.ui.screens.games.quiz.screens.InitializePreSoloScreen
import com.bsoftwares.thebiblequiz.ui.screens.games.quiz.screens.InitializeSoloQuestionScreen
import com.bsoftwares.thebiblequiz.ui.screens.games.wordle.InitializeWordleResult
import com.bsoftwares.thebiblequiz.ui.screens.games.wordle.InitializeWordleScreen
import com.bsoftwares.thebiblequiz.ui.screens.home.InitializeHomeScreen
import com.bsoftwares.thebiblequiz.ui.screens.league.InitializeLeagueEditScreen
import com.bsoftwares.thebiblequiz.ui.screens.league.InitializeLeagueScreen
import com.bsoftwares.thebiblequiz.viewmodel.HomeViewModel
import com.bsoftwares.thebiblequiz.viewmodel.BibleQuizViewModel
import com.bsoftwares.thebiblequiz.viewmodel.WordleViewModel

const val MY_URI = "https://profile-deeplink.com"
const val MY_ARG = "id"

@Composable
fun SetupNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Routes.Start.value, route = Routes.Root.value) {

        navigation(startDestination = Routes.Home.value, route = Routes.Start.value) {
            composable(
                route = Routes.Home.value
            ) {
                val homeViewModel = it.sharedViewModel<HomeViewModel>(navController = navController)
                InitializeHomeScreen(navController = navController, homeViewModel = homeViewModel)
            }

            composable(
                route = Routes.Profile.value,
                deepLinks = listOf(navDeepLink {
                    uriPattern = MY_URI
                })
            ) {
                val homeViewModel = it.sharedViewModel<HomeViewModel>(navController = navController)
                InitializeProfileScreen(navController = navController, homeViewModel = homeViewModel)
            }

            composable(
                route = Routes.LeagueScreen.value
            ) {
                val homeViewModel = it.sharedViewModel<HomeViewModel>(navController = navController)
                InitializeLeagueScreen(navController = navController, viewModel = homeViewModel)
            }

            composable(
                route = Routes.EditLeague.value
            ) {
                val homeViewModel = it.sharedViewModel<HomeViewModel>(navController = navController)
                InitializeLeagueEditScreen(navController = navController, viewModel = homeViewModel)
            }
        }

        navigation(startDestination = Routes.Wordle.value, route = Routes.WordleMode.value) {
            composable(
                route = Routes.Wordle.value
            ) {
                val wordleViewModel = it.sharedViewModel<WordleViewModel>(navController = navController)
                InitializeWordleScreen(navController = navController, viewModel = wordleViewModel)
            }

            composable(
                route = Routes.WordleResults.value
            ) {
                val wordleViewModel = it.sharedViewModel<WordleViewModel>(navController = navController)
                InitializeWordleResult(navController = navController, viewModel = wordleViewModel)
            }
        }

        navigation(
            startDestination = Routes.PreQuiz.value,
            route = Routes.QuizMode.value
        ) {
            composable(route = Routes.PreQuiz.value) {
                val soloViewModel = it.sharedViewModel<BibleQuizViewModel>(navController = navController)
                InitializePreSoloScreen(
                    navController = navController,
                    soloViewModel = soloViewModel
                )
            }
            composable(route = Routes.Quiz.value) {
                val soloViewModel =
                    it.sharedViewModel<BibleQuizViewModel>(navController = navController)
                InitializeSoloQuestionScreen(
                    navController = navController,
                    soloViewModel = soloViewModel
                )
            }
            composable(route = Routes.QuizResults.value) {
                val soloViewModel =
                    it.sharedViewModel<BibleQuizViewModel>(navController = navController)
                InitializeSoloResultScreen(
                    navController = navController,
                    soloViewModel = soloViewModel
                )
            }
            composable(route = Routes.SuggestQuestion.value) {
                val viewModel =
                    it.sharedViewModel<BibleQuizViewModel>(navController = navController)
                InitSuggestQuestionScreen(viewModel =  viewModel)
            }
        }
    }
}

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(navController: NavController): T {
    val navGraphRoute = destination.parent?.route ?: return hiltViewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return hiltViewModel(parentEntry)
}

fun NavHostController.navigateWithoutRemembering(route: Routes, baseRoute: Routes) {
    navigate(route = route.value) {
        popUpTo(graph[baseRoute.value].id)
    }
}