@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.example.jiaozzrecords.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jiaozzrecords.R
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage

@Composable
fun WeatherCard(
    backgroundRes: Int,
    weatherDescription: String,
    isLoading: Boolean,
    errorMessage: String?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(120.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Box(Modifier.fillMaxSize()) {
            // 背景渐变动画
            Crossfade(
                targetState   = backgroundRes,
                animationSpec = tween(durationMillis = 800),
                modifier      = Modifier.fillMaxSize()
            ) { resId ->
                CoilImage(
                    imageModel   = { resId },
                    modifier     = Modifier.fillMaxSize(),
                    imageOptions = ImageOptions(contentScale = ContentScale.Crop)
                )
            }

            // **只有第一次（description=="加载中…"）才显示转圈**
            if (isLoading && weatherDescription == "加载中…") {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )

                // 如果有错误
            } else if (errorMessage != null) {
                Text(
                    text     = errorMessage,
                    color    = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    fontSize = 14.sp
                )

                // 普通内容区
            } else {
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 16.dp)
                ) {
                    Text(
                        text  = "此刻：",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize   = 24.sp,
                            fontWeight = FontWeight.Bold,
                            fontStyle  = FontStyle.Italic
                        )
                    )
                    Text(
                        text  = weatherDescription,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize   = 24.sp,
                            fontWeight = FontWeight.Bold,
                            fontStyle  = FontStyle.Italic
                        )
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WeatherCardPreview() {
    // 示例数据：非加载态、无错误，晴空正午，背景用 noon 晴空图
    WeatherCard(
        backgroundRes     = R.drawable.weather_others,
        weatherDescription = "无状态",
        isLoading         = false,
        errorMessage      = null,
        onClick           = {}
    )
}