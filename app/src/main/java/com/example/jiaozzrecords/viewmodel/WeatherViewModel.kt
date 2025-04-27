package com.example.jiaozzrecords.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.jiaozzrecords.R
import com.example.jiaozzrecords.sheet.info.WeatherInfoProvider
import com.example.jiaozzrecords.utils.fetchWeatherRaw
import com.example.jiaozzrecords.utils.getWeatherDrawable
import com.example.jiaozzrecords.utils.getWeatherText
import com.example.jiaozzrecords.utils.requestCurrentLocation
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class WeatherViewModel(app: Application) : AndroidViewModel(app) {

    private val _uiState = MutableStateFlow(
        WeatherUiState(
            backgroundRes      = R.drawable.weather_others,
            weatherDescription = "加载中…",
            isLoading          = true,
            errorMessage       = null
        )
    )
    val uiState: StateFlow<WeatherUiState> = _uiState

    private val fusedClient = LocationServices.getFusedLocationProviderClient(app)

    init {
        viewModelScope.launch {
            while (true) {
                _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
                try {
                    // ① 定位
                    val loc = requestCurrentLocation(fusedClient)
                    val lat = loc?.latitude ?: 39.9042
                    val lon = loc?.longitude ?: 116.4074

                    // ② 拉取天气，这里内部已经向 WeatherInfoProvider 写了 requestUrl/rawResponseJson
                    val resp = fetchWeatherRaw(lat, lon)

                    // ③ 写回其余几个字段
                    WeatherInfoProvider.lat        = lat
                    WeatherInfoProvider.lon        = lon
                    WeatherInfoProvider.timeNow    = Calendar.getInstance()
                        .get(Calendar.HOUR_OF_DAY)
                        .toString()
                    WeatherInfoProvider.weatherNow = resp.now.text
                    // （errorMsg 上一次已经被清空了，不需要在这里再赋 null）

                    // ④ 映射 UI
                    val bg   = getWeatherDrawable(WeatherInfoProvider.timeNow, resp.now.text)
                    val text = getWeatherText(bg)
                    _uiState.value = WeatherUiState(
                        backgroundRes      = bg,
                        weatherDescription = text,
                        isLoading          = false,
                        errorMessage       = null
                    )
                } catch (e: Exception) {
                    WeatherInfoProvider.errorMsg = "加载失败：${e.message}"
                    _uiState.value = _uiState.value.copy(
                        isLoading    = false,
                        errorMessage = "加载失败：${e.message}"
                    )
                }
                delay(60_000L)
            }
        }
    }
}