package com.example.novagincanabiblica.data.models

data class SignInResult(
    val data: UserData? = UserData(),
    val errorMessage: String? = ""
)

data class UserData (
    val userId: String? = "",
    val userName: String? = "Guest",
    val profilePictureUrl: String? = ""
)
