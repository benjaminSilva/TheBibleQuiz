package com.bsoftwares.thebiblequiz.data.models.state


import androidx.compose.foundation.layout.Box
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import com.bsoftwares.thebiblequiz.data.models.League
import com.bsoftwares.thebiblequiz.ui.theme.closeToBlack

sealed class DialogType() {
    object EmptyValue : DialogType()
    object Loading : DialogType() {
        @Composable
        fun generate(modifier: Modifier = Modifier) {
            Dialog(onDismissRequest = {}) {
                Box(modifier = modifier) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(
                            Alignment.Center
                        ), color = closeToBlack
                    )
                }
            }
        }
    }
}


sealed class ProfileDialogType : DialogType() {
    object Quiz : ProfileDialogType()
    object Wordle : ProfileDialogType()
    object AddFriend : ProfileDialogType()
    object RemoveFriend : ProfileDialogType()
}

sealed class QuizDialogType : DialogType() {
    object HowToPlay : QuizDialogType()
}

sealed class LeagueDialog : DialogType() {
    object FriendList : LeagueDialog()
}

sealed class EditLeagueDialog : DialogType() {
    object SelectNewIcon : EditLeagueDialog()
    object DeleteLeague : EditLeagueDialog()
    object Logs : EditLeagueDialog()
    data class ConfirmSave(val updatedLeague: League) : EditLeagueDialog() {
        fun getLeague() : League {
            return updatedLeague
        }
    }
}