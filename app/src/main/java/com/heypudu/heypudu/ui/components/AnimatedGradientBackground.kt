package com.heypudu.heypudu.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun AnimatedGradientBackground(
    modifier: Modifier = Modifier,
    colors: List<Color> = listOf(Color(0xFFFA76A6), Color(0xFF33E7B2)),
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "gradient_transition")
    val offset = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1500f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 7000, delayMillis = 1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradient_offset"
    ).value
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = colors,
                    startY = 0f + offset,
                    endY = 1500f + offset
                )
            )
    ) {
        content()
    }
}
