package com.bsoftwares.thebiblequiz.ui.screens.games

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.bsoftwares.thebiblequiz.data.models.state.DialogType
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicScreenBox
import com.bsoftwares.thebiblequiz.ui.navigation.navigateWithoutRemembering
import com.bsoftwares.thebiblequiz.ui.screens.Routes
import com.bsoftwares.thebiblequiz.ui.theme.prettyMuchBlack
import com.bsoftwares.thebiblequiz.viewmodel.BaseViewModel
import com.bsoftwares.thebiblequiz.viewmodel.WordleViewModel
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

@Composable
fun AdScreen(navHostController: NavHostController, baseViewModel: BaseViewModel) {

    val ad by baseViewModel.ad.collectAsStateWithLifecycle()
    val dialog by baseViewModel.displayDialog.collectAsStateWithLifecycle()

    val adRequest = AdRequest.Builder().build()

    val context = LocalContext.current

    val (destination, baseRoot) = when (baseViewModel) {
        is WordleViewModel -> Routes.WordleResults to Routes.WordleMode
        else -> Routes.QuizResults to Routes.QuizMode
    }

    BasicScreenBox(dialogType = dialog) {
        Box(modifier = Modifier.fillMaxSize().background(prettyMuchBlack))
    }


    LaunchedEffect(Unit) {
        baseViewModel.updateDialog(DialogType.Loading)
        InterstitialAd.load(
            context,
            "ca-app-pub-9654853503358559/1455072571",
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    navHostController.navigateWithoutRemembering(destination, baseRoot)
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    baseViewModel.updateAd(interstitialAd = interstitialAd)
                }
            })
    }

    LaunchedEffect(ad) {
        val activity = context.getActivityOrNull()
        ad?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdClicked() {

            }

            override fun onAdDismissedFullScreenContent() {
                navHostController.navigateWithoutRemembering(destination, baseRoot)
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.i("Log test", adError.message)
            }

            override fun onAdImpression() {
            }

            override fun onAdShowedFullScreenContent() {
                baseViewModel.updateDialog(DialogType.EmptyValue)
            }
        }
        if (activity != null) {
            ad?.show(activity)
            baseViewModel.updateDialog(DialogType.EmptyValue)
        } else {
            navHostController.navigateWithoutRemembering(destination, baseRoot)
            baseViewModel.updateDialog(DialogType.EmptyValue)
        }
    }

}

fun Context.getActivityOrNull(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}