package com.bsoftwares.thebiblequiz.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.bsoftwares.thebiblequiz.R

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)
val almostWhite = Color(0xFFfcfcfc)
val lessWhite = Color(0xFFf0f0f0)
val gray = Color(0xFFDDDDDD)
val wrongPlace = Color(0xFFffdc73)
val darkYellow = Color(0xFF9C7A10)
val closeToBlack = Color(0xFF424242)
val almostBlack = Color(0xFF2E2E2E)
val black = Color(0xFF000000)
val prettyMuchBlack = Color(0xFF151515)
val darkGray = Color(0xFF5A5A5A)
val lighterGray = Color(0xFF888888)
val wrongAnswerSelected = Color(0xffef9a9a)
val wrongAnswer = Color(0xFFB93939)
val wrongAnswerDark = Color(0xffc62828)
val correctAnswer = Color(0xFF7FA44E)
val correctAnswerDark = Color(0xff558b2f)
val lightBrown = Color(0xFFDBB99F)

@Composable
fun contrastColor() = colorResource(id = R.color.contrast_color)

@Composable
fun basicContainerClean() = colorResource(id = R.color.basic_container_without_shadow)

@Composable
fun basicContainerShadow() = colorResource(id = R.color.basic_container_color)

@Composable
fun appBackground() = colorResource(id = R.color.background_color)

@Composable
fun letterNotWord() = colorResource(id = R.color.letter_not_word)

@Composable
fun yellow() = colorResource(id = R.color.highlight_text)

@Composable
fun green() = colorResource(id = R.color.green)