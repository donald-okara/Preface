package com.don.preface.presentation.utils.color_utils

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import com.don.preface.presentation.utils.color_utils.model.ColorPallet

@Composable
fun getTertiaryContainerColor(
    colorPalette: ColorPallet,
    contentContainerErrorState: String?,
    isDarkTheme: Boolean = isSystemInDarkTheme()
): Color {
    return when {
        contentContainerErrorState != null -> MaterialTheme.colorScheme.tertiaryContainer
        isDarkTheme -> {
            val dominantColor = colorPalette.dominantColor
            val lightMutedColor = colorPalette.lightMutedColor

            when {
                lightMutedColor != Color.Transparent -> lightMutedColor
                dominantColor != Color.Transparent -> dominantColor
                else -> MaterialTheme.colorScheme.tertiaryContainer
            }
        }
        else -> { // Light theme
            val dominantColor = colorPalette.dominantColor
            val darkMutedColor = colorPalette.darkMutedColor

            when {
                darkMutedColor != Color.Transparent -> darkMutedColor
                dominantColor != Color.Transparent -> dominantColor
                else -> MaterialTheme.colorScheme.tertiaryContainer
            }
        }
    }
}


@Composable
fun getTertiaryContentColor(
    colorPalette: ColorPallet,
    contentContainerErrorState: String?,
    isDarkTheme: Boolean = isSystemInDarkTheme()
): Color {
    return when {
        contentContainerErrorState != null -> MaterialTheme.colorScheme.onTertiaryContainer
        isDarkTheme -> {
            val dominantColor = colorPalette.dominantColor
            val lightVibrantColor = colorPalette.lightVibrantColor

            when {
                lightVibrantColor != Color.Transparent -> lightVibrantColor
                dominantColor != Color.Transparent && dominantColor.luminance() < 0.5 ->
                    MaterialTheme.colorScheme.onTertiaryContainer
                dominantColor != Color.Transparent -> dominantColor
                else -> MaterialTheme.colorScheme.onTertiaryContainer
            }
        }
        else -> { // Light theme
            val dominantColor = colorPalette.dominantColor
            val darkVibrantColor = colorPalette.darkVibrantColor

            when {
                darkVibrantColor != Color.Transparent -> darkVibrantColor
                dominantColor != Color.Transparent && dominantColor.luminance() > 0.5 ->
                    MaterialTheme.colorScheme.onTertiaryContainer
                dominantColor != Color.Transparent -> dominantColor
                else -> MaterialTheme.colorScheme.onTertiaryContainer
            }
        }
    }
}