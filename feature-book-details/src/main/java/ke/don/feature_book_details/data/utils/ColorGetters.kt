package ke.don.feature_book_details.data.utils

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import ke.don.shared_domain.utils.color_utils.model.ColorPallet

@Composable
fun getDominantColor(
    colorPalette: ColorPallet,
    isDarkTheme: Boolean
): Color {
    fun isColorVisible(color: Color): Boolean {
        val luminanceThreshold =  0.3
        return color.luminance() > luminanceThreshold
    }

    fun adjustColorBrightness(color: Color): Color {
        val adjustmentFactor = 0.2f
        return color.copy(
            red = (color.red + adjustmentFactor).coerceIn(0f, 1f),
            green = (color.green + adjustmentFactor).coerceIn(0f, 1f),
            blue = (color.blue + adjustmentFactor).coerceIn(0f, 1f)
        )
    }

    return when {
        isDarkTheme -> {
            val dominantColor = colorPalette.dominantColor
            val lightMutedColor = colorPalette.lightMutedColor

            when {
                dominantColor != Color.Transparent && isColorVisible(dominantColor) -> dominantColor
                dominantColor != Color.Transparent -> adjustColorBrightness(dominantColor)
                lightMutedColor != Color.Transparent && isColorVisible(lightMutedColor) -> lightMutedColor
                lightMutedColor != Color.Transparent -> adjustColorBrightness(lightMutedColor)
                else -> MaterialTheme.colorScheme.secondary
            }
        }
        else -> MaterialTheme.colorScheme.secondary
    }
}

