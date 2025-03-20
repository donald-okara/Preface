package ke.don.common_datasource.remote.data.book_details.repositoryImpl

import android.util.Log
import ke.don.common_datasource.local.roomdb.dao.BookshelfDao
import ke.don.common_datasource.remote.domain.states.BookUiState
import ke.don.common_datasource.remote.data.book_details.network.GoogleBooksApi
import ke.don.common_datasource.remote.domain.repositories.BooksRepository
import ke.don.common_datasource.remote.domain.repositories.BookshelfRepository
import ke.don.common_datasource.remote.domain.states.BookshelfBookDetailsState
import ke.don.common_datasource.remote.domain.states.BookshelvesState
import ke.don.common_datasource.remote.domain.states.toAddBookToBookshelf
import ke.don.common_datasource.remote.domain.states.toBookshelfBookDetails
import ke.don.common_datasource.remote.domain.states.toSupabaseBook
import ke.don.shared_domain.data_models.BookDetailsResponse
import ke.don.shared_domain.data_models.BookItem
import ke.don.shared_domain.logger.Logger
import ke.don.shared_domain.states.NetworkResult
import ke.don.shared_domain.states.ResultState
import ke.don.shared_domain.utils.color_utils.ColorPaletteExtractor
import ke.don.shared_domain.utils.color_utils.model.ColorPallet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import retrofit2.Response

