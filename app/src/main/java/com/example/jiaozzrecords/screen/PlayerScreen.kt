package com.example.jiaozzrecords.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.jiaozzrecords.components.BackappManager
import com.example.jiaozzrecords.components.MusicPlayer
import com.example.jiaozzrecords.components.PreviewPlay
import com.example.jiaozzrecords.components.UserMusiclists
import com.example.jiaozzrecords.sheet.BottomSheet
import com.example.jiaozzrecords.sheet.BottomSheetContent
import com.example.jiaozzrecords.sheet.BottomSheetType
import com.example.jiaozzrecords.ui.theme.JiaozzRecordsTheme

@Composable
fun PlayerScreen(modifier: Modifier = Modifier) {
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
            // 主卡片区域
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                //左侧
                UserMusiclists(
                    width = screenWidth * 0.40f,
                    height = screenHeight * 0.56f,
                    onClick = {
                        showBottomSheet = true
                        sheetType = BottomSheetType.MUSICLISTS
                    }
                )
                //右侧
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    MusicPlayer(
                        modifier = Modifier
                            .height(screenHeight * 0.32f)
                            .fillMaxWidth()
                            .shadow(12.dp, RoundedCornerShape(16.dp)),
                        onExClick = {
                            showBottomSheet = true
                            sheetType = BottomSheetType.EX
                        }
                    )

                    PreviewPlay(
                        modifier = Modifier
                            .height(screenHeight * 0.19f)
                            .fillMaxWidth(),
                        onClick = {
                            showBottomSheet = true
                            sheetType = BottomSheetType.PREVIEW
                        }
                    )
                }
            }
            BackappManager(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(screenHeight * 0.3f),
                onClick = {
                    showBottomSheet = true
                    sheetType = BottomSheetType.BACKAPP
                }
            )
        }
        BottomSheet(
            visible = showBottomSheet,
            onDismissRequest = {
                showBottomSheet = false
                sheetType = BottomSheetType.NONE
            },
            sheetHeight = screenHeight * 0.6f
        ) {
            BottomSheetContent(type = sheetType)
        }
    }
}



@Preview(showBackground = true)
@Composable
fun PlayerScreenPreview() {
    JiaozzRecordsTheme {
        PlayerScreen()
    }
}