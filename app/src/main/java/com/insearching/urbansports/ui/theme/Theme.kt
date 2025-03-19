package com.insearching.urbansports.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val lightScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    outline = outlineLight
)

private val darkScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    outline = outlineDark
)

@Composable
fun UrbanSportsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorsScheme = if (darkTheme) {
        darkScheme
    } else {
        lightScheme
    }

    MaterialTheme(
        colorScheme = colorsScheme,
        typography = Typography,
        content = content
    )
}