package com.bsoftwares.thebiblequiz.ui.screens.games.quiz

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
import androidx.compose.ui.res.colorResource
import com.bsoftwares.thebiblequiz.R
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicText
import com.bsoftwares.thebiblequiz.ui.theme.emptyString

@Composable
fun AnimatedCounter(
    count: String,
    modifier: Modifier = Modifier,
    fontSize: Int = 22
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
                }, label = emptyString
            ) { thisChar ->
                BasicText(
                    text = thisChar.toString(),
                    fontSize = fontSize,
                    fontColor = colorResource(id = R.color.basic_container_color)
                )
            }
        }
    }
}