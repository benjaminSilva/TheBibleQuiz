package com.example.novagincanabiblica.viewmodel

import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.example.novagincanabiblica.data.models.state.ResultOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

open class BaseViewModel : ViewModel() {

    protected val _errorMessage = MutableStateFlow("")
    val errorMessage = _errorMessage.asStateFlow()

    suspend fun <T> ResultOf<T>.handleSuccessAndFailure(action: suspend (value:T) -> Unit) = when (this) {
        is ResultOf.Success -> action(value)
        is ResultOf.Failure -> _errorMessage.emit(errorMessage)
    }

}