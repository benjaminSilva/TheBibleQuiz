package com.bsoftwares.thebiblequiz

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bsoftwares.thebiblequiz.ui.navigation.SetupNavGraph
import com.bsoftwares.thebiblequiz.ui.theme.NovaGincanaBiblicaTheme
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

        val launcher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkAndRequestNotificationPermission(launcher)
        }

        setContent {
            // A surface container using the 'background' color from the theme
            Box(modifier = Modifier.background(colorResource(id = R.color.background_color))) {
                navController = rememberNavController()
                SetupNavGraph(navController = navController)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkAndRequestNotificationPermission(
        launcher: ActivityResultLauncher<String>
    ) {
        val permissionCheckResult =
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
        if (permissionCheckResult != PackageManager.PERMISSION_GRANTED) {
            // Request a permission
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
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