@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.example.jiaozzrecords.components

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.jiaozzrecords.R
import com.example.jiaozzrecords.sheet.info.WeatherInfoProvider
import com.example.jiaozzrecords.ui.theme.JiaozzRecordsTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import java.util.Calendar
import java.util.Locale

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

@Serializable
data class HeWeatherNowResponse(
    val code: String,
    val updateTime: String? = null,
    val fxLink: String? = null,
    val now: HeWeatherNow,
    val refer: Refer? = null
)

@Serializable
data class HeWeatherNow(
    val obsTime: String? = null,
    val temp: String? = null,
    val feelsLike: String? = null,
    val icon: String? = null,
    val text: String,  // 天气描述
    val wind360: String? = null,
    val windDir: String? = null,
    val windScale: String? = null,
    val windSpeed: String? = null,
    val humidity: String? = null,
    val precip: String? = null,
    val pressure: String? = null,
    val vis: String? = null,
    val cloud: String? = null,
    val dew: String? = null
)

@Serializable
data class Refer(
    val sources: List<String>? = null,
    val license: List<String>? = null
)

suspend fun fetchWeatherRaw(lat: Double, lon: Double): HeWeatherNowResponse = withContext(Dispatchers.IO) {
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }

        install(ContentEncoding) {
            gzip()
        }
    }

    return@withContext try {
        WeatherInfoProvider.errorMsg = null
        val locationParam = String.format(Locale.US, "%.2f,%.2f", lon, lat)

        val response = client.get("https://mc3byej63n.re.qweatherapi.com/v7/weather/now") {
            url {
                encodedParameters.append("location", locationParam)
                encodedParameters.append("key", WeatherInfoProvider.appid)
            }

            WeatherInfoProvider.requestUrl = this.url.buildString()
        }

        // 🔍 记录完整URL和状态码
        WeatherInfoProvider.requestUrl = response.request.url.toString()
        Log.d("WeatherFetch", "🌐 请求地址：${WeatherInfoProvider.requestUrl}")
        Log.d("WeatherFetch", "🔁 状态码：${response.status}")

        // ❗确保状态码是成功的才处理
        if (!response.status.isSuccess()) {
            throw Exception("API状态异常：${response.status}")
        }

        val jsonString = response.bodyAsText()
        Log.d("WeatherFetch", "📦 原始响应：$jsonString")
        WeatherInfoProvider.rawResponseJson = jsonString

        val parsed = kotlinx.serialization.json.Json.decodeFromString<HeWeatherNowResponse>(jsonString)

        // 🔐 如果返回的 code != "200"，说明业务失败
        if (parsed.code != "200") {
            throw Exception("API返回错误码：${parsed.code}")
        }

        // ✅ 成功更新共享数据
        WeatherInfoProvider.weatherNow = parsed.now.text
        return@withContext parsed

    } catch (e: Exception) {
        val errorMsg = e.message ?: "未知错误"
        Log.e("WeatherFetch", "❌ 获取失败：$errorMsg")
        WeatherInfoProvider.errorMsg = "天气获取失败：$errorMsg"

        // 返回一个假的默认值以防止崩溃
        HeWeatherNowResponse(
            code = "500",
            now = HeWeatherNow(text = "请求失败")
        )
    } finally {
        client.close()
    }
}

@SuppressLint("MissingPermission")
suspend fun requestCurrentLocation(client: FusedLocationProviderClient): Location? =
    withContext(Dispatchers.IO) {
        suspendCancellableCoroutine { cont ->
            client.getCurrentLocation(
                com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
                null
            ).addOnSuccessListener { location ->
                cont.resume(location) {}
            }.addOnFailureListener {
                cont.resume(null) {}
            }
        }
    }

fun shouldRefreshWeather(currentHour: String, cachedHour: String?, weatherNow: String): Boolean {
    return cachedHour != currentHour || weatherNow == "未知"
}


@Composable
fun WeatherCard(modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    val context = LocalContext.current
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(context, "请授予定位权限以获取天气信息", Toast.LENGTH_SHORT).show()
        }
    }
    var weatherNow by rememberSaveable { mutableStateOf("未知") }
    var backgroundRes by rememberSaveable { mutableIntStateOf(R.drawable.weather_others) }
    var weatherDescription by rememberSaveable { mutableStateOf("天气组件") }
    val scope = rememberCoroutineScope()
    val fusedClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val defaultLat = 39.9042
    val defaultLon = 116.4074

    LaunchedEffect(Unit) {
        //  检查定位权限
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // 如果没权限，请求权限
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            return@LaunchedEffect
        }
        var firstLoad = false
        var lastHour = WeatherInfoProvider.timeNow
        while (true) {
            val nowHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY).toString()
            if (!firstLoad ||shouldRefreshWeather(nowHour, lastHour, weatherNow)) {
                firstLoad = true

                lastHour = nowHour
                WeatherInfoProvider.timeNow = nowHour

                val location = requestCurrentLocation(fusedClient)
                val lat = location?.latitude ?: defaultLat
                val lon = location?.longitude ?: defaultLon
                val response = fetchWeatherRaw(lat, lon)
                weatherNow = response.now.text
                WeatherInfoProvider.lat = lat
                WeatherInfoProvider.lon = lon
                backgroundRes = getWeatherDrawable(nowHour, response.now.text)
                weatherDescription = getWeatherText(backgroundRes)
            }

            delay(60 * 1000)
        }
    }

    Surface(
        modifier = modifier
            .height(120.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Transparent)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onClick()
                scope.launch {
                    val location = requestCurrentLocation(fusedClient)
                    val lat = location?.latitude ?: defaultLat
                    val lon = location?.longitude ?: defaultLon

                    val nowHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY).toString()
                    WeatherInfoProvider.timeNow = nowHour

                    val response = fetchWeatherRaw(lat, lon)
                    weatherNow = response.now.text
                    backgroundRes = getWeatherDrawable(nowHour, response.now.text)
                    weatherDescription = getWeatherText(backgroundRes)
                }
            },
        shape = RoundedCornerShape(12.dp),
        color = Color.Transparent
    ) {
        Box(Modifier
            .fillMaxSize()
            .background(Color.Transparent)
        ) {
            FadingWeatherImage(newResId = backgroundRes, modifier = Modifier.fillMaxSize())
            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 12.dp)
            ) {
                Text(
                    text = "此刻：",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = weatherDescription,
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}


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

@Preview(showBackground = true)
@Composable
fun WeatherCardPreview() {
    JiaozzRecordsTheme {
        WeatherCard()
    }
}


