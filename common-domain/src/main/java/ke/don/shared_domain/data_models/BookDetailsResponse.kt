package ke.don.shared_domain.data_models

import kotlinx.serialization.Serializable

@Serializable
data class BookDetailsResponse( // Google book api
    val kind: String = "",
    val id: String = "",
    val etag: String = "",
    val selfLink: String = "",
    val volumeInfo: VolumeInfoDet = VolumeInfoDet(),
    val saleInfo: SaleInfo = SaleInfo(),
    val accessInfo: AccessInfo = AccessInfo()
) {
    fun toSupabaseBook(): SupabaseBook {
        return SupabaseBook(
            bookId = id,
            title = volumeInfo.title,
            description = volumeInfo.description,
            highestImageUrl = volumeInfo.imageLinks.getHighestQualityUrl()?.replace("http", "https"),
            lowestImageUrl = volumeInfo.imageLinks.getLowestQualityUrl()?.replace("http", "https"),
            source = BookSource.Google,
            authors = volumeInfo.authors,
            publisher = volumeInfo.publisher,
            publishedDate = volumeInfo.publishedDate,
            categories = volumeInfo.categories,
            maturityRating = volumeInfo.maturityRating,
            language = volumeInfo.language,
            previewLink = volumeInfo.previewLink,
            pageCount = volumeInfo.pageCount
        )
    }
}

@Serializable
data class VolumeInfoDet(
    val title: String = "",
    val authors: List<String> = emptyList(),
    val publisher: String = "",
    val publishedDate: String = "",
    val description: String = "",
    val industryIdentifiers: List<IndustryIdentifier> = emptyList(),
    val readingModes: ReadingModes = ReadingModes(),
    val pageCount: Int = 0,
    val printedPageCount: Int = 0,
    val dimensions: Dimensions = Dimensions(),
    val printType: String = "",
    val categories: List<String> = emptyList(),
    val maturityRating: String = "",
    val allowAnonLogging: Boolean = false,
    val contentVersion: String = "",
    val panelizationSummary: PanelizationSummary = PanelizationSummary(),
    val imageLinks: ImageLinks = ImageLinks(),
    val language: String = "",
    val previewLink: String= "",
    val infoLink: String = "",
    val canonicalVolumeLink: String = ""
)

@Serializable
data class IndustryIdentifier(
    val type: String,
    val identifier: String
)

@Serializable
data class ReadingModes(
    val text: Boolean = false,
    val image: Boolean = false
)
@Serializable
data class Dimensions(
    val height: String = "",
    val width: String = "",
    val thickness: String = ""
)
@Serializable
data class PanelizationSummary(
    val containsEpubBubbles: Boolean = false,
    val containsImageBubbles: Boolean = false
)
@Serializable
data class SaleInfo(
    val country: String = "",
    val saleability: String = "",
    val isEbook: Boolean = false
)

@Serializable
data class AccessInfo(
    val country: String = "",
    val viewability: String = "",
    val embeddable: Boolean = false,
    val publicDomain: Boolean = false,
    val textToSpeechPermission: String = "",
    val epub: Epub = Epub(),
    val pdf: Pdf = Pdf(),
    val webReaderLink: String = "",
    val accessViewStatus: String = "",
    val quoteSharingAllowed: Boolean = false
)

@Serializable
data class Epub(
    val isAvailable: Boolean = false
)

@Serializable
data class Pdf(
    val isAvailable: Boolean = false
)

