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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bsoftwares.thebiblequiz.ui.navigation.SetupNavGraph
import com.bsoftwares.thebiblequiz.ui.theme.destination
import com.bsoftwares.thebiblequiz.viewmodel.HomeViewModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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

        val backgroundScope = CoroutineScope(Dispatchers.IO)
        backgroundScope.launch {
            // Initialize the Google Mobile Ads SDK on a background thread.
            MobileAds.initialize(this@MainActivity) {}
        }

        val destination = intent.getStringExtra(destination)
        val navigateToLogin = destination == "login"

        val adRequest = AdRequest.Builder().build()

        setContent {
            val homeViewModel = hiltViewModel<HomeViewModel>()
            val localSession by homeViewModel.localSession.collectAsStateWithLifecycle()

            Box(modifier = Modifier.background(colorResource(id = R.color.background_color))) {
                navController = rememberNavController()
                if (localSession.premium || localSession.userInfo.userId.isEmpty()) {
                    SetupNavGraph(navController = navController ,homeViewModel, navigateToLogin)
                } else {
                    Column (modifier = Modifier.fillMaxSize()) {
                        Box(modifier = Modifier.weight(0.9f)) {
                            SetupNavGraph(navController = navController, homeViewModel, navigateToLogin)
                        }
                        AndroidView(modifier = Modifier.weight(.1f).fillMaxSize(), factory = { context ->
                            AdView(context).apply {
                                setAdSize(AdSize.BANNER)
                                adUnitId = "ca-app-pub-3940256099942544/9214589741"
                                loadAd(adRequest)
                            }
                        })
                    }
                }
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