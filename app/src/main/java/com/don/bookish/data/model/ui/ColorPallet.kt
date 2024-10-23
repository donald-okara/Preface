package com.don.bookish.data.model.ui

import androidx.compose.ui.graphics.Color

data class ColorPallet(
    var dominantColor: Color = Color.Transparent,
    var darkMutedColor: Color = Color.Transparent,
    var darkVibrantColor: Color = Color.Transparent,
    var lightMutedColor: Color = Color.Transparent,
    var lightVibrantColor: Color = Color.Transparent
)