class BooksRepositoryImpl(
    private val bookshelfRepository: BookshelfRepository,
    private val googleBooksApi: GoogleBooksApi,
    private val apiKey: String,
    private val bookshelfDao: BookshelfDao,
    private val colorPaletteExtractor: ColorPaletteExtractor,
    private val logger: Logger
) : BooksRepository {

    /**
     * BookDetails vals
     */
    private val _bookState = MutableStateFlow(BookUiState())
    override val bookUiState: StateFlow<BookUiState> = _bookState

    private var initialBookshelves: List<BookshelfBookDetailsState> = emptyList()

    private var initialBookState = BookUiState()


    override fun updateBookState(newState: BookUiState) {
        _bookState.update { newState }
    }

    override suspend fun getBookDetails(bookId: String) {
        // Set the loading state
        updateBookState(
            BookUiState(
                resultState = ResultState.Loading
            )
        )

        try {
            // Fetch book details from the API
            val response: Response<BookDetailsResponse> =
                googleBooksApi.getBookDetails(bookId, apiKey)

            if (response.isSuccessful) {
                response.body()?.let { volumeData ->
                    // Extract the highest image URL from the book details
                    val highestImageUrl = volumeData.volumeInfo.imageLinks.let {
                        it.extraLarge ?: it.large ?: it.medium ?: it.small ?: it.thumbnail
                        ?: it.smallThumbnail
                    }?.replace("http", "https")


                    val colorPallet = if (highestImageUrl.isNullOrEmpty()) {
                        ColorPallet()
                    } else {
                        colorPaletteExtractor.extractColorPalette(highestImageUrl)
                    }

                    // Update the UI state with book details and the highest image URL
                    updateBookState(
                        BookUiState(
                            bookDetails = volumeData,
                            colorPallet = colorPallet,
                            highestImageUrl = highestImageUrl, // Set the highest image URL
                            resultState = ResultState.Success
                        )
                    )
                    fetchBookshelves()


                    Log.d(TAG, "UIState ${bookUiState.value}")

                } ?: run {
                    // Handle case where no data is available
                    updateBookState(
                        BookUiState(
                            resultState = ResultState.Error("No data available")
                        )
                    )
                }
            } else {
                // Handle unsuccessful response
                updateBookState(
                    BookUiState(
                        resultState = ResultState.Error("Failed to load book details")
                    )
                )
            }

        } catch (e: Exception) {
            // Handle network error
            updateBookState(
                BookUiState(
                    resultState = ResultState.Error("Network error. Check your internet and try again")
                )
            )
        }
    }

    private suspend fun fetchBookshelves() {
        Log.d(TAG, "Fetching bookshelves")
        val currentState = bookUiState.value
        withContext(Dispatchers.IO) {
            val bookshelves = bookshelfDao.getAllBookshelves().map {
                it.toBookshelfBookDetails(
                    isBookPresent = it.books.any { book -> book.bookId == currentState.bookDetails.id }
                )
            }

            updateBookState(
                currentState.copy(
                    bookshelvesState = BookshelvesState(bookshelves = bookshelves)
                )
            )

            initialBookState = bookUiState.value

            bookshelves.forEach { bookshelf ->
                Log.d(TAG, "Bookshelf: ${bookshelf.bookshelfBookDetails.name}, isBookPresent: ${bookshelf.isBookPresent}")
            }
        }

    }

    override fun onBookshelfSelected(bookshelfId: Int) {
        updateBookState(
            _bookState.value.copy(
                bookshelvesState = bookUiState.value.bookshelvesState.copy(
                    bookshelves = bookUiState.value.bookshelvesState.bookshelves.map { bookshelf ->
                        if (bookshelf.bookshelfBookDetails.id == bookshelfId) {
                            bookshelf.copy(isBookPresent = !bookshelf.isBookPresent)
                        } else {
                            bookshelf
                        }
                    }
                )
            )
        )

    }



    override suspend fun pushEditedBookshelfBooks(): Boolean {
        return withContext(Dispatchers.IO) { // Run all database operations on IO thread
            try {
                bookUiState.value.bookshelvesState.bookshelves.forEach { currentBookshelf ->
                    val initialBookshelf = initialBookState.bookshelvesState.bookshelves
                        .firstOrNull { it.bookshelfBookDetails.id == currentBookshelf.bookshelfBookDetails.id }

                    if (initialBookshelf != null) {
                        if (currentBookshelf.isBookPresent && !initialBookshelf.isBookPresent) {
                            Log.d(TAG, "Book is not present. Attempting to add book")
                            if (bookshelfRepository.addBookToBookshelf(
                                    bookUiState.value.toAddBookToBookshelf(
                                        bookshelfId = currentBookshelf.bookshelfBookDetails.id,
                                        bookUiState.value
                                    )
                                ) == ResultState.Success
                            ) {
                                bookshelfDao.addBookToBookshelf(
                                    bookshelfId = currentBookshelf.bookshelfBookDetails.id,
                                    book = bookUiState.value.toSupabaseBook()
                                )
                            } else {
                                Log.e(TAG, "Failed to add book to bookshelf ${currentBookshelf.bookshelfBookDetails.id}")
                                return@withContext false
                            }
                        } else if (!currentBookshelf.isBookPresent && initialBookshelf.isBookPresent) {
                            Log.d(TAG, "Book is present. Attempting to remove book")
                            if (bookshelfRepository.removeBookFromBookshelf(
                                    bookshelfId = currentBookshelf.bookshelfBookDetails.id,
                                    bookId = bookUiState.value.bookDetails.id
                                ) == ResultState.Success
                            ) {
                                bookshelfDao.removeBookFromBookshelf(
                                    bookshelfId = currentBookshelf.bookshelfBookDetails.id,
                                    bookId = bookUiState.value.bookDetails.id
                                )
                            } else {
                                Log.e(TAG, "Failed to remove book from bookshelf ${currentBookshelf.bookshelfBookDetails.id}")
                                return@withContext false
                            }
                        }
                    }
                }
                fetchBookshelves() // Already uses Dispatchers.IO internally
                true
            } catch (e: Exception) {
                Log.e(TAG, "Error pushing bookshelf updates: ${e.message}", e)
                false
            }
        }
    }


    /**
     *   SearchBook funs
     *
     */

    // New function to get a random book suggestion

    override suspend fun searchBooks(query: String): NetworkResult<List<BookItem>> {
        return try {
            val response = googleBooksApi.searchBooks(query, apiKey)

            if (response.isSuccessful) {
                NetworkResult.Success(response.body()?.items ?: emptyList())
            } else {
                NetworkResult.Error(
                    message = response.message(),
                    code = response.code().toString(),
                    hint = response.code().toString(),
                    details = response.body().toString()
                )
            }
        } catch (e: Exception) {
            NetworkResult.Error(
                message = "Something went wrong. Please check your internet and try again",
            )
        }
    }
//        updateSearchState(SearchState.Loading)
//        try {
//            val response = googleBooksApi.searchBooks(query, apiKey)
//            if (response.isSuccessful) {
//                updateSearchState(
//                    SearchState.Success(
//                        response.body()?.items ?: emptyList()
//                    )
//                )
//
//            } else {
//                updateSearchState(
//                    SearchState.Error("Failed with status: ${response.code()}")
//                )
//
//            }
//        } catch (e: Exception) {
//            updateSearchState(
//                SearchState.Error("An error occurred. Check your internet and try again")
//
//            )
//
//        }



    companion object {
        const val TAG = "BooksRepositoryImpl"
    }

}