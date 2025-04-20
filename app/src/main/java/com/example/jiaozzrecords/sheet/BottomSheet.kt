package com.example.jiaozzrecords.sheet

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties

@Composable
fun BottomSheet(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    sheetHeight: Dp = 500.dp,
    sheetContent: @Composable () -> Unit
) {
    val targetOffset = if (visible) 0.dp else sheetHeight + 200.dp
    val animatedOffset by animateDpAsState(
        targetValue = targetOffset,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
        label = "BottomSheetOffset"
    )

    if (visible || animatedOffset < sheetHeight) {
        Popup(
            alignment = Alignment.BottomCenter,
            onDismissRequest = onDismissRequest,
            properties = PopupProperties(focusable = true)
        ) {
            // 遮罩层 + 卡片整体包裹
            Box(modifier = Modifier.fillMaxSize()) {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.2f))
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            onDismissRequest()
                        }
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .height(sheetHeight)
                        .offset(y = animatedOffset)
                        .padding(bottom = 16.dp)
                        .align(Alignment.BottomCenter)
                        .shadow(16.dp, RoundedCornerShape(16.dp))
                        .background(Color.White, shape = RoundedCornerShape(16.dp))
                        .pointerInput(Unit) {
                            //  拦截点击事件，不向上传递
                            awaitPointerEventScope {
                                awaitFirstDown()
                            }
                        },
                    contentAlignment = Alignment.TopStart
                ) {
                    sheetContent()
                }
            }
        }
    }
}