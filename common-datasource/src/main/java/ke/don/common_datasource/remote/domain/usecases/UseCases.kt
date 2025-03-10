package ke.don.common_datasource.remote.domain.usecases

import ke.don.common_datasource.remote.domain.repositories.BooksRepository
import ke.don.common_datasource.remote.domain.repositories.BookshelfRepository

class BooksUseCases(
    private val booksRepository: BooksRepository,
    private val bookshelfRepository: BookshelfRepository,
) {
    fun clearSearch() = booksRepository.clearSearch()

    fun onSearchQueryChange(query: String) = booksRepository.onSearchQueryChange(query)

    fun shuffleBook() = booksRepository.shuffleBook()

    suspend fun onSearch() = booksRepository.onSearch()

    suspend fun getBookDetails(bookId: String) = booksRepository.getBookDetails(bookId)

    suspend fun onPushEditedBookshelfBooks(): Boolean = booksRepository.pushEditedBookshelfBooks()

    fun onSelectBookshelf(bookshelfId: Int) = booksRepository.onBookshelfSelected(bookshelfId)

}