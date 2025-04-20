@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.example.jiaozzrecords.components

import android.annotation.SuppressLint
import android.location.Location
import android.util.Log
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
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
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

@Composable
fun WeatherCard(modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    val context = LocalContext.current
    var weatherNow by rememberSaveable { mutableStateOf("未知") }
    var backgroundRes by rememberSaveable { mutableIntStateOf(R.drawable.weather_others) }
    var weatherDescription by rememberSaveable { mutableStateOf("天气组件") }
    val scope = rememberCoroutineScope()
    val fusedClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    LaunchedEffect(Unit) {
        val nowHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY).toString()
        WeatherInfoProvider.timeNow = nowHour

        fetchAndUpdateWeather(fusedClient) { response ->
            weatherNow = response.now.text

            // ✅ 在这一步就直接设置背景图和描述，省一个 Effect
            backgroundRes = getWeatherDrawable(WeatherInfoProvider.timeNow, response.now.text)
            weatherDescription = getWeatherText(backgroundRes)
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
                    val location = getLastLocation(fusedClient)
                    if (location != null) {
                        WeatherInfoProvider.lat = location.latitude
                        WeatherInfoProvider.lon = location.longitude
                        val response = fetchWeatherRaw(location.latitude, location.longitude)
                        WeatherInfoProvider.weatherNow = response.now.text
                        weatherNow = WeatherInfoProvider.weatherNow
                    }
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

        val jsonString = response.bodyAsText()
        WeatherInfoProvider.rawResponseJson = jsonString

        // ✅ 反序列化
        val parsed = kotlinx.serialization.json.Json.decodeFromString<HeWeatherNowResponse>(jsonString)
        // ✅ 提取天气描述到 WeatherInfoProvider.weatherNow
        WeatherInfoProvider.weatherNow = parsed.now.text

        return@withContext parsed

    } catch (e: Exception) {
        val msg = e.message ?: "未知错误"
        Log.e("WeatherFetch", "❌ 和风天气获取失败: $msg")
        WeatherInfoProvider.errorMsg = "天气获取失败：$msg"
        HeWeatherNowResponse(
            code = "500",
            now = HeWeatherNow(text = "请求失败")
        )
    } finally {
        client.close()
    }
}

@SuppressLint("MissingPermission")
suspend fun getLastLocation(client: FusedLocationProviderClient): Location? =
    withContext(Dispatchers.IO) {
        suspendCancellableCoroutine { cont ->
            client.lastLocation
                .addOnSuccessListener { cont.resume(it) {} }
                .addOnFailureListener { cont.resume(null) {} }
        }
    }

suspend fun fetchAndUpdateWeather(
    client: FusedLocationProviderClient,
    update: (HeWeatherNowResponse) -> Unit
) {
    val location = getLastLocation(client)
    if (location != null) {
        WeatherInfoProvider.lat = location.latitude
        WeatherInfoProvider.lon = location.longitude
        val response = fetchWeatherRaw(location.latitude, location.longitude)
        WeatherInfoProvider.weatherResponse = response
        WeatherInfoProvider.weatherNow = response.now.text
        update(response)
    }
}

fun getWeatherDrawable(hourStr: String, weather: String): Int {
    val hour = hourStr.toIntOrNull() ?: return R.drawable.weather_others
    val timePeriod = when (hour) {
        in 5..11 -> "morning"
        in 12..16 -> "noon"
        in 17..20 -> "sunset"
        else -> "night"
    }
    val weatherType = when {
        weather.contains("晴") -> "sunny"
        weather.contains("多云") -> "cloudy"
        weather.contains("阴") -> if (timePeriod == "night") "cloudy" else "overcast"
        weather.contains("雨") -> "rainy"
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