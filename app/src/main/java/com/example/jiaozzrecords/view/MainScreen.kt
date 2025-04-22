package com.example.jiaozzrecords.view

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MainScreen(pagerState: PagerState, modifier: Modifier = Modifier) {
    HorizontalPager(
        state    = pagerState,          // PagerState 里已经有 pageCount
        modifier = modifier.fillMaxSize(),
        pageSpacing           = 0.dp    // 可选：页间距
    ) { page ->
        when (page) {
            0 -> HomeScreen()
            1 -> PlayerScreen()
            2 -> UserScreen()
        }
    }
}