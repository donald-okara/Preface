package ke.don.common_datasource.remote.domain.usecases

import ke.don.common_datasource.remote.domain.repositories.BooksRepository
import ke.don.common_datasource.remote.domain.repositories.BookshelfRepository
import ke.don.shared_domain.data_models.BookDetailsResponse
import ke.don.shared_domain.states.NetworkResult

class BooksUseCases(
    private val booksRepository: BooksRepository,
    private val bookshelfRepository: BookshelfRepository,
) {
    suspend fun getBookDetails(bookId: String): NetworkResult<BookDetailsResponse> = booksRepository.getBookDetails(bookId)


}