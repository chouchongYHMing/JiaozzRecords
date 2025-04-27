package com.example.jiaozzrecords.utils

import com.example.jiaozzrecords.R

fun getWeatherDrawable(hourStr: String, weather: String): Int {
    val hour = hourStr.toIntOrNull() ?: return R.drawable.weather_others
    val timePeriod = when (hour) {
        in 5..9 -> "morning"
        in 10..16 -> "noon"
        in 17..20 -> "sunset"
        else -> "night"
    }
    val isRain = listOf("雨", "雷阵雨", "小雨", "中雨", "大雨", "暴雨", "阵雨", "毛毛雨", "冻雨", "毛毛雨/细雨", "小到中雨", "中到大雨", "大到暴雨", "强阵雨")
        .any { keyword -> weather.contains(keyword) }

    val isSnow = listOf("雪", "雨夹雪", "小雪", "中雪", "大雪", "暴雪", "阵雪", "雨雪天气", "阵雨夹雪", "小到中雪", "中到大雪", "大到暴雪")
        .any { keyword -> weather.contains(keyword) }

    val isCloudy = listOf("多云", "少云", "晴间多云").any { keyword -> weather.contains(keyword)}

    val weatherType = when {
        weather.contains("晴") -> "sunny"
        isCloudy -> "cloudy"
        weather.contains("阴") -> if (timePeriod == "night") "cloudy" else "overcast"
        isRain -> "rainy"
        isSnow -> "snowy"
        else -> "others"
    }
    val name = if (weatherType == "others") "weather_others" else "weather_${weatherType}${timePeriod}"
    return when (name) {
        "weather_sunnymorning" -> R.drawable.weather_sunnymorning
        "weather_sunnynoon" -> R.drawable.weather_sunnynoon
        "weather_sunnysunset" -> R.drawable.weather_sunnysunset
        "weather_sunnynight" -> R.drawable.weather_sunnynight
        "weather_cloudymorning" -> R.drawable.weather_cloudymorning
        "weather_cloudynoon" -> R.drawable.weather_cloudynoon
        "weather_cloudysunset" -> R.drawable.weather_cloudysunset
        "weather_cloudynight" -> R.drawable.weather_cloudynight
        "weather_overcastmorning" -> R.drawable.weather_overcastmorning
        "weather_overcastnoon" -> R.drawable.weather_overcastnoon
        "weather_rainymorning" -> R.drawable.weather_rainymorning
        "weather_rainynoon" -> R.drawable.weather_rainynoon
        "weather_rainysunset" -> R.drawable.weather_rainysunset
        "weather_rainynight" -> R.drawable.weather_rainynight
        "weather_snowymorning" -> R.drawable.weather_snowymorning
        "weather_snowynoon" -> R.drawable.weather_snowynoon
        "weather_snowysunset" -> R.drawable.weather_snowysunset
        "weather_snowynight" -> R.drawable.weather_snowynight
        else -> R.drawable.weather_others
    }
}

fun getWeatherText(resId: Int): String = when (resId) {
    R.drawable.weather_sunnymorning -> "黎明破晓"
    R.drawable.weather_sunnynoon -> "晴空正午"
    R.drawable.weather_sunnysunset -> "夕阳傍晚"
    R.drawable.weather_sunnynight -> "晴空星夜"
    R.drawable.weather_cloudymorning -> "多云清晨"
    R.drawable.weather_cloudynoon -> "多云正午"
    R.drawable.weather_cloudysunset -> "多云晚霞"
    R.drawable.weather_cloudynight -> "静谧夜晚"
    R.drawable.weather_overcastmorning -> "阴天清晨"
    R.drawable.weather_overcastnoon -> "阴天正午"
    R.drawable.weather_rainymorning -> "雨落清晨"
    R.drawable.weather_rainynoon -> "雨落正午"
    R.drawable.weather_rainysunset -> "雨落傍晚"
    R.drawable.weather_rainynight -> "雨夜时分"
    R.drawable.weather_snowymorning -> "雪落清晨"
    R.drawable.weather_snowynoon -> "雪落正午"
    R.drawable.weather_snowysunset -> "雪覆夕阳"
    R.drawable.weather_snowynight -> "雪夜时分"
    R.drawable.weather_others -> "其他天气"
    else -> "信息错误"
}
