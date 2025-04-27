package com.example.jiaozzrecords.utils

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage

@Composable
fun FadingWeatherImage(newResId: Int, modifier: Modifier = Modifier) {
    var currentResId by remember { mutableIntStateOf(newResId) }

    LaunchedEffect(newResId) {
        currentResId = newResId
    }

    Crossfade(
        targetState = currentResId,
        animationSpec = tween(1000),
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) { resId ->
        CoilImage(
            imageModel = { resId },
            modifier = Modifier.fillMaxSize(),
            imageOptions = ImageOptions(contentScale = ContentScale.Crop),
            loading = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Transparent)
                )
            },
            failure = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Transparent)
                )
            }
        )
    }
}