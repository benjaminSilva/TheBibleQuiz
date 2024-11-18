package com.bsoftwares.thebiblequiz.ui.basicviews

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bsoftwares.thebiblequiz.data.models.state.FeedbackMessage

@Composable
fun BasicScreenBox(feedbackMessage: FeedbackMessage = FeedbackMessage.NoMessage, condition: Boolean = true, content: @Composable BoxScope.() -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        content()
        Log.i("Feedback message", feedbackMessage.messageText)
        if (feedbackMessage != FeedbackMessage.NoMessage && condition) {
            FeedbackMessageContainer(modifier = Modifier.align(Alignment.TopCenter).padding(16.dp), errorMessage = feedbackMessage)
        }
    }
}