package com.example.jiaozzrecords.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jiaozzrecords.components.BackappManager
import com.example.jiaozzrecords.components.MusicPlayer
import com.example.jiaozzrecords.components.PreviewPlay
import com.example.jiaozzrecords.components.UserMusiclists
import com.example.jiaozzrecords.ui.theme.JiaozzRecordsTheme

@Composable
fun PlayerScreen(modifier: Modifier = Modifier) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ){
        // 主卡片区域
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            //左侧
            Surface(
                modifier = Modifier
                    .width(screenWidth * 0.40f)
                    .height(screenHeight * 0.56f)
                    .padding(10.dp)
                    .shadow(12.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
            ) {
                UserMusiclists()
            }
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
                )

                PreviewPlay(
                    modifier = Modifier
                        .height(screenHeight * 0.19f)
                        .fillMaxWidth()
                )
            }
        }
        BackappManager(
            modifier = Modifier
                .fillMaxWidth()
                .height(screenHeight * 0.3f)
        )
    }

}



@Preview(showBackground = true)
@Composable
fun PlayerScreenPreview() {
    JiaozzRecordsTheme {
        PlayerScreen()
    }
}