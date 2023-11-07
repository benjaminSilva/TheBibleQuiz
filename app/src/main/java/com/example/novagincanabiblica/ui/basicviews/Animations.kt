package com.example.novagincanabiblica.ui.basicviews

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.novagincanabiblica.data.models.wordle.LetterState
import com.example.novagincanabiblica.ui.theme.animationDuration
import com.example.novagincanabiblica.ui.theme.correctAnswer
import com.example.novagincanabiblica.ui.theme.gray
import com.example.novagincanabiblica.ui.theme.lessWhite
import com.example.novagincanabiblica.ui.theme.startDelayAnimation
import com.example.novagincanabiblica.ui.theme.wrongPlace

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
fun animateColor(
    condition: Boolean,
    startValue: Color,
    endValue: LetterState,
    duration: Int = animationDuration,
    delay: Int = startDelayAnimation,
    easing: Easing = LinearOutSlowInEasing
): State<Color> {
    return animateColorAsState(
        targetValue = if (condition) startValue else when (endValue) {
            LetterState.LETTER_CORRECT_PLACE -> correctAnswer
            LetterState.LETTER_NOT_IN_WORD -> gray
            LetterState.LETTER_WRONG_PLACE -> wrongPlace
            LetterState.LETTER_NOT_CHECKED -> lessWhite
        },
        animationSpec = tween(
            durationMillis = duration,
            delayMillis = delay,
            easing = easing
        ),
        label = "color"
    )
}

@Composable
fun animateDp(
    condition: Boolean,
    startValue: Dp = 0.dp,
    endValue: Dp = 0.dp,
    duration: Int = animationDuration,
    delay: Int = startDelayAnimation,
    easing: Easing = LinearOutSlowInEasing
): State<Dp> {
    return animateDpAsState(
        targetValue = if (condition) startValue else endValue,
        animationSpec = tween(
            durationMillis = duration,
            delayMillis = delay,
            easing = easing
        ),
        label = "dp"
    )
}

@Composable
fun generateSubSequentialAlphaAnimations(
    numberOfViews: Int,
    condition: Boolean,
    duration: Int = 500
): List<State<Float>> {
    val list = mutableListOf<State<Float>>()
    var delay = 0
    for (i in 0..numberOfViews) {
        delay += 200
        list.add(
            animateAlpha(condition = condition, duration = duration, delay = delay)
        )
    }
    return list
}

@Composable
fun generateSubSequentialPositionAnimations(
    numberOfViews: Int,
    condition: Boolean,
    offsetStart: IntOffset,
    duration: Int = 500
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
                duration = duration,
                delay = delay
            )
        )
    }
    return list
}

@Composable
fun animateInt(startAnimation: Boolean, endValue: Int): State<Int> {
    return animateIntAsState(
        targetValue = if (startAnimation) 0 else endValue,
        animationSpec = tween(
            durationMillis = 1000,
            delayMillis = 100,
            easing = LinearOutSlowInEasing
        ),
        label = "int"
    )
}