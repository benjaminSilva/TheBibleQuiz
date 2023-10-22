package com.example.novagincanabiblica.data.repositories

import android.content.res.AssetManager
import com.example.novagincanabiblica.data.models.Question
import com.example.novagincanabiblica.data.models.SoloGameMode
import com.google.gson.Gson
import javax.inject.Inject

class SoloModeRepoImpl @Inject constructor(
    private val assetManager: AssetManager
) : SoloModeRepo {

    override fun loadLocalQuestions(): List<Question> {
        assetManager.open("questions.json").bufferedReader().use {
            return Gson().fromJson(it.readText(), SoloGameMode::class.java).questions
        }
    }
}