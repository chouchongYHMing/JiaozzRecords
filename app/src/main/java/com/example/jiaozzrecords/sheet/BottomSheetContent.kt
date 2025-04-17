// BottomSheetContent.kt
package com.example.jiaozzrecords.sheet

import androidx.compose.runtime.Composable

enum class BottomSheetType {
    ALBUM, WEATHER, PLAYLIST, LOGO, RECOMMMEND, NONE
}

@Composable
fun BottomSheetContent(type: BottomSheetType) {
    when (type) {
        BottomSheetType.ALBUM -> AlbumStoreSheet()
        BottomSheetType.WEATHER -> WeatherDetailSheet()
        BottomSheetType.PLAYLIST -> PlaylistDetailSheet()
        BottomSheetType.LOGO -> LogoInfoSheet()
        BottomSheetType.RECOMMMEND -> AlbumRecommendSheet()
        BottomSheetType.NONE -> {} // 不显示任何内容
    }
}