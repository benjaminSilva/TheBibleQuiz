package com.example.novagincanabiblica.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.novagincanabiblica.data.models.Question
import com.example.novagincanabiblica.data.models.SoloGameMode
import com.example.novagincanabiblica.data.repositories.SoloModeRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SoloModeViewModel: ViewModel() {

    val repo = SoloModeRepo()

    private val _questions = MutableStateFlow(listOf<Question>())
    val question = _questions.asStateFlow()

    fun loadQuestionsForSoloMode(jsonString: String) {
        viewModelScope.launch {
            _questions.value = repo.loadLocalQuestions(jsonString).questions
        }
    }
}