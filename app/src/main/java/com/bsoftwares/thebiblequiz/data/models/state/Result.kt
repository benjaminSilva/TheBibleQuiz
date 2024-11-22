package com.bsoftwares.thebiblequiz.data.models.state

sealed class ResultOf<out T> {
    data class Success<out R>(val value: R) : ResultOf<R>()
    data class Failure(
        val errorMessage: FeedbackMessage
    ): ResultOf<Nothing>()
    data class LogMessage(
        val reference: LogTypes,
        val errorMessage: String
    ) : ResultOf<Nothing>()
}
