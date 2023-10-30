package com.example.novagincanabiblica.data.repositories

import com.example.novagincanabiblica.data.models.Question
import com.example.novagincanabiblica.data.models.Session

interface SoloModeRepo {
    fun loadLocalQuestions(): List<Question>
    fun getCurrentUser(): Session
}