package ke.don.common_datasource.remote.domain.utils

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import ke.don.shared_domain.utils.color_utils.model.ColorPallet
import kotlin.math.pow

@Composable
fun getDominantColor(
    colorPalette: ColorPallet,
    isDarkTheme: Boolean
): Color {
    @Composable
    fun isColorVisible(color: Color): Boolean {
        val background = MaterialTheme.colorScheme.surface
        val contrast = color.contrastAgainst(background)
        return contrast > 4.5 // WCAG AA contrast threshold
    }

    fun adjustColorBrightness(color: Color): Color {
        val hsl = FloatArray(3)
        android.graphics.Color.RGBToHSV(
            (color.red * 255).toInt(),
            (color.green * 255).toInt(),
            (color.blue * 255).toInt(),
            hsl
        )
        hsl[1] = (hsl[1] + 0.2f).coerceAtMost(1f) // Saturation
        hsl[2] = (hsl[2] + if (isDarkTheme) 0.2f else -0.2f).coerceIn(0f, 1f) // Brightness tweak
        val newColorInt = android.graphics.Color.HSVToColor(hsl)
        return Color(newColorInt)
    }

    val fallback = MaterialTheme.colorScheme.secondary

    return listOf(
        colorPalette.dominantColor,
        colorPalette.lightMutedColor
    )
        .filter { it != Color.Transparent }
        .map { if (isColorVisible(it)) it else adjustColorBrightness(it) }
        .firstOrNull { isColorVisible(it) }
        ?: fallback
}

fun Color.contrastAgainst(background: Color): Double {
    fun Color.relativeLuminance(): Double {
        fun channel(c: Float): Double {
            val v = c.toDouble()
            return if (v <= 0.03928) v / 12.92 else ((v + 0.055) / 1.055).pow(2.4)
        }
        val r = channel(this.red)
        val g = channel(this.green)
        val b = channel(this.blue)
        return 0.2126 * r + 0.7152 * g + 0.0722 * b
    }

    val fgLum = this.relativeLuminance()
    val bgLum = background.relativeLuminance()

    return if (fgLum > bgLum)
        (fgLum + 0.05) / (bgLum + 0.05)
    else
        (bgLum + 0.05) / (fgLum + 0.05)
}

