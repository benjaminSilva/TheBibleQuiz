package com.bsoftwares.thebiblequiz.ui.basicviews

import androidx.compose.foundation.clickable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DefaultShadowColor
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.platform.inspectable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

fun Modifier.shadowWithAnimation(
    elevation: Dp,
    shape: Shape = RectangleShape,
    clip: Boolean = elevation > 0.dp,
    ambientColor: Color = DefaultShadowColor,
    spotColor: Color = DefaultShadowColor,
    offset: IntOffset = IntOffset.Zero,
    alpha: Float = 1f,
    scaleY: Float = 1f,
    scaleX: Float = 1f
) = if (elevation > 0.dp || clip) {
    inspectable(
        inspectorInfo = debugInspectorInfo {
            name = "shadow"
            properties["elevation"] = elevation
            properties["shape"] = shape
            properties["clip"] = clip
            properties["ambientColor"] = ambientColor
            properties["spotColor"] = spotColor
        }
    ) {
        graphicsLayer {
            this.shadowElevation = elevation.toPx()
            this.shape = shape
            this.clip = clip
            this.ambientShadowColor = ambientColor
            this.spotShadowColor = spotColor
            this.translationX = offset.x.toFloat()
            this.alpha = alpha
            this.scaleY = scaleY
            this.scaleX = scaleX
        }
    }
} else {
    this
}

fun Modifier.clickableIfClickNotNull(clickableCondition: Boolean, modifier : () -> Unit) : Modifier {
    return if (clickableCondition) {
        then(clickable {
            modifier()
        })
    } else {
        this
    }
}