package ke.don.feature_book_details.data.repositoryImpl

import ke.don.feature_book_details.data.model.BookDetailsResponse
import ke.don.feature_book_details.data.model.BookListItemResponse
import ke.don.feature_book_details.domain.logger.Logger
import ke.don.feature_book_details.domain.repositories.BooksRepository
import ke.don.feature_book_details.domain.states.BookUiState
import ke.don.feature_book_details.domain.states.ResultState
import ke.don.feature_book_details.domain.utils.color_utils.ColorPaletteExtractor
import ke.don.feature_book_details.domain.utils.color_utils.model.ColorPallet
import ke.don.feature_book_details.network.GoogleBooksApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import retrofit2.Response

class BooksRepositoryImpl(
    private val googleBooksApi: GoogleBooksApi,
    private val apiKey: String,
    private val colorPaletteExtractor: ColorPaletteExtractor,
    private val logger: Logger
) : BooksRepository {

    private val _bookState = MutableStateFlow(BookUiState())
    override val bookUiState: StateFlow<BookUiState> = _bookState


    override suspend fun searchBooks(
        query: String
    ): Response<BookListItemResponse> {
        return googleBooksApi.searchBooks(query, apiKey)
    }


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

    companion object {
        const val TAG = "BooksRepositoryImpl"
    }

}