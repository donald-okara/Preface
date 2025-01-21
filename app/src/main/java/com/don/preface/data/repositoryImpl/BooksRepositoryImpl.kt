package com.don.preface.data.repositoryImpl

import androidx.lifecycle.viewModelScope
import com.don.preface.data.model.BookListItemResponse
import com.don.preface.data.model.BookDetailsResponse
import com.don.preface.domain.logger.Logger
import com.don.preface.domain.repositories.BooksRepository
import com.don.preface.domain.states.BookUiState
import com.don.preface.domain.states.ResultState
import com.don.preface.domain.utils.color_utils.extractColorPalette
import com.don.preface.domain.utils.color_utils.model.ColorPallet
import com.don.preface.network.GoogleBooksApi
import com.don.preface.presentation.screens.book_details.BookDetailsViewModel.Companion.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.net.ConnectException

class BooksRepositoryImpl(
    private val googleBooksApi: GoogleBooksApi,
    private val apiKey: String,
    private val logger: Logger
) : BooksRepository {

    private val _bookState = MutableStateFlow(BookUiState())
    override val bookUiState: StateFlow<BookUiState> = _bookState

    override suspend fun searchBooks(
        query: String
    ): Response<BookListItemResponse> {
        return googleBooksApi.searchBooks(query, apiKey)
    }


    private fun updateBookState(newState: BookUiState) {
        _bookState.update { newState }
    }

    fun clearState() {
        updateBookState(BookUiState())
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
                        it.extraLarge ?: it.large ?: it.medium ?: it.small ?: it.thumbnail ?: it.smallThumbnail
                    }?.replace("http", "https")


                    val colorPallet = if (highestImageUrl.isNullOrEmpty()) {
                        ColorPallet()
                    } else {
                        extractColorPalette(highestImageUrl)
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

                    logger.logDebug(TAG, "book highestUrl : ${bookUiState.value.highestImageUrl}")
                    logger.logDebug(TAG, "book state : ${bookUiState.value}")
                } ?: run {
                    // Handle case where no data is available
                    updateBookState(
                        BookUiState(
                            resultState = ResultState.Error("No data available")
                        )
                    )
                    logger.logError(TAG, "No data available")
                }
            } else {
                // Handle unsuccessful response
                updateBookState(
                    BookUiState(
                        resultState = ResultState.Error("Failed to load book details")
                    )
                )
                logger.logError(TAG, "Error response: ${response.message()}")
            }

        } catch (e: ConnectException) {
            // Handle network error
            updateBookState(
                BookUiState(
                    resultState = ResultState.Error("Network error. Check your internet and try again")
                )
            )
            logger.logError(TAG, "ConnectException: ${e.message}")
        }
    }

    companion object {
        const val TAG = "BooksRepositoryImpl"
    }

}