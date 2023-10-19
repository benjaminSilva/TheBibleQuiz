package com.example.novagincanabiblica.ui.screens

sealed class Routes(val value: String) {
    object Home: Routes(value = "home_screen")
    object START: Routes(value = "start")
    object PreSoloScreen: Routes(value = "solo_pre_screen")
}
