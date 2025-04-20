package com.example.jiaozzrecords

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.jiaozzrecords.components.BottomBar
import com.example.jiaozzrecords.ui.AppBackground
import com.example.jiaozzrecords.ui.theme.JiaozzRecordsTheme
import com.example.jiaozzrecords.view.MainScreen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Step 1: 请求定位权限（运行时）
        requestLocationPermission()

        // 👉 获取一次位置信息进行打印（只用于 Debug）
        val fusedClient = com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    android.util.Log.d("MainActivity", "✅ 地理位置获取成功: (${location.latitude}, ${location.longitude})")
                } else {
                    android.util.Log.e("MainActivity", "⚠️ 获取位置失败：location == null")
                }
            }
        }

        enableEdgeToEdge()

        // Step 2: 设置 Compose UI
        setContent {
            JiaozzRecordsTheme {
                val pagerState = rememberPagerState(initialPage = 0, pageCount = { 3 })
                val coroutineScope = rememberCoroutineScope()

                AppBackground {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = Color.Transparent,
                        bottomBar = {
                            BottomBar(
                                currentPage = pagerState.currentPage,
                                onItemSelected = { page ->
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(page)
                                    }
                                }
                            )
                        }
                    ) { innerPadding ->
                        MainScreen(
                            modifier = Modifier.padding(innerPadding),
                            pagerState = pagerState
                        )
                    }
                }
            }
        }
    }

    // 👉 请求精确和粗略位置权限
    private fun requestLocationPermission() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        val allGranted = permissions.all { perm ->
            ContextCompat.checkSelfPermission(this, perm) == PackageManager.PERMISSION_GRANTED
        }

        if (!allGranted) {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

}