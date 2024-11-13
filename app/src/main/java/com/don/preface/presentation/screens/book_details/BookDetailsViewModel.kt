package com.don.preface.presentation.screens.book_details

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.don.preface.data.model.BookDetailsResponse
import com.don.preface.data.repositories.BooksRepository
import com.don.preface.presentation.utils.logger.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.Response
import java.net.ConnectException
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class BookDetailsViewModel @Inject constructor(
    private val repository: BooksRepository,
    private val logger : Logger
) : ViewModel() {

    private val _bookState = MutableStateFlow<BookState>(BookState.Empty)
    val bookState: StateFlow<BookState> = _bookState

    var loadingJoke: String by mutableStateOf("")
        private set

    init {
        onLoading()
    }

    fun updateBookState(newState: BookState) {
        _bookState.update { newState }
    }

    fun clearState() {
        updateBookState(BookState.Empty)
    }

    fun onLoading() {
        loadingJoke = loadingBookJokes[Random.nextInt(loadingBookJokes.size)]
        logger.logDebug(TAG, "Loading joke: $loadingJoke")
    }


    fun getBookDetails(bookId: String) {
        updateBookState(BookState.Loading)
        onLoading()

        viewModelScope.launch {
            try {
                val response: Response<BookDetailsResponse> = repository.getBookDetails(bookId)

                if (response.isSuccessful) {
                    response.body()?.let { volumeData ->
                        updateBookState(
                            BookState.Success(volumeData)
                        )
                    } ?: run {
                        updateBookState( BookState.Error("No data available"))
                        logger.logError(TAG,"No data available")

                    }
                } else {
                    updateBookState(BookState.Error("Failed to load book details"))
                    logger.logError(TAG, "Error response: ${response.message()}")
                }

            } catch (e: ConnectException) {
                updateBookState(BookState.Error("Network error. Check your internet and try again"))

                logger.logError(TAG, "ConnectException: ${e.message}")
            } catch (e: Exception) {
                updateBookState(BookState.Error("An error occurred") )
                logger.logError(TAG, "Exception: ${e.message}")
                
            }
        }
    }

    companion object {
        const val TAG = "BookDetailsViewModel"
    }
}

sealed interface BookState {
    data class Success(val data: BookDetailsResponse) : BookState
    data class Error(val message: String = "An error occurred") : BookState
    data object Loading : BookState
    data object Empty : BookState
    object FallbackError : BookState // New state to handle the specific error
}
