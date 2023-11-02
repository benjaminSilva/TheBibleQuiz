package com.example.novagincanabiblica.client

import com.example.novagincanabiblica.data.models.WordleCheck
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface WordleService {

    @GET(value = "{word}")
    fun checkWord(@Path(value = "word") word: String): Call<List<WordleCheck>>?

}