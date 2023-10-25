package com.example.novagincanabiblica.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.novagincanabiblica.ui.screens.Routes
import com.example.novagincanabiblica.ui.screens.gamemodes.InitializeSoloResultScreen
import com.example.novagincanabiblica.ui.screens.home.HomeScreen
import com.example.novagincanabiblica.ui.screens.gamemodes.solomode.InitializePreSoloScreen
import com.example.novagincanabiblica.ui.screens.gamemodes.solomode.InitializeSoloQuestionScreen
import com.example.novagincanabiblica.viewmodel.SoloModeViewModel

@Composable
fun SetupNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Routes.Start.value) {
        navigation(startDestination = Routes.Home.value, route = Routes.Start.value) {
            composable(
                route = Routes.Home.value
            ) {
                HomeScreen(navController = navController)
            }
        }

        navigation(startDestination = Routes.SoloModePreQuestion.value, route = Routes.SoloMode.value) {
            composable(route = Routes.SoloModePreQuestion.value) {
                val soloViewModel = it.sharedViewModel<SoloModeViewModel>(navController = navController)
                InitializePreSoloScreen(
                    navController = navController,
                    soloViewModel = soloViewModel
                )
            }
            composable(route = Routes.SoloModeQuestion.value) {
                val soloViewModel =
                    it.sharedViewModel<SoloModeViewModel>(navController = navController)
                InitializeSoloQuestionScreen(navController = navController, soloViewModel)
            }
            composable(route = Routes.Results.value) {
                val soloViewModel = it.sharedViewModel<SoloModeViewModel>(navController = navController)
                InitializeSoloResultScreen(navController = navController, soloViewModel)
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

fun NavHostController.navigateWithoutRemembering(route: Routes) {
    navigate(route.value) {
        popUpTo(route.value) {
            inclusive = true
        }
    }
}