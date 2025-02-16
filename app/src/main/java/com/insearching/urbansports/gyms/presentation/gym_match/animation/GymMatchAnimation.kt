package com.insearching.urbansports.gyms.presentation.gym_match.animation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.insearching.urbansportschallenage.R

@Composable
fun GymMatchAnimation(
    isVisible: Boolean,
    durationMillis: Int = 3000, // Extended to 3 seconds
    onAnimationEnd: () -> Unit = {}
) {
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 8f else 0f, // Expand beyond screen
        animationSpec = tween(durationMillis),
        finishedListener = { onAnimationEnd() }
    )

    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 0f else 1f, // Fade out while expanding
        animationSpec = tween(durationMillis)
    )

    val secondaryColor = MaterialTheme.colorScheme.secondary // Get secondary color for background

    if (isVisible) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)), // Dim background
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        alpha = alpha
                    )
                    .background(secondaryColor, shape = CircleShape), // Use theme's secondary color
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.gym_match),
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp) // Added padding for better readability
                )
            }
        }
    }
}
