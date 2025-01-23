package ke.don.feature_book_details.fake.contracts

import ke.don.shared_domain.utils.color_utils.ColorPaletteExtractor
import ke.don.shared_domain.utils.color_utils.model.ColorPallet

class FakeColorPaletteExtractor :
    ColorPaletteExtractor {
    override suspend fun extractColorPalette(imageUrl: String?): ColorPallet {
        return ColorPallet() // Return a default or mock object
    }

}
