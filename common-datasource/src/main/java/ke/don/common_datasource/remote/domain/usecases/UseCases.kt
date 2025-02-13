package ke.don.common_datasource.remote.domain.usecases

import ke.don.common_datasource.remote.domain.repositories.BooksRepository

class BooksUseCases(
    private val booksRepository: BooksRepository,
) {
    fun clearSearch() = booksRepository.clearSearch()

    fun onSearchQueryChange(query: String) = booksRepository.onSearchQueryChange(query)

    fun shuffleBook() = booksRepository.shuffleBook()

    suspend fun onSearch() = booksRepository.onSearch()

    suspend fun getBookDetails(bookId: String) = booksRepository.getBookDetails(bookId)


}