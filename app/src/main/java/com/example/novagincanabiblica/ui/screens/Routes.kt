package com.example.novagincanabiblica.ui.screens

sealed class Routes(val value: String) {
    object Home: Routes(value = "home_screen")
    object START: Routes(value = "start")
    object SOLOMODE: Routes(value = "solo_mode")
    object SOLOPREQUESTION: Routes(value = "solo_pre_screen")
    object SOLOQUESTION: Routes(value = "solo_screen")
}
