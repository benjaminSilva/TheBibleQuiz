package com.example.novagincanabiblica.data.models

interface SessionCache {

    fun saveSession(session: Session)

    fun getActiveSession(): Session

    fun clearSession()

}