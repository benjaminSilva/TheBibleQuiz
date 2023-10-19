package com.example.novagincanabiblica.data.repositories

import com.example.novagincanabiblica.data.models.SoloGameMode
import com.google.gson.Gson

class SoloModeRepo {

    fun loadLocalQuestions(jsonString: String): SoloGameMode = Gson().fromJson(jsonString, SoloGameMode::class.java)

}