package com.example.jiaozzrecords.components

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.os.IBinder
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jiaozzrecords.R
import com.example.jiaozzrecords.components.service.MusicPlayerService
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicPlayer(
    modifier: Modifier = Modifier,
    onExClick: () -> Unit = {}
) {
    val context = LocalContext.current

    // ① 绑定 Service
    var service: MusicPlayerService? by remember { mutableStateOf(null) }
    DisposableEffect(Unit) {
        val conn = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, binder: IBinder) {
                service = (binder as MusicPlayerService.LocalBinder).getService()
            }
            override fun onServiceDisconnected(name: ComponentName) {
                service = null
            }
        }
        context.bindService(
            Intent(context, MusicPlayerService::class.java),
            conn,
            Context.BIND_AUTO_CREATE
        )
        onDispose {
            context.unbindService(conn)
        }
    }

    // ② 收集 Service 暴露的四个流
    val isPlaying by service?.isPlaying?.collectAsState(initial = false)
        ?: remember { mutableStateOf(false) }
    val position  by service?.position?.collectAsState(initial = 0f)
        ?: remember { mutableFloatStateOf(0f) }
    val duration  = service?.duration?.toFloat() ?: 0f
    val coverBmp  by service?.coverBitmap?.collectAsState(initial = null)
        ?: remember { mutableStateOf<Bitmap?>(null) }

    // —— UI 布局（沿用你原来的所有样式） ——
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
            // 专辑封面
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .aspectRatio(1f)
            ) {
                coverBmp?.let { bmp ->
                    Image(
                        painter = BitmapPainter(bmp.asImageBitmap()),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                        .align(Alignment.BottomCenter)
                        .background(Color.Black.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            "当前播放：Aruarian Dance",
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                fontStyle = FontStyle.Italic
                            )
                        )
                        Text(
                            "音源：本地",
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
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

            // 进度条外框
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp)
                    .padding(horizontal = 8.dp)
                    .border(
                        width = 1.dp,
                        color = Color.Black.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(50)
                    )
                    .padding(horizontal = 16.dp)
            ) {
                Slider(
                    value = position,
                    onValueChange = { newPos ->
                        service?.seekTo(newPos.toInt())
                    },
                    valueRange = 0f..duration,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center),
                    colors = SliderDefaults.colors(
                        activeTrackColor   = Color(0xFF4AAE86),
                        inactiveTrackColor = Color.LightGray,
                        thumbColor         = Color.Black.copy(alpha = 0.3f)
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 控制按钮行
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                AnimatedIconButton(
                    icon = if (isPlaying)
                        ImageVector.vectorResource(id = R.drawable.ic_pause)
                    else
                        ImageVector.vectorResource(id = R.drawable.ic_toplay),
                    onClick = {
                        // —— 先启动 Service（如果还没启动） ——
                        context.startService(Intent(context, MusicPlayerService::class.java))
                        // —— 再根据当前状态播放或暂停 ——
                        if (isPlaying) service?.pause() else service?.play()
                    },
                    modifier = Modifier.weight(1f).fillMaxHeight()
                )

                Spacer(modifier = Modifier.width(8.dp))

                AnimatedIconButton(
                    icon = ImageVector.vectorResource(id = R.drawable.ic_nextmusic),
                    onClick = {
                        service?.seekTo(0)
                        service?.play()
                    },
                    modifier = Modifier.weight(1f).fillMaxHeight()
                )

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .width(36.dp)
                        .height(64.dp)
                        .border(
                            width = 2.dp,
                            color = Color.Gray.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(6.dp)
                        )
                        .clickable(
                            indication       = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { onExClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Text("Ex", color = Color.Gray.copy(alpha = 0.3f), fontSize = 18.sp)
                }
            }
        }
    }
}
/**
 * 简易带点击反馈的 IconButton
 */
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
            .border(2.dp, animatedColor, shape = CircleShape)
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
