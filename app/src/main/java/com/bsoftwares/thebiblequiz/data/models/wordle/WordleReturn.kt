package com.bsoftwares.thebiblequiz.data.models.wordle

data class WordleReturnEnglish(val listOfSomething: List<WordleCheck> = listOf())
data class WordleCheck(val word: String = "")

data class WordleReturnSpanish(val word: String)

data class WordleReturnPortuguese(val word: String)
