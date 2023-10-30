package com.example.novagincanabiblica.data.repositories

import android.content.res.AssetManager
import com.example.novagincanabiblica.client.GoogleAuthUiClient
import com.example.novagincanabiblica.data.models.Question
import com.example.novagincanabiblica.data.models.Session
import com.example.novagincanabiblica.data.models.SessionCache
import com.example.novagincanabiblica.data.models.SoloGameMode
import com.google.gson.Gson
import javax.inject.Inject

class SoloModeRepoImpl @Inject constructor(
    private val assetManager: AssetManager,
    private val googleAuthUiClient: GoogleAuthUiClient,
    private val session: SessionCache
) : SoloModeRepo {

    override fun loadLocalQuestions(): List<Question> {
        assetManager.open("questions.json").bufferedReader().use {
            return Gson().fromJson(it.readText(), SoloGameMode::class.java).questions
        }
    }

    override fun getCurrentUser(): Session {
        return Session()
    }
}