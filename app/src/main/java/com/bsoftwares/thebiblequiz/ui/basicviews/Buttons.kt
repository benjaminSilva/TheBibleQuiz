package com.bsoftwares.thebiblequiz.ui.basicviews

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.isOutOfBounds
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastAll
import androidx.compose.ui.util.fastAny
import com.bsoftwares.thebiblequiz.R

@Composable
fun BasicContainer(
    modifier: Modifier = Modifier,
    backGroundColor: Color = colorResource(id = R.color.basic_container_color),
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    allowAnimation: Boolean = true,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .bounceClick(onClick, onLongClick, allowAnimation)
            .clip(RoundedCornerShape(16.dp))
            .animateContentSize()
            .background(backGroundColor)
    ) {
        content()
    }
}

enum class ButtonState { Pressed, Idle }
@OptIn(ExperimentalFoundationApi::class)
fun Modifier.bounceClick(onClick: (() -> Unit)?, onLongClick:(() -> Unit)?, allowAnimation: Boolean) = composed {
    if (onClick != null || onLongClick != null) {
        var buttonState by remember { mutableStateOf(ButtonState.Idle) }

        val scale by animateScaleBouncy(buttonState != ButtonState.Pressed, startValue = 1f, endValue = 0.95f)

        this
            .clip(RoundedCornerShape(16.dp))
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
            .graphicsLayer {
                if (allowAnimation) {
                    scaleX = scale
                    scaleY = scale
                }
            }
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

@Composable
fun AnimatedBorderCard(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(size = 16.dp),
    borderWidth: Dp = 2.dp,
    gradient: Brush = Brush.linearGradient(listOf(Color.Gray, Color.White)),
    animationDuration: Int = 10000,
    onCardClick: () -> Unit = {},
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "Infinite Color Animation")
    val degrees by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = animationDuration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Infinite Colors"
    )

    Surface(
        modifier = modifier
            .clip(shape)
            .clickable { onCardClick() },
        shape = shape
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(borderWidth)
                .drawWithContent {

                    rotate(degrees = degrees) {
                        drawCircle(
                            brush = gradient,
                            radius = size.width,
                            blendMode = BlendMode.SrcIn,
                        )
                    }
                    drawContent()
                },
            color = MaterialTheme.colorScheme.surface,
            shape = shape
        ) {
            content()
        }
    }
}