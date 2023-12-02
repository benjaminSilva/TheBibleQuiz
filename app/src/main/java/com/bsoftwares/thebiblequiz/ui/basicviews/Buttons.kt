package com.bsoftwares.thebiblequiz.ui.basicviews

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.isOutOfBounds
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastAll
import androidx.compose.ui.util.fastAny
import com.bsoftwares.thebiblequiz.R

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BasicContainer(
    modifier: Modifier = Modifier,
    backGroundColor: Color = colorResource(id = R.color.basic_container_color),
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
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
            .bounceClick(onClick, onLongClick)
            .shadowWithAnimation(
                shadow,
                alpha = shadowAlpha,
                offset = shadowOffset,
                scaleY = shadowScaleY,
                scaleX = shadowScaleX
            )
            .clip(RoundedCornerShape(16.dp))
            .animateContentSize()
            .background(backGroundColor)
    ) {
        content()
    }
}

enum class ButtonState { Pressed, Idle }
@OptIn(ExperimentalFoundationApi::class)
fun Modifier.bounceClick(onClick: (() -> Unit)?, onLongClick:(() -> Unit)?) = composed {
    if (onClick != null || onLongClick != null) {
        var buttonState by remember { mutableStateOf(ButtonState.Idle) }

        val scale by animateScaleBouncy(buttonState != ButtonState.Pressed, startValue = 1f, endValue = 0.95f)

        this
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { }
            )
            .combinedClickable(onLongClick = {
                onLongClick?.invoke()
            }, onClick = {
                onClick?.invoke()
            })
            .pointerInput(buttonState) {
                awaitPointerEventScope {
                    buttonState = if (buttonState == ButtonState.Pressed) {
                        waitForUp()
                        ButtonState.Idle
                    } else {
                        awaitFirstDown(false)
                        ButtonState.Pressed
                    }
                }
            }
    } else {
        this
    }
}

suspend fun AwaitPointerEventScope.waitForUp(
    pass: PointerEventPass = PointerEventPass.Main
): PointerInputChange? {
    while (true) {
        val event = awaitPointerEvent(pass)
        if (event.changes.fastAll { it.changedToUp() }) {
            // All pointers are up
            return event.changes[0]
        }

        if (event.changes.fastAny {
                it.isConsumed || it.isOutOfBounds(size, extendedTouchPadding)
            }
        ) {
            return null // Canceled
        }
    }
}