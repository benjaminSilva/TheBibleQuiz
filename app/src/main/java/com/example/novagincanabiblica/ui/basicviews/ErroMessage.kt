package com.example.novagincanabiblica.ui.basicviews

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.novagincanabiblica.R
import com.example.novagincanabiblica.ui.theme.NovaGincanaBiblicaTheme
import com.example.novagincanabiblica.ui.theme.almostWhite
import kotlinx.coroutines.delay

@Composable
fun ErrorMessage(modifier: Modifier = Modifier, errorMessage: String) {

    var startAnimation by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(errorMessage) {
        startAnimation = false
        delay(3000)
        startAnimation = true
    }

    val alphaAnimation by animateAlpha(condition = startAnimation, delay = 0)
    val animateScale by animateScaleBouncy(condition = startAnimation, startValue = .0f)

    Row(
        modifier = modifier
            .shadowWithAnimation(
                elevation = 20.dp,
                alpha = alphaAnimation,
                scaleX = animateScale,
                scaleY = animateScale
            )
            .clip(RoundedCornerShape(16.dp))
            .background(almostWhite)
            .padding(16.dp)
            .alpha(alphaAnimation)
            .scale(animateScale),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Image(
            modifier = Modifier
                .size(24.dp),
            painter = painterResource(id = R.drawable.baseline_error_outline_24),
            contentDescription = null
        )
        BasicText(
            modifier = Modifier.align(Alignment.CenterVertically),
            text = errorMessage, fontSize = 16
        )
    }


}

@Preview(showBackground = true)
@Composable
fun PreviewErrorMessage() {
    NovaGincanaBiblicaTheme {
        ErrorMessage(
            errorMessage = "Not a valid word"
        )
    }
}