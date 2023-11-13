package com.example.novagincanabiblica.ui.basicviews

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.novagincanabiblica.data.models.wordle.LetterState
import com.example.novagincanabiblica.data.models.wordle.generateStartWordleAttemptList
import com.example.novagincanabiblica.data.models.wordle.initiateKeyboardState
import com.example.novagincanabiblica.ui.screens.games.wordle.WordleScreen
import com.example.novagincanabiblica.ui.theme.NovaGincanaBiblicaTheme
import com.example.novagincanabiblica.ui.theme.animationDuration
import com.example.novagincanabiblica.ui.theme.correctAnswer
import com.example.novagincanabiblica.ui.theme.gray
import com.example.novagincanabiblica.ui.theme.lessWhite
import com.example.novagincanabiblica.ui.theme.startDelayAnimation
import com.example.novagincanabiblica.ui.theme.wrongPlace
import kotlin.math.roundToInt

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
fun animateScaleBouncy(
    condition: Boolean,
    startValue: Float = 0f,
    endValue: Float = 1f
): State<Float> {
    return animateFloatAsState(
        targetValue = if (condition) startValue else endValue,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessVeryLow
        ),
        label = "scaleWithBoucyness"
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

fun Modifier.shake(shakeController: ShakeController) = composed {
    shakeController.shakeConfig?.let { shakeConfig ->
        val shake = remember { Animatable(0f) }
        LaunchedEffect(shakeController.shakeConfig) {
            for (i in 0..shakeConfig.iterations) {
                when (i % 2) {
                    0 -> shake.animateTo(1f, spring(stiffness = shakeConfig.intensity))
                    else -> shake.animateTo(-1f, spring(stiffness = shakeConfig.intensity))
                }
            }
            shake.animateTo(0f)
        }

        this
            .rotate(shake.value * shakeConfig.rotate)
            .graphicsLayer {
                rotationX = shake.value * shakeConfig.rotateX
                rotationY = shake.value * shakeConfig.rotateY
            }
            .scale(
                scaleX = 1f + (shake.value * shakeConfig.scaleX),
                scaleY = 1f + (shake.value * shakeConfig.scaleY),
            )
            .offset {
                IntOffset(
                    (shake.value * shakeConfig.translateX).roundToInt(),
                    (shake.value * shakeConfig.translateY).roundToInt(),
                )
            }
    } ?: this
}

@Composable
fun rememberShakeController(): ShakeController {
    return remember { ShakeController() }
}

class ShakeController {
    var shakeConfig: ShakeConfig? by mutableStateOf(null)
        private set

    fun shake(shakeConfig: ShakeConfig) {
        this.shakeConfig = shakeConfig
    }
}

data class ShakeConfig(
    val iterations: Int,
    val intensity: Float = 100_000f,
    val rotate: Float = 0f,
    val rotateX: Float = 0f,
    val rotateY: Float = 0f,
    val scaleX: Float = 0f,
    val scaleY: Float = 0f,
    val translateX: Float = 0f,
    val translateY: Float = 0f,
    val trigger: Long = System.currentTimeMillis(),
)

@Preview(showBackground = true)
@Composable
fun PreviewAnimations() {
    NovaGincanaBiblicaTheme {
        var condition by remember {
            mutableStateOf(false)
        }
        LaunchedEffect(Unit) {
            condition = true
        }
        val animationAlpha by animateAlpha(condition = condition)
        Box (modifier = Modifier
            .fillMaxSize()
            .background(Color.Blue)) {
            Box (
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(animationAlpha)
                    .background(Color.White)) {

            }
        }

    }
}