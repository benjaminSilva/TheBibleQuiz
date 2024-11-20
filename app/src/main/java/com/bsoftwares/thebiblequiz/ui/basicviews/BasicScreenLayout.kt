package com.bsoftwares.thebiblequiz.ui.basicviews

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bsoftwares.thebiblequiz.data.models.state.FeedbackMessage
import com.bsoftwares.thebiblequiz.ui.theme.contrastColor
import com.bsoftwares.thebiblequiz.ui.theme.prettyMuchBlack

@Composable
fun BasicScreenBox(
    feedbackMessage: FeedbackMessage = FeedbackMessage.NoMessage,
    condition: Boolean = true,
    isLoading: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        content()
        Log.i("Feedback message", feedbackMessage.messageText)
        if (feedbackMessage != FeedbackMessage.NoMessage && condition) {
            FeedbackMessageContainer(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp),
                errorMessage = feedbackMessage
            )
        }
        if (isLoading) {
            Box(modifier = Modifier
                .fillMaxSize()
                .background(prettyMuchBlack.copy(alpha = 0.5f))) {
                CircularProgressIndicator(modifier = Modifier.size(36.dp).align(Alignment.Center), color = contrastColor())
            }

        }
    }
}