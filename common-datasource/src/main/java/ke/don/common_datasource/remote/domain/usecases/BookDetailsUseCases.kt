package ke.don.common_datasource.remote.domain.usecases

import android.util.Log
import ke.don.common_datasource.local.datastore.profile.ProfileDataStoreManager
import ke.don.common_datasource.remote.domain.repositories.BooksRepository
import ke.don.common_datasource.remote.domain.repositories.UserProgressRepository
import ke.don.common_datasource.remote.domain.states.BookshelvesState
import ke.don.common_datasource.remote.domain.states.NoDataReturned
import ke.don.common_datasource.remote.domain.states.UserProgressState
import ke.don.common_datasource.remote.domain.states.toBookshelfBookDetails
import ke.don.shared_domain.data_models.BookDetailsResponse
import ke.don.shared_domain.data_models.CreateUserProgressDTO
import ke.don.shared_domain.states.NetworkResult
import ke.don.shared_domain.states.ResultState

class BooksUseCasesImpl(
    private val booksRepository: BooksRepository,
    private val profileDataStoreManager: ProfileDataStoreManager,
    private val progressRepository: UserProgressRepository,
): BooksUseCases {
    override suspend fun checkAndAddBook(book: BookDetailsResponse): NetworkResult<NoDataReturned> = booksRepository.checkAndAddBook(book)

    override suspend fun addUserProgress(userProgress: CreateUserProgressDTO): NetworkResult<NoDataReturned> = progressRepository.addUserProgress(userProgress)

    override suspend fun updateUserProgress(
        userId: String,
        bookId: String,
        newCurrentPage: Int
    ): NetworkResult<NoDataReturned> = progressRepository.updateUserProgress(userId, bookId, newCurrentPage)

    override suspend fun fetchProfileId(): String = profileDataStoreManager.getProfileFromDatastore().authId

    override suspend fun pushEditedBookshelfBooks(
        bookId: String,
        removeBookshelfIds: List<Int>,
        addBookshelves: List<Int>
    ): NetworkResult<NoDataReturned> = booksRepository.pushEditedBookshelfBooks(bookId, removeBookshelfIds, addBookshelves)

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
                newProgress = progressResult.data!!.currentPage,
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

interface BooksUseCases {
    suspend fun checkAndAddBook(book: BookDetailsResponse): NetworkResult<NoDataReturned>
    suspend fun addUserProgress(userProgress: CreateUserProgressDTO): NetworkResult<NoDataReturned>
    suspend fun updateUserProgress(userId: String, bookId: String, newCurrentPage: Int) : NetworkResult<NoDataReturned>
    suspend fun fetchProfileId() : String
    suspend fun pushEditedBookshelfBooks(bookId: String, removeBookshelfIds: List<Int>, addBookshelves: List<Int>): NetworkResult<NoDataReturned>
    suspend fun fetchBookDetails(bookId: String): NetworkResult<BookDetailsResponse>
    suspend fun fetchAndMapBookshelves(bookId: String): BookshelvesState?
    suspend fun fetchBookProgress(bookId: String, userId: String): UserProgressState?
}