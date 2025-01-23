package ke.don.feature_book_details.fake.data

import ke.don.feature_book_details.data.model.BookItem
import ke.don.feature_book_details.data.model.BookListItemResponse
import ke.don.feature_book_details.data.model.ImageLinks
import ke.don.feature_book_details.data.model.VolumeInfo
import ke.don.feature_book_details.presentation.screens.search.SearchState
import retrofit2.Response

object FakeBooksDataSource {

    val fakeBookListItemResponse: ke.don.feature_book_details.data.model.BookListItemResponse =
        ke.don.feature_book_details.data.model.BookListItemResponse(
            kind = "books#volumes",
            totalItems = 3,
            items = listOf(
                ke.don.feature_book_details.data.model.BookItem(
                    id = "1",
                    volumeInfo = ke.don.feature_book_details.data.model.VolumeInfo(
                        title = "The Art of Coding",
                        authors = listOf("Alice Johnson"),
                        publisher = "Tech Books Publishing",
                        publishedDate = "2020-09-15",
                        description = "A comprehensive guide to mastering the art of coding, from basics to advanced techniques.",
                        imageLinks = ke.don.feature_book_details.data.model.ImageLinks(
                            smallThumbnail = "https://example.com/image1_small.jpg",
                            thumbnail = "https://example.com/image1_thumb.jpg",
                            small = "https://example.com/image1_small.jpg",
                            medium = "https://example.com/image1_medium.jpg",
                            large = "https://example.com/image1_large.jpg",
                            extraLarge = "https://example.com/image1_extra_large.jpg"
                        ),
                        previewLink = "https://example.com/book1_preview"
                    )
                ),
                ke.don.feature_book_details.data.model.BookItem(
                    id = "2",
                    volumeInfo = ke.don.feature_book_details.data.model.VolumeInfo(
                        title = "Journey Through Data",
                        authors = listOf("Michael Reed", "Sophia Chen"),
                        publisher = "Data Insights Inc.",
                        publishedDate = "2018-06-23",
                        description = "An exploration of data science and analytics, covering key concepts, techniques, and applications.",
                        imageLinks = ke.don.feature_book_details.data.model.ImageLinks(
                            smallThumbnail = "https://example.com/image2_small.jpg",
                            thumbnail = "https://example.com/image2_thumb.jpg",
                            small = "https://example.com/image2_small.jpg",
                            medium = "https://example.com/image2_medium.jpg",
                            large = "https://example.com/image2_large.jpg",
                            extraLarge = "https://example.com/image2_extra_large.jpg"
                        ),
                        previewLink = "https://example.com/book2_preview"
                    )
                ),
                ke.don.feature_book_details.data.model.BookItem(
                    id = "3",
                    volumeInfo = ke.don.feature_book_details.data.model.VolumeInfo(
                        title = "Mastering Android Development",
                        authors = listOf("Emma Brown"),
                        publisher = "Mobile Dev Press",
                        publishedDate = "2021-01-10",
                        description = "A practical guide to Android development, covering Jetpack Compose, architecture, and modern tools.",
                        imageLinks = ke.don.feature_book_details.data.model.ImageLinks(
                            smallThumbnail = "https://example.com/image3_small.jpg",
                            thumbnail = "https://example.com/image3_thumb.jpg",
                            small = "https://example.com/image3_small.jpg",
                            medium = "https://example.com/image3_medium.jpg",
                            large = "https://example.com/image3_large.jpg",
                            extraLarge = "https://example.com/image3_extra_large.jpg"
                        ),
                        previewLink = "https://example.com/book3_preview"
                    )
                )
            )
        )

    val fakeBookList: Response<BookListItemResponse> = Response.success(
        fakeBookListItemResponse
    )

    val fakeSearchSuccessState = SearchState.Success(
        fakeBookList.body()?.items ?: emptyList()
    )

    val fakeSearchErrorResponseState = SearchState.Error("Failed with status: 404")

    val fakeSearchErrorState = SearchState.Error("An error occurred. Check your internet and try again")

}