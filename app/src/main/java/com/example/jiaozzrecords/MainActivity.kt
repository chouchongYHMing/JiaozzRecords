package com.example.jiaozzrecords

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import com.example.jiaozzrecords.components.BottomBar
import com.example.jiaozzrecords.sheet.info.WeatherInfoProvider
import com.example.jiaozzrecords.ui.AppBackground
import com.example.jiaozzrecords.ui.theme.JiaozzRecordsTheme
import com.example.jiaozzrecords.view.MainScreen
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var fusedClient: FusedLocationProviderClient

    // 权限结果回调
    private val locationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                fetchLocationOnce()
            } else {
                Log.e(TAG, "定位权限被拒绝，无法获取天气位置")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1️⃣ 初始化定位客户端
        fusedClient = LocationServices.getFusedLocationProviderClient(this)

        // 2️⃣ 请求定位权限
        requestLocationPermission()

        // 3️⃣ 打开 Edge-to-Edge
        enableEdgeToEdge()

        // 4️⃣ Compose 主界面
        setContent {
            JiaozzRecordsTheme {
                // 4.1 PagerState：三页滑动
                val pagerState = rememberPagerState(
                    initialPage = 0,
                    pageCount   = { 3 }
                )
                val coroutineScope = rememberCoroutineScope()

                AppBackground {
                    Scaffold(
                        modifier       = Modifier.fillMaxSize(),
                        containerColor = Color.Transparent,
                        bottomBar      = {
                            BottomBar(
                                currentPage    = pagerState.currentPage,
                                onItemSelected = { page ->
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(page)
                                    }
                                }
                            )
                        }
                    ) { innerPadding ->
                        MainScreen(
                            pagerState = pagerState,
                            modifier   = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }

    /** 检查权限：有则直接取一次定位，否则发起权限请求 */
    private fun requestLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                // 已有权限，直接获取一次
                fetchLocationOnce()
            }
            else -> {
                // 请求权限
                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun fetchLocationOnce() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        fusedClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            CancellationTokenSource().token
        )
            .addOnSuccessListener { loc ->
                if (loc != null) {
                    WeatherInfoProvider.lat = loc.latitude
                    WeatherInfoProvider.lon = loc.longitude
                } else {
                    // fallback
                    fusedClient.lastLocation.addOnSuccessListener { last ->
                        last?.let {
                            WeatherInfoProvider.lat = it.latitude
                            WeatherInfoProvider.lon = it.longitude
                        }
                    }
                }
            }
            .addOnFailureListener {
                Log.e(TAG, "实时定位失败，已忽略", it)
            }
    }
}