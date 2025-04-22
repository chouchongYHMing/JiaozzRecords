package com.example.jiaozzrecords.sheet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jiaozzrecords.sheet.info.WeatherInfoProvider
import com.example.jiaozzrecords.ui.theme.JiaozzRecordsTheme

@Composable
fun WeatherDetailSheet() {
    val info = WeatherInfoProvider
    val scrollState = rememberScrollState()

    Column (
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.Start // 左对齐
    ){
        Text(
            text = "🌤 天气详情",
            fontSize = 30.sp,
            color = Color(0xFF4AAE86)
        )

        Text(
            text = "当前时间: ${info.timeNow}",
            fontSize = 18.sp,
            color = Color.DarkGray
        )

        Text(
            text = "经度: ${info.lon ?: "未获取"}",
            fontSize = 16.sp,
            color = Color.Gray
        )

        Text(
            text = "纬度: ${info.lat ?: "未获取"}",
            fontSize = 16.sp,
            color = Color.Gray
        )

        Text(
            text = "天气状态: ${info.weatherNow}",
            fontSize = 18.sp,
            color = Color.Black
        )

        Text(
            text = "App ID: ${info.appid}",
            fontSize = 18.sp,
            color = Color.LightGray
        )

        info.errorMsg?.let {
            Text(
                text = "⚠️ 错误信息: $it",
                fontSize = 16.sp,
                color = Color(0xFF888888)
            )
        }

        Text(
            text = "请求URL: ${info.requestUrl ?: "未生成"}",
            fontSize = 12.sp,
            color = Color.LightGray
        )

        Text(
            text = "原始响应: ${info.rawResponseJson ?: "无响应"}",
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WeatherSheetPreview() {
    JiaozzRecordsTheme {
        WeatherDetailSheet()
    }
}