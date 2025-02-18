package ke.don.feature_book_details.fake.data

import ke.don.shared_domain.data_models.AccessInfo
import ke.don.shared_domain.data_models.BookDetailsResponse
import ke.don.shared_domain.data_models.Dimensions
import ke.don.shared_domain.data_models.Epub
import ke.don.shared_domain.data_models.ImageLinks
import ke.don.shared_domain.data_models.IndustryIdentifier
import ke.don.shared_domain.data_models.PanelizationSummary
import ke.don.shared_domain.data_models.Pdf
import ke.don.shared_domain.data_models.ReadingModes
import ke.don.shared_domain.data_models.SaleInfo
import ke.don.shared_domain.data_models.VolumeInfoDet
import retrofit2.Response

object FakeBookDetailsDataSource {
    val fakeBookDetailsResponse: BookDetailsResponse =
        BookDetailsResponse(
            kind = "books#volume",
            id = "5cu7sER89nwC",
            etag = "sxFXNictMnU",
            selfLink = "https://www.googleapis.com/books/v1/volumes/5cu7sER89nwC",
            volumeInfo = VolumeInfoDet(
                title = "Gone Girl",
                authors = listOf("Gillian Flynn"),
                publisher = "Phoenix",
                publishedDate = "2012",
                description = "On a warm summer morning in North Carthage, Missouri, it is Nick and Amy Dunne's fifth wedding anniversary. Presents are being wrapped and reservations are being made when Nick's clever and beautiful wife disappears from their rented McMansion on the Mississippi River.",
                industryIdentifiers = listOf(
                    IndustryIdentifier(
                        type = "ISBN_10",
                        identifier = "1780221355"
                    ),
                    IndustryIdentifier(
                        type = "ISBN_13",
                        identifier = "9781780221359"
                    )
                ),
                readingModes = ReadingModes(
                    text = false,
                    image = false
                ),
                pageCount = 466,
                printedPageCount = 484,
                imageLinks = ImageLinks(),
                dimensions = Dimensions(
                    height = "18.00 cm",
                    width = "11.50 cm",
                    thickness = "3.80 cm"
                ),
                printType = "BOOK",
                categories = listOf(
                    "Fiction / Mystery & Detective / General",
                    "Fiction / Thrillers / Suspense",
                    "Fiction / Family Life / Marriage & Divorce"
                ),
                maturityRating = "NOT_MATURE",
                allowAnonLogging = false,
                contentVersion = "0.3.0.0.preview.0",
                panelizationSummary = PanelizationSummary(
                    containsEpubBubbles = false,
                    containsImageBubbles = false
                ),
                language = "en",
                previewLink = "http://books.google.com/books?id=5cu7sER89nwC&hl=&source=gbs_api",
                infoLink = "https://play.google.com/store/books/details?id=5cu7sER89nwC&source=gbs_api",
                canonicalVolumeLink = "https://play.google.com/store/books/details?id=5cu7sER89nwC"
            ),

            saleInfo = SaleInfo(
                country = "KE",
                saleability = "NOT_FOR_SALE",
                isEbook = false
            ),
            accessInfo = AccessInfo(
                country = "KE",
                viewability = "NO_PAGES",
                embeddable = false,
                publicDomain = false,
                textToSpeechPermission = "ALLOWED",
                epub = Epub(isAvailable = false),
                pdf = Pdf(isAvailable = false),
                webReaderLink = "http://play.google.com/books/reader?id=5cu7sER89nwC&hl=&source=gbs_api",
                accessViewStatus = "NONE",
                quoteSharingAllowed = false
            )
        )

    val fakeBookDetails: Response<BookDetailsResponse> = Response.success(
        ke.don.feature_book_details.fake.data.FakeBookDetailsDataSource.fakeBookDetailsResponse
    )

}