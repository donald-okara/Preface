package com.don.bookish.fake

import com.don.bookish.data.model.AccessInfo
import com.don.bookish.data.model.Dimensions
import com.don.bookish.data.model.Epub
import com.don.bookish.data.model.ImageLinks
import com.don.bookish.data.model.IndustryIdentifier
import com.don.bookish.data.model.PanelizationSummary
import com.don.bookish.data.model.Pdf
import com.don.bookish.data.model.ReadingModes
import com.don.bookish.data.model.SaleInfo
import com.don.bookish.data.model.VolumeData
import com.don.bookish.data.model.VolumeInfo
import com.don.bookish.data.model.VolumeInfoDet


fun createFakeVolumeData(): VolumeData {
    return VolumeData(
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
                IndustryIdentifier(type = "ISBN_10", identifier = "1780221355"),
                IndustryIdentifier(type = "ISBN_13", identifier = "9781780221359")
            ),
            readingModes = ReadingModes(text = false, image = false),
            pageCount = 466,
            printedPageCount = 484,
            dimensions = Dimensions(height = "18.00 cm", width = "11.50 cm", thickness = "3.80 cm"),
            printType = "BOOK",
            categories = listOf("Fiction / Mystery & Detective / General", "Fiction / Thrillers / Suspense", "Fiction / Family Life / Marriage & Divorce"),
            maturityRating = "NOT_MATURE",
            allowAnonLogging = false,
            contentVersion = "0.3.0.0.preview.0",
            panelizationSummary = PanelizationSummary(containsEpubBubbles = false, containsImageBubbles = false),
            imageLinks = ImageLinks(
                smallThumbnail = "http://books.google.com/books/content?id=5cu7sER89nwC&printsec=frontcover&img=1&zoom=5",
                thumbnail = "http://books.google.com/books/content?id=5cu7sER89nwC&printsec=frontcover&img=1&zoom=1",
                small = "http://books.google.com/books/content?id=5cu7sER89nwC&printsec=frontcover&img=1&zoom=2",
                medium = "http://books.google.com/books/content?id=5cu7sER89nwC&printsec=frontcover&img=1&zoom=3",
                large = "http://books.google.com/books/content?id=5cu7sER89nwC&printsec=frontcover&img=1&zoom=4",
                extraLarge = "http://books.google.com/books/content?id=5cu7sER89nwC&printsec=frontcover&img=1&zoom=6"
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
}