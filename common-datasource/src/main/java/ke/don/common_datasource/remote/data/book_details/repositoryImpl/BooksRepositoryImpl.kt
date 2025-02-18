package ke.don.common_datasource.remote.data.book_details.repositoryImpl

import android.util.Log
import ke.don.shared_domain.data_models.BookDetailsResponse
import ke.don.common_datasource.remote.domain.repositories.BooksRepository
import ke.don.shared_domain.states.BookUiState
import ke.don.shared_domain.states.ResultState
import ke.don.shared_domain.states.SearchState
import ke.don.shared_domain.states.searchMessages
import ke.don.shared_domain.states.suggestedBookTitles
import ke.don.common_datasource.remote.data.book_details.network.GoogleBooksApi
import ke.don.common_datasource.remote.domain.repositories.BookshelfRepository
import ke.don.shared_domain.logger.Logger
import ke.don.shared_domain.states.BookshelvesState
import ke.don.shared_domain.states.toAddBookToBookshelf
import ke.don.shared_domain.states.toBookshelfBookDetails
import ke.don.shared_domain.utils.color_utils.ColorPaletteExtractor
import ke.don.shared_domain.utils.color_utils.model.ColorPallet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import retrofit2.Response
import kotlin.random.Random

class BooksRepositoryImpl(
    private val bookshelfRepository: BookshelfRepository,
    private val googleBooksApi: GoogleBooksApi,
    private val apiKey: String,
    private val colorPaletteExtractor: ColorPaletteExtractor,
    private val logger: Logger
) : BooksRepository {

    /**
     * BookDetails vals
     */
    private val _bookState = MutableStateFlow(BookUiState())
    override val bookUiState: StateFlow<BookUiState> = _bookState

    private var initialBookState = BookUiState()
    /**
     * SearchBook vals
     */
    private var _searchUiState = MutableStateFlow<SearchState>(SearchState.Empty)
    override val searchUiState: StateFlow<SearchState> = _searchUiState

    private var _searchQuery = MutableStateFlow("")
    override var searchQuery : StateFlow<String> = _searchQuery

    private var _suggestedBook = MutableStateFlow("")
    override var suggestedBook : StateFlow<String> = _suggestedBook

    private var _searchMessage = MutableStateFlow("")
    override var searchMessage : StateFlow<String> = _searchMessage

    override val userLibraryState = bookshelfRepository.userLibraryState

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

    private fun fetchBookshelves(){
        Log.d(TAG, "Fetching bookshelves")
        val bookshelves = userLibraryState.value.userBookshelves

        val bookshelfBookDetails = bookshelves.map { bookShelf ->
            // For each bookshelf, check if the current bookshelf contains the book
            val isBookPresent = bookShelf.catalog.any { catalog ->
                catalog.bookId == bookUiState.value.bookDetails.id
            }

            // Map each bookshelf to its details with the correct `isBookPresent` flag
            bookShelf.toBookshelfBookDetails(
                bookshelf = bookShelf,
                isBookPresent = isBookPresent
            )
        }

        Log.d(TAG, "Bookshelves fetched successfully: $bookshelfBookDetails")

        val bookshelfBookState = BookshelvesState(
            bookshelves = bookshelfBookDetails
        )

        updateBookState(
            bookUiState.value.copy(
                bookshelvesState = bookshelfBookState
            )
        )
        initialBookState = bookUiState.value
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

    override suspend fun pushEditedBookshelfBooks() {
        // Iterate over the bookshelves in the current book state
        bookUiState.value.bookshelvesState.bookshelves.forEach { currentBookshelf ->
            // Find the corresponding bookshelf from the initial state
            val initialBookshelf = initialBookState.bookshelvesState.bookshelves
                .firstOrNull { it.bookshelfBookDetails.id == currentBookshelf.bookshelfBookDetails.id }

            // If a corresponding bookshelf exists, compare isBookPresent to determine changes
            if (initialBookshelf != null) {
                if (currentBookshelf.isBookPresent && !initialBookshelf.isBookPresent) {
                    Log.d(TAG, "Book is not present. Attempting to add book")

                    // Book was not present initially but is now present, add it to the bookshelf
                    bookshelfRepository.addBookToBookshelf(bookUiState.value.toAddBookToBookshelf(bookshelfId = currentBookshelf.bookshelfBookDetails.id, bookUiState.value))
                } else if (!currentBookshelf.isBookPresent && initialBookshelf.isBookPresent) {
                    Log.d(TAG, "Book is present. Attempting to remove book")

                    // Book was present initially but is now removed, remove it from the bookshelf
                    bookshelfRepository.removeBookFromBookshelf(bookshelfId = currentBookshelf.bookshelfBookDetails.id, bookId = bookUiState.value.bookDetails.id)
                }
            }
        }
    }



    /**
     *   SearchBook funs
     *
     */
    init {
        suggestRandomBook()
    }

    override fun clearSearch() {

        updateSearchState(SearchState.Empty)
        _searchQuery.update {
            ""
        }
    }

    override fun updateSearchState(newState: SearchState){
        _searchUiState.update { newState }
    }
    // New function to get a random book suggestion
    override fun suggestRandomBook() {
        _suggestedBook.update {
            suggestedBookTitles[Random.nextInt(suggestedBookTitles.size)]
        }
    }

    override fun onLoading(){
        _searchMessage.update {
            searchMessages[Random.nextInt(searchMessages.size)]
        }
    }


    override fun onSearchQueryChange(query: String) {
        _searchQuery.update {
            query
        }
    }

    override fun assignSuggestedBook(){
        onSearchQueryChange(suggestedBook.value)
    }

    override fun shuffleBook(){
        suggestRandomBook()
        assignSuggestedBook()
    }

    override suspend fun onSearch() {
        if (searchQuery.value.isEmpty() && suggestedBook.value.isNotEmpty()) {
            assignSuggestedBook()
        }
        onLoading()
        searchBooks(searchQuery.value)
    }

    override suspend fun searchBooks(query: String) {
        updateSearchState(SearchState.Loading)
        try {
            val response = googleBooksApi.searchBooks(query, apiKey)
            if (response.isSuccessful) {
                updateSearchState(
                    SearchState.Success(
                        response.body()?.items ?: emptyList()
                    )
                )

            } else {
                updateSearchState(
                    SearchState.Error("Failed with status: ${response.code()}")
                )

            }
        } catch (e: Exception) {
            updateSearchState(
                SearchState.Error("An error occurred. Check your internet and try again")

            )

        }

    }

    companion object {
        const val TAG = "BooksRepositoryImpl"
    }

}