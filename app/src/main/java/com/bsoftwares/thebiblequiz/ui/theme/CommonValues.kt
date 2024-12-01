package com.bsoftwares.thebiblequiz.ui.theme

const val animationDuration: Int = 1000
const val startDelayAnimation: Int = 100
const val jobTimeOut: Long = 5000L
const val emptyString: String = ""
const val initialValue: String = "initialValue"
const val destination: String = "destination"

fun disableClicks(function: () -> Unit) = false to function
fun enableClicks() = true to {}