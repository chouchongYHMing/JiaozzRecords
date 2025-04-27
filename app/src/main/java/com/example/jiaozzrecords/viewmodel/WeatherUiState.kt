package com.example.jiaozzrecords.viewmodel

import androidx.annotation.DrawableRes


/**
 * 描述天气界面所需的所有状态
 */
data class WeatherUiState(
    @DrawableRes val backgroundRes: Int,
    val weatherDescription: String,
    val isLoading: Boolean,
    val errorMessage: String?
)
