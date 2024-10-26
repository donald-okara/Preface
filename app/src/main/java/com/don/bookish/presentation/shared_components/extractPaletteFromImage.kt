package com.don.bookish.presentation.shared_components

import okhttp3.OkHttpClient
import okhttp3.Request
import com.don.bookish.data.model.ui.ColorPallet
import java.io.InputStream
import androidx.palette.graphics.Palette
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun downloadImage(url: String): InputStream? {
    val client = OkHttpClient()
    val request = Request.Builder().url(url).build()
    return client.newCall(request).execute().body?.byteStream()
}


fun extractPaletteFromImage(inputStream: InputStream): Palette {
    val bitmap = BitmapFactory.decodeStream(inputStream)
    return Palette.from(bitmap).generate()
}


fun getColorHashCode(color: Color): Int {
    // Extract ARGB components from the Color object
    val alpha = (color.alpha * 255).toInt()
    val red = (color.red * 255).toInt()
    val green = (color.green * 255).toInt()
    val blue = (color.blue * 255).toInt()

    // Combine the ARGB values into a single hash code
    return (alpha shl 24) or (red shl 16) or (green shl 8) or blue
}

suspend fun extractColorPalette(lowestImageUrl: String?): ColorPallet {
    return withContext(Dispatchers.IO) {
        val inputStream = lowestImageUrl?.let { downloadImage(it) }
            ?: throw IllegalArgumentException("Image URL is null or image could not be downloaded.")

        val palette = extractPaletteFromImage(inputStream)
        ColorPallet(
            dominantColor = Color(palette.getDominantColor(Color.Transparent.value.toInt())),
            darkMutedColor = Color(palette.getDarkMutedColor(Color.Transparent.value.toInt())),
            darkVibrantColor = Color(palette.getDarkVibrantColor(Color.Transparent.value.toInt())),
            lightMutedColor = Color(palette.getLightMutedColor(Color.Transparent.value.toInt())),
            lightVibrantColor = Color(palette.getLightVibrantColor(Color.Transparent.value.toInt()))
        ).also { colorPalette ->
            Log.d("BookImage", "Dominant color: ${colorPalette.dominantColor}")
        }
    }
}


