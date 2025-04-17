package com.example.jiaozzrecords.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.jiaozzrecords.R

@Composable
fun BottomBar(
    currentPage: Int,
    onItemSelected: (Int) -> Unit
) {
    val icons = listOf(
        R.drawable.ic_shop,
        R.drawable.ic_play,
        R.drawable.ic_user
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color.White)
            .padding(horizontal = 32.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        icons.forEachIndexed { index, icon ->
            BottomBarItem(
                icon = icon,
                selected = index == currentPage,
                onClick = { onItemSelected(index) }
            )
        }
    }
}


@Composable
fun BottomBarItem(icon: Int, selected: Boolean, onClick: () -> Unit) {
    val animatedTint by animateColorAsState(
        targetValue = if (selected) Color(0xFF4AAE86) else Color.Gray,
        animationSpec = tween(durationMillis = 500),
        label = "IconColorAnimation"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(48.dp)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onClick()
            }
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(30.dp),
            tint = animatedTint
        )
    }
}