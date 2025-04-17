package com.example.jiaozzrecords.components

import android.media.MediaPlayer
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jiaozzrecords.R
import kotlinx.coroutines.delay

@Composable
fun MusicPlayer(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var currentPosition by remember { mutableStateOf(0f) }
    val duration = 100000f // 模拟总时长

    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            mediaPlayer?.let {
                currentPosition = it.currentPosition.toFloat()
            }
            delay(500)
        }
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .shadow(12.dp),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 封面图
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.96f)
                    .aspectRatio(1f)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.test_img),
                    contentDescription = "Album Cover",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.3f)
                        .align(Alignment.BottomCenter)
                        .background(Color.Black.copy(alpha = 0.5f)),
                ) {
                    Column {
                        Text(
                            "当前播放：无",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                fontStyle = FontStyle.Italic
                            )

                        )
                        Text(
                            "音源：本地",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                fontStyle = FontStyle.Italic
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 极细进度条
            Slider(
                value = currentPosition,
                onValueChange = { value ->
                    currentPosition = value
                    mediaPlayer?.seekTo(value.toInt())
                },
                valueRange = 0f..duration,
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(32.dp), // 整体高度控制拖动器空间
                colors = SliderDefaults.colors(
                    activeTrackColor = Color(0xFF4AAE86),
                    inactiveTrackColor = Color.LightGray,
                    thumbColor = Color(0xFF4AAE86)
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 播放
                AnimatedIconButton(
                    icon = if (isPlaying)
                        ImageVector.vectorResource(id = R.drawable.ic_pause)
                    else
                        ImageVector.vectorResource(id = R.drawable.ic_toplay),
                    onClick = {
                        mediaPlayer?.let {
                            if (isPlaying) it.pause() else it.start()
                            isPlaying = !isPlaying
                        }
                    },
                    modifier = Modifier.weight(1f).fillMaxHeight()
                )

                Spacer(modifier = Modifier.width(8.dp))

                // 下一首
                AnimatedIconButton(
                    icon = ImageVector.vectorResource(id = R.drawable.ic_nextmusic),
                    onClick = {
                        mediaPlayer?.seekTo(0)
                        mediaPlayer?.start()
                        isPlaying = true
                    },
                    modifier = Modifier.weight(1f).fillMaxHeight()
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Ex 按钮
                Box(
                    modifier = Modifier
                        .width(36.dp)
                        .height(64.dp)
                        .border(2.dp, Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Ex", color = Color.Gray.copy(alpha = 0.3f), fontSize = 18.sp)
                }
            }
        }
    }
}

@Composable
fun AnimatedIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconSize: Dp = 36.dp
) {
    var isActivated by remember { mutableStateOf(false) }

    val animatedColor by animateColorAsState(
        targetValue = if (isActivated) Color(0xFF4AAE86) else Color.Gray.copy(alpha = 0.3f),
        animationSpec = tween(durationMillis = 300),
        label = "tint"
    )

    LaunchedEffect(isActivated) {
        if (isActivated) {
            delay(300)
            isActivated = false
        }
    }

    Box(
        modifier = modifier
            .border(2.dp, animatedColor, shape = CircleShape) // ✅ 同步边框颜色动画
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                isActivated = true
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = animatedColor,
            modifier = Modifier.size(iconSize)
        )
    }
}