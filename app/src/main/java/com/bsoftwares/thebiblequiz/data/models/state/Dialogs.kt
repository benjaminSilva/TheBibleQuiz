package com.bsoftwares.thebiblequiz.data.models.state


import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.bsoftwares.thebiblequiz.R
import com.bsoftwares.thebiblequiz.data.models.League
import com.bsoftwares.thebiblequiz.data.models.SessionInLeague

sealed class DialogType {
    object EmptyValue : DialogType()
    object Loading : DialogType() {
        @Composable
        fun Generate(modifier: Modifier = Modifier) {
            Dialog(onDismissRequest = {}) {
                Box(modifier = modifier.fillMaxSize()) {
                    val infiniteTransition = rememberInfiniteTransition(label = "")

                    // Rotation animation
                    val rotation by infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = 0f, // Loop back to 0 degrees
                        animationSpec = infiniteRepeatable(
                            animation = keyframes {
                                durationMillis = 2000 // Total duration of the animation cycle
                                0f at 0 using (EaseOut) // Start at 0 degrees
                                20f at 500 using (EaseIn) // 45 degrees after 500ms
                                (0f) at 1000 using (EaseOut) // Back to 0 degrees at 1000ms
                                (-20f) at 1500 using (EaseIn) // -45 degrees at 1500ms
                                0f at 2000 using (EaseOut)// Back to 0 degrees at 2000ms (loop point)
                            },
                            repeatMode = RepeatMode.Restart
                        ), label = ""
                    )

                    // Vertical movement animation
                    val verticalOffset by infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = 0f, // Loop back to 0
                        animationSpec = infiniteRepeatable(
                            animation = keyframes {
                                durationMillis = 2000 // Total duration of the animation cycle

                                30f at 0 using (EaseInOut) // Start at 0
                                (-30f) at 1000 using (EaseInOut) // Move up to 30dp after 500ms
                                30f at 2000 using (EaseInOut)
                            },
                            repeatMode = RepeatMode.Restart
                        ), label = ""
                    )

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.fish), // Replace with your image resource
                            contentDescription = "Animated Image",
                            modifier = Modifier
                                .offset(y = verticalOffset.dp) // Apply vertical movement first
                                .rotate(rotation)
                        )
                    }
                }
            }
        }
    }
}


sealed class ProfileDialogType : DialogType() {
    object Quiz : ProfileDialogType()
    object Wordle : ProfileDialogType()
    object AddFriend : ProfileDialogType()
    object StartPremium : ProfileDialogType()
    object RemoveFriend : ProfileDialogType()
}

sealed class QuizDialogType : DialogType() {
    object HowToPlay : QuizDialogType()
}

sealed class LeagueDialog : DialogType() {
    object FriendList : LeagueDialog()
    data class RemoveFriend(val sessionInLeague: SessionInLeague) : LeagueDialog()
}

sealed class EditLeagueDialog : DialogType() {
    object SelectNewIcon : EditLeagueDialog()
    object DeleteLeague : EditLeagueDialog()
    object LeaveLeague : EditLeagueDialog()
    object Logs : EditLeagueDialog()
    data class ConfirmSave(val updatedLeague: League) : EditLeagueDialog() {
        fun getLeague() : League = updatedLeague
    }
}