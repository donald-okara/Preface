package ke.don.feature_book_details.data.repositoryImpl

import ke.don.feature_book_details.data.model.BookDetailsResponse
import ke.don.feature_book_details.domain.repositories.BooksRepository
import ke.don.feature_book_details.domain.states.BookUiState
import ke.don.feature_book_details.domain.states.ResultState
import ke.don.feature_book_details.network.GoogleBooksApi
import ke.don.feature_book_details.presentation.screens.search.SearchState
import ke.don.feature_book_details.presentation.screens.search.searchMessages
import ke.don.feature_book_details.presentation.screens.search.suggestedBookTitles
import ke.don.shared_domain.screens.logger.Logger
import ke.don.shared_domain.screens.utils.color_utils.ColorPaletteExtractor
import ke.don.shared_domain.screens.utils.color_utils.model.ColorPallet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import retrofit2.Response
import kotlin.random.Random

class BooksRepositoryImpl(
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