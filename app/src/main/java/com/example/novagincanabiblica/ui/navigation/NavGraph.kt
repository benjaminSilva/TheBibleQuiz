package com.example.novagincanabiblica.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.novagincanabiblica.ui.screens.HomeScreen
import com.example.novagincanabiblica.ui.screens.PreSoloScreen
import com.example.novagincanabiblica.ui.screens.Routes

@Composable
fun SetupNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Routes.Home.value) {
        composable(
            route = Routes.Home.value
        ) {
            HomeScreen(navController = navController)
        }
        composable(route = Routes.PreSoloScreen.value) {
            PreSoloScreen(navController = navController)
        }
    }
}