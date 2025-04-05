package ke.don.common_datasource.remote.domain.usecases

import android.util.Log
import ke.don.common_datasource.remote.domain.repositories.BooksRepository
import ke.don.common_datasource.remote.domain.repositories.BookshelfRepository
import ke.don.common_datasource.remote.domain.repositories.ProfileRepository
import ke.don.common_datasource.remote.domain.repositories.UserProgressRepository
import ke.don.common_datasource.remote.domain.states.BookshelvesState
import ke.don.common_datasource.remote.domain.states.UserProgressState
import ke.don.common_datasource.remote.domain.states.toBookshelfBookDetails
import ke.don.shared_domain.data_models.BookDetailsResponse
import ke.don.shared_domain.states.NetworkResult
import ke.don.shared_domain.states.ResultState

class BooksUseCasesImpl(
    private val booksRepository: BooksRepository,
    private val bookshelfRepository: BookshelfRepository,
    private val progressRepository: UserProgressRepository,
    private val profileRepository: ProfileRepository
): BooksUseCases {
    override suspend fun fetchBookDetails(bookId: String): NetworkResult<BookDetailsResponse> = booksRepository.getBookDetails(bookId)

    override suspend fun fetchAndMapBookshelves(bookId: String): BookshelvesState? {
        return try {
        if (bookId.isNotBlank()) {
                val bookshelves = booksRepository.fetchBookshelves()

                val bookshelfDetails = bookshelves.map { bookshelf ->
                    bookshelf.toBookshelfBookDetails(
                        isBookPresent = bookshelf.books.any { book ->
                            book.bookId == bookId
                        }
                    )
                }

                Log.d(TAG, "InitialState :: $bookshelfDetails")

                val newState = BookshelvesState(bookshelves = bookshelfDetails, resultState = ResultState.Success)

                newState
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching bookshelves: ${e.message}")
            null
        }
    }

    override suspend fun fetchBookProgress(bookId: String, userId: String): UserProgressState? {
        val progressResult = progressRepository.fetchBookProgressByUserAndBook(userId, bookId)

        return if (progressResult is NetworkResult.Success && progressResult.data != null) {
            UserProgressState(
                bookProgress = progressResult.data!!,
                resultState = ResultState.Success,
                isPresent = true
            )
        } else {
            null
        }
    }

    companion object{
        const val TAG = "BooksUseCasesImpl"
    }

}

interface BooksUseCases{
    suspend fun fetchBookDetails(bookId: String): NetworkResult<BookDetailsResponse>
    suspend fun fetchAndMapBookshelves(bookId: String): BookshelvesState?
    suspend fun fetchBookProgress(bookId: String, userId: String): UserProgressState?
}