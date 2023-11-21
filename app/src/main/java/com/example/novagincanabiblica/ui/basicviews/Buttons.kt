package com.example.novagincanabiblica.ui.basicviews

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.novagincanabiblica.ui.theme.almostWhite

@Composable
fun BasicContainer(
    modifier: Modifier = Modifier,
    backGroundColor: Color = almostWhite,
    onClick: (() -> Unit)? = null,
    shadowAlpha: Float = 1f,
    shadowOffset: IntOffset = IntOffset.Zero,
    shadowScaleX: Float = 1f,
    shadowScaleY: Float = 1f,
    enabled: Boolean = true,
    shadow: Dp = 20.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .shadowWithAnimation(
                shadow,
                alpha = shadowAlpha,
                offset = shadowOffset,
                scaleY = shadowScaleY,
                scaleX = shadowScaleX
            )
            .clip(RoundedCornerShape(16.dp))
            .clickableIfClickNotNull(clickableCondition = onClick != null) {
                if (enabled) {
                    onClick?.invoke()
                }
            }
            .background(backGroundColor)
    ) {
        content()
    }
}