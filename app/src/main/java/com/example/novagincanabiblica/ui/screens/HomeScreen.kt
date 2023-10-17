package com.example.novagincanabiblica.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.novagincanabiblica.R
import com.example.novagincanabiblica.ui.theme.NovaGincanaBiblicaTheme

@Composable
fun HomeScreen() {
    Column {
        Text(text = stringResource(R.string.app_name))
        Column {
            Button(onClick = { /*TODO*/ }) {
                Text(text = stringResource(R.string.spm_title).uppercase())
            }
            Button(onClick = { /*TODO*/ }) {
                Text(text = stringResource(R.string.mpm_title).uppercase())
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    NovaGincanaBiblicaTheme {
        HomeScreen()
    }
}