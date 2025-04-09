package ke.don.feature_book_details.fake.data

import ke.don.common_datasource.local.roomdb.entities.BookshelfEntity
import ke.don.common_datasource.remote.domain.states.BookshelfBookDetailsState
import ke.don.common_datasource.remote.domain.states.BookshelvesState
import ke.don.shared_domain.data_models.BookSource
import ke.don.shared_domain.data_models.SupabaseBook
import ke.don.shared_domain.states.ResultState

object FakeBookshelfState {
        val fakeBookshelvesState = BookshelvesState(
        bookshelves = listOf(
            BookshelfBookDetailsState(
                bookshelfBookDetails = BookshelfEntity(
                    id = 1,
                    name = "Favorites",
                    description = "My favorite books",
                    bookshelfType = "custom",
                    userId = "user123",
                    books = listOf(
                        SupabaseBook(
                            id = 1,
                            bookId = "book123",
                            title = "The Art of Testing",
                            description = "A comprehensive guide to writing unit tests.",
                            highestImageUrl = "https://example.com/high.jpg",
                            lowestImageUrl = "https://example.com/low.jpg",
                            source = BookSource.Google,
                            authors = listOf("Jane Doe", "John Smith"),
                            publisher = "TestPress",
                            publishedDate = "2023-05-15",
                            categories = listOf("Technology", "Programming"),
                            maturityRating = "NOT_MATURE",
                            language = "en",
                            previewLink = "https://books.example.com/preview/book123",
                            pageCount = 320
                        )
                    )
                ),
                isSelected = true,
                isBookPresent = true
            ),
            BookshelfBookDetailsState(
                bookshelfBookDetails = BookshelfEntity(
                    id = 2,
                    name = "To Read",
                    description = "Books I plan to read",
                    bookshelfType = "custom",
                    userId = "user123",
                    books = emptyList()
                ),
                isSelected = false,
                isBookPresent = false
            )
        ),
        resultState = ResultState.Success
    )

}