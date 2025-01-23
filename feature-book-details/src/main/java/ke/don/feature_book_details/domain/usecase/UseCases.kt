package ke.don.feature_book_details.domain.usecase

import ke.don.feature_book_details.domain.repositories.BooksRepository

class BooksUseCases(
    private val booksRepository: BooksRepository,
) {
    fun clearSearch() = booksRepository.clearSearch()

    fun onSearchQueryChange(query: String) = booksRepository.onSearchQueryChange(query)

    fun shuffleBook() = booksRepository.shuffleBook()

    suspend fun onSearch() = booksRepository.onSearch()

    suspend fun getBookDetails(bookId: String) = booksRepository.getBookDetails(bookId)


}