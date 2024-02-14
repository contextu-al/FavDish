package com.tutorials.eu.favdish.view.compose

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign

@Composable
fun ComposeComponents(modifier: Modifier = Modifier) {
    Text(
        text = "Hello world",
        modifier = modifier,
        textAlign = TextAlign.Center
    )
}