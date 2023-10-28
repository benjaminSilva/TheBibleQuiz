package com.example.novagincanabiblica.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.novagincanabiblica.R
import com.example.novagincanabiblica.data.models.UserData
import com.example.novagincanabiblica.ui.basicviews.BasicText
import com.example.novagincanabiblica.ui.theme.almostWhite
import com.example.novagincanabiblica.ui.theme.lessWhite
import com.example.novagincanabiblica.viewmodel.HomeViewModel


@Composable
fun InitializeProfileScreen(navController: NavHostController, homeViewModel: HomeViewModel) {
    val userData by homeViewModel.signInResult.collectAsStateWithLifecycle()
    userData.data?.apply {
        ProfileScreen(userData = this) {
            homeViewModel.signOut()
            navController.popBackStack()
        }
    }
}

@Composable
fun ProfileScreen(userData: UserData, signOut: () -> Unit) {

    val clipboardManager: ClipboardManager = LocalClipboardManager.current

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(modifier = Modifier
                .shadow(20.dp)
                .align(Alignment.End)
                .clip(RoundedCornerShape(16.dp))
                .clickable {
                    signOut()
                }
                .background(almostWhite)) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Image(
                        modifier = Modifier
                            .size(24.dp),
                        painter = painterResource(id = R.drawable.logout_24),
                        contentDescription = null
                    )
                    BasicText(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        text = "Logout",
                        fontSize = 16
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            clipboardManager.setText(AnnotatedString(userData.userId!!))
                        }
                        .background(almostWhite)
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box {
                        Row {
                            AsyncImage(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape),
                                model = userData.profilePictureUrl,
                                contentDescription = null
                            )
                            Column(
                                modifier = Modifier.padding(4.dp),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                BasicText(text = userData.userName, fontSize = 22)
                                BasicText(text = "id: ${userData.userId}", fontSize = 8)
                            }
                        }
                    }
                    Image(
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(8.dp)
                            .size(32.dp),
                        painter = painterResource(id = R.drawable.baseline_content_copy_24),
                        contentDescription = null
                    )
                }
            }

            BasicText(text = "My stats", fontSize = 22)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .shadow(20.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                        }
                        .background(almostWhite)
                ) {
                    Image(
                        modifier = Modifier
                            .padding(16.dp)
                            .size(24.dp),
                        painter = painterResource(id = R.drawable.baseline_menu_book_24),
                        contentDescription = null
                    )
                    BasicText(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        text = "Quiz", fontSize = 16
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .shadow(20.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                        }
                        .background(almostWhite)
                ) {
                    Image(
                        modifier = Modifier
                            .padding(16.dp)
                            .size(24.dp),
                        painter = painterResource(id = R.drawable.baseline_border_clear_24),
                        contentDescription = null
                    )
                    BasicText(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        text = "Wordle",
                        fontSize = 16
                    )
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                BasicText(text = "Friends list", fontSize = 22)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .shadow(20.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(almostWhite)
                ) {
                    LazyColumn(
                        modifier = Modifier.padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            Box {
                                Row(modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable { }
                                    .padding(8.dp)) {
                                    Image(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .align(Alignment.CenterVertically),
                                        painter = painterResource(id = R.drawable.baseline_add_24),
                                        contentDescription = null
                                    )
                                    BasicText(
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .align(Alignment.CenterVertically),
                                        text = "Add new friend",
                                        fontSize = 18
                                    )
                                }
                            }
                        }
                        items(20) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable { }
                                    .background(lessWhite)
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Box {
                                    Row {
                                        AsyncImage(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(CircleShape)
                                                .align(Alignment.CenterVertically),
                                            model = userData.profilePictureUrl,
                                            contentDescription = null
                                        )
                                        BasicText(
                                            modifier = Modifier
                                                .padding(8.dp)
                                                .align(Alignment.CenterVertically),
                                            text = userData.userName,
                                            fontSize = 18
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
