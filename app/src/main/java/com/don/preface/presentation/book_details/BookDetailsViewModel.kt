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
import java.net.ConnectException
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class BookDetailsViewModel @Inject constructor(
    private val repository: BooksRepository
): ViewModel() {
    var bookState: BookState by mutableStateOf(BookState.Empty)
        private set

    var loadingJoke: String by mutableStateOf("")
        private set

    init {
        onLoading()
    }

    fun clearState(){
        bookState = BookState.Loading
    }

    private fun onLoading(){
        loadingJoke = loadingBookJokes[Random.nextInt(loadingBookJokes.size)]
        Log.d("BookDetailsViewModel", "Loading joke: $loadingJoke")
    }
    val supervisorJob = SupervisorJob()


    fun getBookDetails(bookId: String) {
        bookState = BookState.Loading
        onLoading()
        Log.d("BookDetailsViewModel", "About to fetch details for bookId: $bookId")

        viewModelScope.launch(Dispatchers.Main + supervisorJob) {
            try {
                withContext(Dispatchers.IO) {
                    Log.d("BookDetailsViewModel", "Fetching book details for: $bookId")
                    val response = repository.getBookDetails(bookId)

                    // Switch back to the Main thread to update UI
                    withContext(Dispatchers.Main) {
                        bookState = BookState.Success(response)
                    }
                }
            } catch (e: ConnectException) {
                bookState = BookState.Error("Network error. Check your internet and try again")
                Log.e("BookDetailsViewModel", "ConnectException: ${e.message}")
            } catch (e: IllegalArgumentException) {
                bookState = BookState.FallbackError
            } catch (e: Exception) {
                bookState = BookState.Error("Something went wrong. Try again.")
                Log.e("BookDetailsViewModel", "Exception: ${e.message}")
            }
        }
    }



}

sealed interface BookState{
    data class Success(val data: VolumeData): BookState
    data class Error(val message : String = "An error occurred"): BookState
    data object Loading: BookState
    data object Empty: BookState
    object FallbackError: BookState // New state to handle the specific error
}
