package ke.don.common_datasource.remote.domain.usecases

import ke.don.common_datasource.remote.domain.repositories.BooksRepository
import ke.don.common_datasource.remote.domain.repositories.BookshelfRepository

class BooksUseCases(
    private val booksRepository: BooksRepository,
    private val bookshelfRepository: BookshelfRepository,
) {
    suspend fun getBookDetails(bookId: String) = booksRepository.getBookDetails(bookId)

    suspend fun onPushEditedBookshelfBooks(): Boolean = booksRepository.pushEditedBookshelfBooks()

    suspend fun searchBooks(query: String) = booksRepository.searchBooks(query)

    fun onSelectBookshelf(bookshelfId: Int) = booksRepository.onBookshelfSelected(bookshelfId)

}