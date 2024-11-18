package com.bsoftwares.thebiblequiz.ui.screens.profile

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bsoftwares.thebiblequiz.R
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicContainer
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicEditText
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicText
import com.bsoftwares.thebiblequiz.ui.theme.NovaGincanaBiblicaTheme

@Composable
fun AddFriendDialog(
    modifier: Modifier = Modifier,
    errorMessage: String,
    goBackClick: () -> Unit,
    addUser: (String) -> Unit,
    updateErrorMessage: () -> Unit
) {

    var userIdString by remember {
        mutableStateOf("")
    }

    Box(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            BasicContainer {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    BasicText(text = "Add a friend", fontSize = 22)
                    BasicEditText(
                        text = userIdString,
                        errorMessage = errorMessage,
                        keyboardAction = {
                            addUser(userIdString)
                        }) {
                        updateErrorMessage()
                        userIdString = it
                    }
                }


            }

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                BasicContainer(modifier = Modifier.weight(1f), onClick = {
                    goBackClick()
                }) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Image(
                            modifier = Modifier
                                .size(24.dp),
                            painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                            contentDescription = null
                        )
                        BasicText(
                            modifier = Modifier.align(Alignment.CenterVertically),
                            text = "Go Back",
                            fontSize = 16
                        )
                    }
                }
                BasicContainer(modifier = Modifier.weight(1f), onClick = {
                    addUser(userIdString)
                }) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Image(
                            modifier = Modifier
                                .size(24.dp),
                            painter = painterResource(id = R.drawable.baseline_add_24),
                            contentDescription = null
                        )
                        BasicText(
                            modifier = Modifier.align(Alignment.CenterVertically),
                            text = "Add User",
                            fontSize = 16
                        )
                    }
                }
            }
        }
    }
}

    @Composable
    fun RemoveFriendDialog(
        modifier: Modifier = Modifier,
        goBackClick: () -> Unit,
        removeUser: () -> Unit,
    ) {

        Box(modifier = modifier) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                BasicContainer {
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .animateContentSize()
                            .padding(horizontal = 16.dp, vertical = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        BasicText(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.Start),
                            text = "Friend Removal",
                            fontSize = 24
                        )
                        BasicText(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.Start),
                            text = "Are you sure you want to remove this friend?"
                        )
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    BasicContainer(modifier = Modifier.weight(0.5f).clickable {
                        goBackClick()
                    }) {
                        Row {
                            Image(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .size(24.dp),
                                painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                                contentDescription = null
                            )
                            BasicText(
                                modifier = Modifier.align(Alignment.CenterVertically),
                                text = "No",
                                fontSize = 16
                            )
                        }
                    }
                    BasicContainer(modifier = Modifier.weight(0.5f).clickable {
                        removeUser()
                    }) {
                        Row {
                            Image(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .size(24.dp),
                                painter = painterResource(id = R.drawable.baseline_delete_24),
                                contentDescription = null
                            )
                            BasicText(
                                modifier = Modifier.align(Alignment.CenterVertically),
                                text = "Yes",
                                fontSize = 16
                            )
                        }
                    }
                }
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun PreviewAddFriend() {
        NovaGincanaBiblicaTheme {
            AddFriendDialog(
                errorMessage = "Something went wrong",
                goBackClick = { },
                addUser = { }) {

            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun PreviewRemoveFriend() {
        NovaGincanaBiblicaTheme {
            RemoveFriendDialog(goBackClick = { }) {

            }
        }
    }