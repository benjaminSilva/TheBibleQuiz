package com.example.novagincanabiblica.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.novagincanabiblica.data.models.Session
import com.example.novagincanabiblica.data.models.state.ResultOf
import com.example.novagincanabiblica.data.repositories.SoloModeRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

open class BaseViewModel(private val repo: SoloModeRepo) : ViewModel() {

    protected val _errorMessage = MutableStateFlow("")
    val errorMessage = _errorMessage.asStateFlow()

    protected val _localSession = MutableStateFlow(Session())
    val localSession = _localSession.asStateFlow()

    init {
        collectSession()
    }

    private fun collectSession() = viewModelScope.launch {
        repo.getSession().collectLatest {
            it.handleSuccessAndFailure { session ->
                _localSession.emit(session)
            }
        }
    }

    suspend fun <T> ResultOf<T>.handleSuccessAndFailure(action: suspend (value:T) -> Unit) = when (this) {
        is ResultOf.Success -> action(value)
        is ResultOf.Failure -> _errorMessage.emit(errorMessage)
    }

}