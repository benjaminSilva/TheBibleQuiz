package com.example.novagincanabiblica.data.models.state

sealed class DialogType() {
    object EmptyValue: DialogType()
}

sealed class ProfileDialogType: DialogType() {
    object Quiz: ProfileDialogType()
    object Wordle: ProfileDialogType()
    object AddFriend: ProfileDialogType()
    object RemoveFriend: ProfileDialogType()
}

sealed class QuizDialogType: DialogType() {
    object HowToPlay: QuizDialogType()
}

sealed class LeagueDialog: DialogType() {
    object FriendList: LeagueDialog()
}