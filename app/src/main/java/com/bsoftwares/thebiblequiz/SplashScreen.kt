package com.bsoftwares.thebiblequiz

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.bsoftwares.thebiblequiz.ui.theme.destination
import com.bsoftwares.thebiblequiz.ui.theme.emptyString
import com.bsoftwares.thebiblequiz.ui.theme.initialValue
import com.bsoftwares.thebiblequiz.viewmodel.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashActivity: ComponentActivity() {

    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            val splashScreen = installSplashScreen()
            splashScreen.setKeepOnScreenCondition { true }
        }
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            viewModel.signedInUserId.collectLatest {
                val dest = when(it) {
                    initialValue -> {
                        return@collectLatest
                    }
                    emptyString -> "login"
                    else -> "home"
                }
                val intent = Intent(this@SplashActivity, MainActivity::class.java).apply {
                    putExtra(destination, dest)
                }
                startActivity(intent)
                finish()
            }
        }
    }
}