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

    var bookState: BookState by mutableStateOf(BookState.Empty)
        private set


    var loadingJoke: String by mutableStateOf("")
        private set

    init {
        onLoading()
    }

    fun clearState() {
        bookState = BookState.Loading
    }

    fun onLoading() {
        loadingJoke = loadingBookJokes[Random.nextInt(loadingBookJokes.size)]
        Log.d("BookDetailsViewModel", "Loading joke: $loadingJoke")
    }


    fun getBookDetails(bookId: String) {
        bookState = BookState.Loading
        onLoading()
        Log.d("BookDetailsViewModel", "About to fetch details for bookId: $bookId")

        viewModelScope.launch {
            try {
                val response: Response<VolumeData> = repository.getBookDetails(bookId)

                if (response.isSuccessful) {
                    response.body()?.let { volumeData ->
                        bookState = BookState.Success(volumeData)
                    } ?: run {
                        bookState = BookState.Error("No data available")
                        Log.e("BookDetailsViewModel", "Response body is null")
                    }
                } else {
                    bookState = BookState.Error("Failed to load book details: ${response.message()}")
                    Log.e("BookDetailsViewModel", "Error response: ${response.message()}")
                }

            } catch (e: ConnectException) {
                bookState = BookState.Error("Network error. Check your internet and try again")
                Log.e("BookDetailsViewModel", "ConnectException: ${e.message}")
            } catch (e: IllegalArgumentException) {
                bookState = BookState.Error("Failed to load book details")
            } catch (e: SocketTimeoutException) {
                bookState = BookState.Error("Failed to load book details")
            } catch (e: Exception) {
                bookState = BookState.Error("Something went wrong. Try again.")
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
