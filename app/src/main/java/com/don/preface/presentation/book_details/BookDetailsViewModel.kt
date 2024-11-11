package com.don.preface.presentation.book_details

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.don.preface.data.model.VolumeData
import com.don.preface.data.repositories.BooksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.net.ConnectException
import java.net.SocketTimeoutException
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class BookDetailsViewModel @Inject constructor(
    private val repository: BooksRepository
) : ViewModel() {

    private val _bookState = MutableStateFlow<BookState>(BookState.Empty)
    val bookState: StateFlow<BookState> = _bookState

    var loadingJoke: String by mutableStateOf("")
        private set

    init {
        onLoading()
    }

    fun clearState() {
        _bookState.update { BookState.Empty }
    }

    fun onLoading() {
        loadingJoke = loadingBookJokes[Random.nextInt(loadingBookJokes.size)]
        Log.d("BookDetailsViewModel", "Loading joke: $loadingJoke")
    }


    fun getBookDetails(bookId: String) {
        _bookState.update { BookState.Loading }
        onLoading()
        Log.d("BookDetailsViewModel", "About to fetch details for bookId: $bookId")

        viewModelScope.launch {
            try {
                val response: Response<VolumeData> = repository.getBookDetails(bookId)

                if (response.isSuccessful) {
                    response.body()?.let { volumeData ->
                        _bookState.update { BookState.Success(volumeData) }// = BookState.Success(volumeData)

                    } ?: run {
                        _bookState.update { BookState.Error("No data available") }
                        Log.e("BookDetailsViewModel", "Response body is null")

                    }
                } else {
                    _bookState.update { BookState.Error("Failed to load book details: ${response.message()}") }
                    Log.e("BookDetailsViewModel", "Error response: ${response.message()}")
                }

            } catch (e: ConnectException) {
                _bookState.update{BookState.Error("Network error. Check your internet and try again")}
                Log.e("BookDetailsViewModel", "ConnectException: ${e.message}")

            } catch (e: IllegalArgumentException) {
                _bookState.update { BookState.Error("Invalid book ID") }

            } catch (e: SocketTimeoutException) {
                _bookState.update { BookState.Error("Request timed out. Please try again.") }

            } catch (e: Exception) {
                _bookState.update { BookState.Error("An error occurred: ${e.message}") }
                Log.e("BookDetailsViewModel", "Exception: ${e.message}")
                
            }
        }
    }
}

sealed interface BookState {
    data class Success(val data: VolumeData) : BookState
    data class Error(val message: String = "An error occurred") : BookState
    data object Loading : BookState
    data object Empty : BookState
    object FallbackError : BookState // New state to handle the specific error
}
