package com.example.novagincanabiblica.ui.basicviews

import android.graphics.BlurMaskFilter
import android.graphics.Paint
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DefaultShadowColor
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
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
    smallerShadow: Float = 0.8f
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
        }
    }
} else {
    this
}