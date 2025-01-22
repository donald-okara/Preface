package ke.don.feature_book_details.domain.utils.color_utils

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import ke.don.feature_book_details.domain.utils.color_utils.model.ColorPallet

@Composable
fun getDominantColor(
    colorPalette: ColorPallet,
    isDarkTheme: Boolean = isSystemInDarkTheme()
): Color {
    fun isColorVisible(color: Color, isDarkTheme: Boolean): Boolean {
        val luminanceThreshold = if (isDarkTheme) 0.3 else 0.7
        return color.luminance() > luminanceThreshold
    }

    return when {
        isDarkTheme -> {
            val dominantColor = colorPalette.dominantColor
            val lightMutedColor = colorPalette.lightMutedColor

            when {
                lightMutedColor != Color.Transparent && isColorVisible(lightMutedColor, isDarkTheme) -> lightMutedColor
                dominantColor != Color.Transparent && isColorVisible(dominantColor, isDarkTheme) -> dominantColor
                else -> MaterialTheme.colorScheme.tertiaryContainer
            }
        }
        else -> { // Light theme
            val dominantColor = colorPalette.dominantColor
            val darkMutedColor = colorPalette.darkMutedColor

            when {
                darkMutedColor != Color.Transparent && isColorVisible(darkMutedColor, isDarkTheme) -> darkMutedColor
                dominantColor != Color.Transparent && isColorVisible(dominantColor, isDarkTheme) -> dominantColor
                else -> MaterialTheme.colorScheme.tertiaryContainer
            }
        }
    }
}


