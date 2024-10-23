package com.don.bookish.presentation.book_details

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.don.bookish.data.model.BookItem
import com.don.bookish.data.model.VolumeData
import com.don.bookish.data.repositories.BooksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class BookDetailsViewModel @Inject constructor(
    private val repository: BooksRepository
): ViewModel() {
    var bookState: BookState by mutableStateOf(BookState.Loading)
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

    fun getBookDetails(bookId: String) {
        bookState = BookState.Loading

        viewModelScope.launch {
            try {
                Log.d("BookDetailsViewModel", "Fetching book details for: $bookId")
                val response = repository.getBookDetails(bookId)
                // Check if response is successful
                if (response.isSuccessful) {
                    bookState = BookState.Success(response.body()!!)
                } else {
                    // Handle non-successful response (e.g., 404, 500, etc.)
                    bookState = BookState.Error("Error: ${response.code()}")
                }
                Log.d("BookDetailsViewModel", "BookState: $bookState")

            } catch (e: Exception) {
                // Handle network errors or unexpected exceptions
                bookState = BookState.Error("Network error. Check your internet and try again")
            }
        }
    }

}

sealed interface BookState{
    data class Success(val data: VolumeData): BookState
    data class Error(val message : String = "An error occurred"): BookState
    object Loading: BookState
}
