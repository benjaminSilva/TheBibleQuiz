package com.example.novagincanabiblica.data.models.state

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.example.novagincanabiblica.R

enum class LeagueImages {
    SHIELD_CROSS
}

@Composable
fun LeagueImages.getPainter(): Painter = painterResource(id = when(this) {
    LeagueImages.SHIELD_CROSS -> R.drawable.shield_cross_outline_icon
} )

