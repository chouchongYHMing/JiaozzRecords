package com.example.jiaozzrecords.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jiaozzrecords.components.AlbumCard
import com.example.jiaozzrecords.components.AlbumStore
import com.example.jiaozzrecords.components.LogoBlock
import com.example.jiaozzrecords.components.PlaylistsStore
import com.example.jiaozzrecords.components.SearchBar
import com.example.jiaozzrecords.components.WeatherCard
import com.example.jiaozzrecords.sheet.BottomSheet
import com.example.jiaozzrecords.sheet.BottomSheetContent
import com.example.jiaozzrecords.sheet.BottomSheetType
import com.example.jiaozzrecords.ui.theme.JiaozzRecordsTheme

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val focusManager = LocalFocusManager.current

    var showBottomSheet by remember { mutableStateOf(false) }
    var sheetType by remember { mutableStateOf(BottomSheetType.NONE) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                focusManager.clearFocus()
                showBottomSheet = false
                sheetType = BottomSheetType.NONE
            }
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(screenHeight * 0.06f)
                    .shadow(12.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                tonalElevation = 8.dp
            ) {
                SearchBar(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                LogoBlock(
                    modifier = Modifier
                        .width(screenWidth * 0.43f)
                        .height(screenHeight * 0.36f)
                        .padding(10.dp),
                    onClick = {
                        showBottomSheet = true
                        sheetType = BottomSheetType.LOGO
                    }
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    WeatherCard(
                        modifier = Modifier
                            .height(screenHeight * 0.12f)
                            .fillMaxWidth(),
                        onClick = {
                            showBottomSheet = true
                            sheetType = BottomSheetType.WEATHER
                        }
                    )

                    AlbumCard(
                        modifier = Modifier
                            .height(screenHeight * 0.24f)
                            .fillMaxWidth(),
                        onClick = {
                            showBottomSheet = true
                            sheetType = BottomSheetType.RECOMMMEND
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            AlbumStore(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(screenHeight * 0.15f),
                onClick = {
                    showBottomSheet = true
                    sheetType = BottomSheetType.ALBUM
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            PlaylistsStore(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(screenHeight * 0.15f),
                onClick = {
                    showBottomSheet = true
                    sheetType = BottomSheetType.PLAYLIST
                }
            )
        }

        BottomSheet(
            visible = showBottomSheet,
            onDismissRequest = { showBottomSheet = false },
            sheetHeight = screenHeight * 0.6f
        ) {
            BottomSheetContent(type = sheetType)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    JiaozzRecordsTheme {
        HomeScreen()
    }
}