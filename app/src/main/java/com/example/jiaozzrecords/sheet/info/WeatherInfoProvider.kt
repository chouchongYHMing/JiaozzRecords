package com.example.jiaozzrecords.sheet.info

import com.example.jiaozzrecords.components.HeWeatherNowResponse

object WeatherInfoProvider {
    var lat: Double? = null
    var lon: Double? = null
    var appid: String = "98d6b9e38bac4bf19f99cb0d5ef35253"
    var timeNow: String = ""
    var weatherNow: String = "未知"
    var weatherResponse: HeWeatherNowResponse? = null

    var errorMsg: String? = null
    var requestUrl: String? = null
    var rawResponseJson: String? = null
}