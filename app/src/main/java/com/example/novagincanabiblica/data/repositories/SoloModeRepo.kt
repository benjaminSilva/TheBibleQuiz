package com.example.novagincanabiblica.data.repositories

import com.example.novagincanabiblica.data.models.Question

interface SoloModeRepo {
    fun loadLocalQuestions() : List<Question>
}