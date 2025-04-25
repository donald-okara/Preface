package ke.don.feature_bookshelf.fake

import ke.don.shared_domain.data_models.BookJson
import ke.don.shared_domain.data_models.BookShelf
import ke.don.shared_domain.data_models.BookshelfRef
import ke.don.shared_domain.data_models.SupabaseBook

object FakeBookshelf {
    val fictionBookshelfRef = BookshelfRef(
        id = 1,
        name = "Fiction Favorites",
        description = "A collection of thrilling fiction books.",
        bookshelfType = "fiction",
        userId = "user_001",
        books = listOf(
            BookJson(bookId = "book_001"),
            BookJson(bookId = "book_002")
        )
    )

    val fictionBooks = listOf(
        SupabaseBook(bookId = "book_001", title = "The Silent Patient", authors = listOf("Alex Michaelides")),
        SupabaseBook(bookId = "book_002", title = "The Midnight Library", authors = listOf("Matt Haig"))
    )

    val scienceBookshelfRef = BookshelfRef(
        id = 2,
        name = "Science & Learning",
        description = "Books to expand your mind.",
        bookshelfType = "non-fiction",
        userId = "user_002",
    )

    val scienceBooks = listOf(
        SupabaseBook(bookId = "book_003", title = "Sapiens", authors = listOf("Yuval Noah Harari") )
    )

    val emptyBookshelfRef = BookshelfRef(
        id = 3,
        name = "Empty Shelf",
        description = "This shelf is still under construction.",
        bookshelfType = "misc",
        userId = "user_003",
        books = emptyList()
    )

    val emptyBooks = emptyList<SupabaseBook>()

    val fictionBookshelf = BookShelf(
        bookshelfRef = fictionBookshelfRef,
        books = fictionBooks
    )

    val scienceBookshelf = BookShelf(
        bookshelfRef = scienceBookshelfRef,
        books = scienceBooks
    )

    val emptyBookshelf = BookShelf(
        bookshelfRef = emptyBookshelfRef,
        books = emptyBooks
    )

    val fakeBookShelves = listOf(
        fictionBookshelf,
        scienceBookshelf,
        emptyBookshelf
    )

}