package com.example.novagincanabiblica.data.models

data class WordleReturnEnglish(val listOfSomething: List<WordleCheck> = listOf())
data class WordleCheck(val word: String = "")

data class WordleReturnSpanish(val word: String)

data class WordleReturnPortuguese(val word: String)
