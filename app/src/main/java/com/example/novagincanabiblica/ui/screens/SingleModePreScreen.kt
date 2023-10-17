package com.example.novagincanabiblica.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.novagincanabiblica.R
import com.example.novagincanabiblica.ui.theme.NovaGincanaBiblicaTheme

@Composable
fun PreSoloScreen(navController : NavHostController) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column {
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = stringResource(R.string.the_quiz)
            )
            Column {
                Button(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = { navController.popBackStack() }) {
                    Text(text = stringResource(R.string.go_back))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPreSoloScreen() {
    NovaGincanaBiblicaTheme {
        PreSoloScreen(rememberNavController())
    }
}