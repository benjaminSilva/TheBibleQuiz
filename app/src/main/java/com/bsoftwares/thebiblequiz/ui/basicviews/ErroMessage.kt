package com.bsoftwares.thebiblequiz.ui.basicviews

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bsoftwares.thebiblequiz.R
import com.bsoftwares.thebiblequiz.data.models.state.FeedbackMessage
import com.bsoftwares.thebiblequiz.ui.theme.NovaGincanaBiblicaTheme
import com.bsoftwares.thebiblequiz.ui.theme.almostBlack
import kotlinx.coroutines.delay

@Composable
fun FeedbackMessageContainer(
    modifier: Modifier = Modifier,
    errorMessage: FeedbackMessage,
    isItError: Boolean = true
) {

    var startAnimation by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(errorMessage) {
        startAnimation = false
        delay(3000)
        startAnimation = true
    }

    val alphaAnimation by animateAlpha(condition = startAnimation, delay = 200, duration = 500, endEasing = LinearOutSlowInEasing)
    val animateScale by animateAlpha(condition = startAnimation, delay = 0, duration = 500)

    BasicContainer(
        modifier = modifier
            .alpha(alphaAnimation)
            .scale(animateScale),
        backGroundColor = almostBlack
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.Center),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.CenterVertically),
                painter = painterResource(id = if (isItError) R.drawable.baseline_error_outline_24 else R.drawable.baseline_check_24),
                contentDescription = null
            )
            BasicText(
                modifier = Modifier.align(Alignment.CenterVertically),
                text = errorMessage.get(), fontSize = 16
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewErrorMessage() {
    NovaGincanaBiblicaTheme {
        FeedbackMessageContainer(
            errorMessage = FeedbackMessage.FriendRemoved
        )
    }
}