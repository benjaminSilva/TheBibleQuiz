package com.example.novagincanabiblica.data.models.state

sealed class ResultOf<out T> {
    data class Success<out R>(val value: R): ResultOf<R>()
    data class Failure(
        val errorMessage: String
    ): ResultOf<Nothing>()
}
