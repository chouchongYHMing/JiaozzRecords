package com.example.jiaozzrecords.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@Composable
fun AppBackground(content: @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x80FFFDF8)) // ARGB: 0x80 = 50% 透明
                .blur(20.dp)
                .zIndex(0f)
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(1f)
        ) {
            content()
        }
    }
}