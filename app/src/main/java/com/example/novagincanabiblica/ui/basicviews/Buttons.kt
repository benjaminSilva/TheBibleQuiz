package com.example.novagincanabiblica.ui.basicviews

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.novagincanabiblica.ui.theme.zillasFontFamily

@Composable
fun BasicButton(modifier: Modifier = Modifier, text: String, onClick: () -> Unit) {
    Button(modifier = modifier, onClick = onClick) {
        Text(text = text, fontFamily = zillasFontFamily)
    }
}