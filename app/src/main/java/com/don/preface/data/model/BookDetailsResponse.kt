package com.don.preface.data.model

import kotlinx.serialization.Serializable

@Serializable
data class BookDetailsResponse(
    val kind: String,
    val id: String,
    val etag: String,
    val selfLink: String,
    val volumeInfo: VolumeInfoDet,
    val saleInfo: SaleInfo,
    val accessInfo: AccessInfo
)

@Serializable
data class VolumeInfoDet(
    val title: String,
    val authors: List<String>?,
    val publisher: String = "",
    val publishedDate: String?,
    val description: String?,
    val industryIdentifiers: List<IndustryIdentifier> = emptyList(),
    val readingModes: ReadingModes?,
    val pageCount: Int? = 0,
    val printedPageCount: Int? = 0,
    val dimensions: Dimensions?,
    val printType: String?,
    val categories: List<String> = emptyList(),
    val maturityRating: String?,
    val allowAnonLogging: Boolean? = false,
    val contentVersion: String?,
    val panelizationSummary: PanelizationSummary?,
    val imageLinks: ImageLinks?,
    val language: String?,
    val previewLink: String?,
    val infoLink: String?,
    val canonicalVolumeLink: String?
)

@Serializable
data class IndustryIdentifier(
    val type: String,
    val identifier: String
)

@Serializable
data class ReadingModes(
    val text: Boolean,
    val image: Boolean
)
@Serializable
data class Dimensions(
    val height: String = "",
    val width: String = "",
    val thickness: String?
)
@Serializable
data class PanelizationSummary(
    val containsEpubBubbles: Boolean,
    val containsImageBubbles: Boolean
)
@Serializable
data class SaleInfo(
    val country: String,
    val saleability: String,
    val isEbook: Boolean
)

@Serializable
data class AccessInfo(
    val country: String?,
    val viewability: String?,
    val embeddable: Boolean?,
    val publicDomain: Boolean?,
    val textToSpeechPermission: String?,
    val epub: Epub?,
    val pdf: Pdf?,
    val webReaderLink: String? = "",
    val accessViewStatus: String?,
    val quoteSharingAllowed: Boolean?
)

@Serializable
data class Epub(
    val isAvailable: Boolean
)

@Serializable
data class Pdf(
    val isAvailable: Boolean
)

