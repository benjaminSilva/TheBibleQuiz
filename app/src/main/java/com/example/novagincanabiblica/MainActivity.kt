package com.example.novagincanabiblica

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.novagincanabiblica.ui.screens.HomeScreen
import com.example.novagincanabiblica.ui.theme.NovaGincanaBiblicaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NovaGincanaBiblicaTheme {
                // A surface container using the 'background' color from the theme
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "home") {
                    navigation(startDestination = "home", route = "solo_mode") {
                        composable("home") {
                            HomeScreen()
                        }
                        composable("solo_mode") {

                        }
                    }
                }
            }
        }
    }
}

/*@Composable
inline fun<reified T : ViewModel> NavBackStackEntry.sharedViewModel(navController: NavController): T {
    val navGraphRoute = destination.parent?.route ?: return viewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return viewModel(parentEntry)
}*/

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NovaGincanaBiblicaTheme {
        HomeScreen()
    }
}