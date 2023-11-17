package com.example.novagincanabiblica

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.novagincanabiblica.ui.navigation.SetupNavGraph
import com.example.novagincanabiblica.ui.theme.NovaGincanaBiblicaTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        lifecycleScope.launch {
            val token = Firebase.messaging.token.await()
            Log.d("FCM token:", token)
        }

        setContent {
            NovaGincanaBiblicaTheme {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    RequestNotificationPermissionDialog()
                }
                // A surface container using the 'background' color from the theme
                navController = rememberNavController()
                SetupNavGraph(navController = navController)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    fun RequestNotificationPermissionDialog() {
        val permissionState =
            rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)

        if (!permissionState.status.isGranted) {

        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NovaGincanaBiblicaTheme {
        SetupNavGraph(navController = rememberNavController())
    }
}