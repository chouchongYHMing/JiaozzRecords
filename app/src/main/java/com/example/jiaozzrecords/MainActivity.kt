package com.example.jiaozzrecords

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
import com.example.jiaozzrecords.components.BottomBar
import com.example.jiaozzrecords.ui.AppBackground
import com.example.jiaozzrecords.ui.theme.JiaozzRecordsTheme
import com.example.jiaozzrecords.view.MainScreen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
}