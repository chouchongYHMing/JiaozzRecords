package com.example.jiaozzrecords.utils

import kotlinx.serialization.Serializable

@Serializable
data class HeWeatherNowResponse(
    val code: String,
    val updateTime: String? = null,
    val fxLink: String?     = null,
    val now: HeWeatherNow,
    val refer: Refer?       = null
)

@Serializable
data class HeWeatherNow(
    val obsTime: String? = null,
    val temp: String?    = null,
    val feelsLike: String? = null,
    val icon: String?    = null,
    val text: String,               // 天气描述
    val wind360: String? = null,
    val windDir: String? = null,
    val windScale: String? = null,
    val windSpeed: String? = null,
    val humidity: String?  = null,
    val precip: String?    = null,
    val pressure: String?  = null,
    val vis: String?       = null,
    val cloud: String?     = null,
    val dew: String?       = null
)

@Serializable
data class Refer(
    val sources: List<String>? = null,
    val license: List<String>? = null
)