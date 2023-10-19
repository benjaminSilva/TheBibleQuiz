package com.example.novagincanabiblica.ui.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.novagincanabiblica.ui.screens.HomeScreen
import com.example.novagincanabiblica.ui.screens.PreSoloScreen
import com.example.novagincanabiblica.ui.screens.Routes

@Composable
fun SetupNavGraph(navController: NavHostController, context: Context) {
    NavHost(navController = navController, startDestination = Routes.START.value) {
        navigation(startDestination = Routes.Home.value, route = Routes.START.value) {
            composable(
                route = Routes.Home.value
            ) {
                HomeScreen(navController = navController)
            }
            composable(route = Routes.PreSoloScreen.value) {
                PreSoloScreen(navController = navController, context = context)
            }
        }
    }
}