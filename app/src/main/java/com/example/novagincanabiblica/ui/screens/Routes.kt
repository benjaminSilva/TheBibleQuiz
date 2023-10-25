package com.example.novagincanabiblica.ui.screens

sealed class Routes(val value: String) {
    object Home: Routes(value = "home_screen")
    object Start: Routes(value = "start")
    object SoloMode: Routes(value = "solo_mode")
    object SoloModePreQuestion: Routes(value = "solo_pre_screen")
    object SoloModeQuestion: Routes(value = "solo_screen")
    object Results: Routes(value = "results")
}
