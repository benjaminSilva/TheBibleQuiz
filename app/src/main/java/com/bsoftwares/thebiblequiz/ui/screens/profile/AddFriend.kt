package com.bsoftwares.thebiblequiz.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bsoftwares.thebiblequiz.R
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicContainer
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicEditText
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicText
import com.bsoftwares.thebiblequiz.ui.theme.NovaGincanaBiblicaTheme
import com.bsoftwares.thebiblequiz.ui.theme.emptyString

@Composable
fun AddFriendDialog(
    modifier: Modifier = Modifier,
    errorMessage: String,
    goBackClick: () -> Unit,
    addUser: (String) -> Unit,
    updateErrorMessage: () -> Unit
) {

    val focusRequester by remember { mutableStateOf(FocusRequester())  }
    val keyboardController = LocalSoftwareKeyboardController.current

    var userIdString by remember {
        mutableStateOf(emptyString)
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    Box(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            BasicContainer {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    BasicText(text = stringResource(R.string.add_a_friend), fontSize = 22)
                    BasicEditText(
                        modifier = Modifier.focusRequester(focusRequester),
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
                            text = stringResource(R.string.go_back),
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
                            text = stringResource(R.string.add_user),
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