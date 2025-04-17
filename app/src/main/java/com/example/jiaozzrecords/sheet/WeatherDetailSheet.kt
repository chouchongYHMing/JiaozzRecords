package com.example.jiaozzrecords.sheet

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

@Composable
fun WeatherDetailSheet() {
    // 可添加图标、温度、城市选择等
    Text("🌤 天气信息", fontSize = 20.sp, color = Color(0xFF4AAE86))
}