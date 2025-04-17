package com.example.jiaozzrecords.components

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jiaozzrecords.R
import com.example.jiaozzrecords.ui.theme.JiaozzRecordsTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FadingWeatherImage(
    newResId: Int,
    modifier: Modifier = Modifier
) {
    var currentResId by remember { mutableStateOf(newResId) }
    var previousResId by remember { mutableStateOf(newResId) }

    // 是否正在切换图片
    val isSwitching = currentResId != newResId

    val alpha by animateFloatAsState(
        targetValue = if (isSwitching) 0f else 1f,
        animationSpec = tween(durationMillis = 500),
        finishedListener = {
            previousResId = newResId
            currentResId = newResId
        },
        label = "imageFade"
    )

    Box(modifier = modifier) {
        // 旧图层
        Image(
            painter = painterResource(id = previousResId),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = if (isSwitching) 1f else 0f
        )

        // 新图层（淡入）
        Image(
            painter = painterResource(id = newResId),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = alpha
        )
    }
}


@Composable
fun WeatherCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val context = LocalContext.current
    var timeNow by remember { mutableStateOf("") }
    var weatherNow by remember { mutableStateOf("未知") }
    var backgroundRes by remember { mutableStateOf(R.drawable.weather_others) }

    // 获取时间并定期刷新
    LaunchedEffect(Unit) {
        while (true) {
            val sdf = SimpleDateFormat("HH", Locale.getDefault())
            val hour = sdf.format(Date()).toInt()
            timeNow = hour.toString()
            if (hour % 1 == 0) { // 每小时整点更新天气
                val fusedClient = LocationServices.getFusedLocationProviderClient(context)
                val location = getLastLocation(fusedClient, context)
                location?.let {
                    weatherNow = fetchWeather(it.latitude, it.longitude)
                }
            }
            delay(60 * 1000L) // 每分钟检测一次时间
        }
    }

    LaunchedEffect(timeNow, weatherNow) {
        backgroundRes = getWeatherDrawable(timeNow, weatherNow)
    }

    Surface(
        modifier = modifier
            .height(120.dp)
            .shadow(6.dp, RoundedCornerShape(12.dp))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = Color.Transparent
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            FadingWeatherImage(
                newResId = backgroundRes,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
            )
            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart) // 整体左中对齐
                    .padding(start = 8.dp)
            )
            {
                Text(
                    text = "此刻:",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic
                    )
                )
                Text(
                    text = "天气组件",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic
                    )
                )
            }
        }
    }
}

@SuppressLint("MissingPermission")
suspend fun getLastLocation(
    fusedLocationClient: FusedLocationProviderClient,
    context: Context
): Location? = withContext(Dispatchers.IO) {
    suspendCancellableCoroutine { cont ->
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                cont.resume(location) {}
            }
            .addOnFailureListener {
                cont.resume(null) {}
            }
    }
}

@Serializable
data class WeatherResponse(val weather: List<Weather>)
@Serializable
data class Weather(val description: String)

suspend fun fetchWeather(lat: Double, lon: Double): String = withContext(Dispatchers.IO) {
    val client = HttpClient(CIO) {
        install(ContentNegotiation) { json() }
    }

    return@withContext try {
        val response: WeatherResponse = client.get("https://api.openweathermap.org/data/2.5/weather") {
            parameter("lat", lat)
            parameter("lon", lon)
            parameter("appid", "28eed9200d385de2a0bc519e107edbbd")
            parameter("lang", "zh_cn")
            parameter("units", "metric")
        }.body()
        response.weather.firstOrNull()?.description ?: "未知"
    } catch (e: Exception) {
        "请求失败"
    } finally {
        client.close()
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
        weather.contains("多云") -> if (timePeriod == "night") "cloudy" else "cloudy"
        weather.contains("阴") -> if (timePeriod == "night") "cloudy" else "overcast"
        weather.contains("雨") -> "rainy"
        else -> "others"
    }

    val name = when (weatherType) {
        "others" -> "weather_others"
        else -> "weather_${weatherType}${timePeriod}"
    }

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
        "weather_overcastnight" -> R.drawable.weather_cloudynight

        "weather_rainymorning" -> R.drawable.weather_rainymorning
        "weather_rainynoon" -> R.drawable.weather_rainynoon
        "weather_rainysunset" -> R.drawable.weather_rainysunset
        "weather_rainynight" -> R.drawable.weather_rainynight

        else -> R.drawable.weather_others
    }
}

@Preview(showBackground = true)
@Composable
fun WeatherCardPreview() {
    JiaozzRecordsTheme {
        WeatherCard()
    }
}