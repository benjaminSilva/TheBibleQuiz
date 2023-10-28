package com.example.novagincanabiblica.ui.basicviews

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun AnimatedCounter(
    count: String,
    modifier: Modifier = Modifier
) {
    var oldCount by remember {
        mutableStateOf(count)
    }
    SideEffect {
        oldCount = count
    }
    Row(modifier = modifier) {
        val oldCountString = oldCount
        for (i in count.indices) {
            val oldChar = oldCountString.getOrNull(i)
            val newChar = count[i]
            val char = if (oldChar == newChar) {
                oldCountString[i]
            } else {
                count[i]
            }
            AnimatedContent(
                targetState = char,
                transitionSpec = {
                    slideInVertically { it } togetherWith slideOutVertically { -it }
                }, label = ""
            ) { char ->
                BasicText(
                    text = char.toString(),
                    fontSize = 22
                )
            }
        }
    }
}