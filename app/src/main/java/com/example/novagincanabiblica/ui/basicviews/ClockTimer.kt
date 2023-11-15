package com.example.novagincanabiblica.ui.basicviews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.novagincanabiblica.ui.theme.almostWhite

@Composable
fun ClockTimer(modifier: Modifier, time: String, progress: Float) {

    Box(modifier = modifier) {
        CircularProgressIndicator(
            modifier = Modifier.fillMaxSize(),
            progress = progress,
            strokeWidth = 15.dp
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .clip(CircleShape)
                .background(almostWhite)
        ) {
            AnimatedCounter(modifier = Modifier.align(Alignment.Center), count = time)
        }
    }

}