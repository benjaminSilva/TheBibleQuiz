package com.bsoftwares.thebiblequiz.ui.basicviews

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.bsoftwares.thebiblequiz.data.models.state.DialogType
import com.bsoftwares.thebiblequiz.data.models.state.FeedbackMessage
import com.bsoftwares.thebiblequiz.ui.theme.appBackground

@Composable
fun BasicScreenBox(
    feedbackMessage: FeedbackMessage = FeedbackMessage.NoMessage,
    conditionToDisplayFeedbackMessage: Boolean = false,
    dialogType: DialogType = DialogType.EmptyValue,
    enabled: Boolean = true,
    content: @Composable() (BoxScope.() -> Unit)
) {


    var displayDialog by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(dialogType) {
        if (dialogType is DialogType.Loading) {
            displayDialog = true
        }
    }

    if (displayDialog) {
        when(dialogType) {
            is DialogType.Loading -> {
                dialogType.Generate()
            }
            else -> Unit
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(appBackground())) {
        content()
        if (!enabled) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent) // Optional, purely for visibility
                    .pointerInput(Unit) {
                        awaitPointerEventScope {
                            while (true) {
                                awaitPointerEvent() // Consume all touch events
                            }
                        }
                    }
            )
        }
        Log.i("Feedback message", feedbackMessage.messageText)
        if (feedbackMessage != FeedbackMessage.NoMessage && conditionToDisplayFeedbackMessage) {
            FeedbackMessageContainer(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(16.dp),
                errorMessage = feedbackMessage
            )
        }
    }
}