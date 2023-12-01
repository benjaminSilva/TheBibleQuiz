package com.bsoftwares.thebiblequiz.ui.basicviews

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.bsoftwares.thebiblequiz.R
import com.bsoftwares.thebiblequiz.ui.theme.almostWhite

@Composable
fun BasicContainer(
    modifier: Modifier = Modifier,
    backGroundColor: Color = colorResource(id = R.color.basic_container_color),
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
            .animateContentSize()
            .background(backGroundColor)
    ) {
        content()
    }
}