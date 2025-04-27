package com.example.jiaozzrecords.utils

import android.annotation.SuppressLint
import android.location.Location
import com.example.jiaozzrecords.sheet.info.WeatherInfoProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

// utils/NetworkUtils.kt
suspend fun fetchWeatherRaw(lat: Double, lon: Double): HeWeatherNowResponse =
    withContext(Dispatchers.IO) {
        val client = HttpClient(CIO) {
            install(ContentNegotiation) { json() }
            install(ContentEncoding)   { gzip() }
        }
        try {
            val locationParam = "%.2f,%.2f".format(lon, lat)
            val resp = client.get("https://mc3byej63n.re.qweatherapi.com/v7/weather/now") {
                url {
                    parameters.append("location", locationParam)
                    parameters.append("key", WeatherInfoProvider.appid)
                }
            }
            // 1) 保存请求 URL
            WeatherInfoProvider.requestUrl = resp.request.url.toString()

            // 2) 读原始文本并保存
            val bodyText = resp.bodyAsText()
            WeatherInfoProvider.rawResponseJson = bodyText

            if (!resp.status.isSuccess()) {
                throw Exception("HTTP ${resp.status}")
            }
            // 3) 解析并返回
            kotlinx.serialization.json.Json.decodeFromString(bodyText)
        } finally {
            client.close()
        }
    }

/** 获取一次定位（调用方需先确保有权限） */
@SuppressLint("MissingPermission")
suspend fun requestCurrentLocation(client: FusedLocationProviderClient): Location? =
    withContext(Dispatchers.IO) {
        suspendCancellableCoroutine<Location?> { cont ->
            client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { cont.resume(it) }
                .addOnFailureListener { cont.resume(null) }
        }
    }