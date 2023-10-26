package com.example.novagincanabiblica.ui.basicviews

import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.unit.IntOffset
import com.example.novagincanabiblica.ui.theme.animationDuration
import com.example.novagincanabiblica.ui.theme.startDelayAnimation

@Composable
fun animateAngle(
    condition: Boolean,
    startValue: Float,
    endValue: Float,
    duration: Int = animationDuration,
    delay: Int = startDelayAnimation,
    easing: Easing = LinearOutSlowInEasing
): State<Float> {
    return animateFloatAsState(
        targetValue = if (condition) startValue else endValue,
        animationSpec = tween(
            durationMillis = duration,
            delayMillis = delay,
            easing = easing
        ),
        label = "angle"
    )
}

@Composable
fun animatePosition(
    condition: Boolean,
    startValue: IntOffset,
    endValue: IntOffset,
    duration: Int = animationDuration,
    delay: Int = startDelayAnimation,
    easing: Easing = LinearOutSlowInEasing
): State<IntOffset> {
    return animateIntOffsetAsState(
        targetValue = if (condition) startValue else endValue,
        animationSpec = tween(
            durationMillis = duration,
            delayMillis = delay,
            easing = easing
        ),
        label = "offset"
    )
}

@Composable
fun animateScale(
    condition: Boolean,
    startValue: Float,
    endValue: Float,
    duration: Int = animationDuration,
    delay: Int = startDelayAnimation,
    easing: Easing = LinearOutSlowInEasing
): State<Float> {
    return animateFloatAsState(
        targetValue = if (condition) startValue else endValue,
        animationSpec = tween(
            durationMillis = duration,
            delayMillis = delay,
            easing = easing
        ),
        label = "scale"
    )
}

@Composable
fun animateAlpha(
    condition: Boolean,
    startValue: Float = 0f,
    endValue: Float = 1f,
    duration: Int = animationDuration,
    delay: Int = startDelayAnimation,
    easing: Easing = LinearOutSlowInEasing
): State<Float> {
    return animateFloatAsState(
        targetValue = if (condition) startValue else endValue,
        animationSpec = tween(
            durationMillis = duration,
            delayMillis = delay,
            easing = easing
        ),
        label = "alpha"
    )
}

@Composable
fun generateSubSequentialAlphaAnimations(
    numberOfViews: Int,
    condition: Boolean
): List<State<Float>> {
    val list = mutableListOf<State<Float>>()
    var delay = 0
    for (i in 0..numberOfViews) {
        delay += 200
        list.add(
            animateAlpha(condition = condition, duration = 500, delay = delay)
        )
    }
    return list
}

@Composable
fun generateSubSequentialPositionAnimations(
    numberOfViews: Int,
    condition: Boolean,
    offsetStart: IntOffset
): List<State<IntOffset>> {
    val list = mutableListOf<State<IntOffset>>()
    var delay = 0
    for (i in 0..numberOfViews) {
        delay += 200
        list.add(
            animatePosition(
                condition = condition,
                startValue = offsetStart,
                endValue = IntOffset.Zero,
                duration = 500,
                delay = delay
            )
        )
    }
    return list
}