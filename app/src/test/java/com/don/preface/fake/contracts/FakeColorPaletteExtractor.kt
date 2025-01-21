package com.don.preface.fake.contracts

import com.don.preface.domain.utils.color_utils.ColorPaletteExtractor
import com.don.preface.domain.utils.color_utils.model.ColorPallet

class FakeColorPaletteExtractor : ColorPaletteExtractor {
    override suspend fun extractColorPalette(imageUrl: String?): ColorPallet {
        return ColorPallet() // Return a default or mock object
    }

}
