package com.bsoftwares.thebiblequiz.ui.screens.games.quiz

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.bsoftwares.thebiblequiz.R
import com.bsoftwares.thebiblequiz.ui.theme.gray

@Composable
fun ClockTimer(modifier: Modifier, time: String, progress: Float) {

    Box(modifier = modifier) {
        CircularProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxSize(),
            color = gray,
            strokeWidth = 15.dp,
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .clip(CircleShape)
                .background(colorResource(id = R.color.contrast_color))
        ) {
            AnimatedCounter(modifier = Modifier.align(Alignment.Center), count = time)
        }
    }

}