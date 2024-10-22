package com.don.bookish.presentation.book_details

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.don.bookish.data.model.BookItem
import com.don.bookish.data.model.VolumeData
import com.don.bookish.data.repositories.BooksRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class BookDetailsViewModel @Inject constructor(
    private val repository: BooksRepository
): ViewModel() {
    var bookState: BookState by mutableStateOf(BookState.Empty)
        private set

    fun getBookDetails(bookId: String) {
        bookState = BookState.Loading

        viewModelScope.launch {
            try {
                val response = repository.getBookDetails(bookId)
                // Check if response is successful
                if (response.isSuccessful) {
                    bookState = BookState.Success(response.body()!!)
                } else {
                    // Handle non-successful response (e.g., 404, 500, etc.)
                    bookState = BookState.Error("Error: ${response.code()}")
                }
            } catch (e: Exception) {
                // Handle network errors or unexpected exceptions
                bookState = BookState.Error("Network error: ${e.message}")
            }
        }
    }

}

sealed interface BookState{
    data class Success(val data: VolumeData): BookState
    data class Error(val message : String = "An error occurred"): BookState
    object Loading: BookState
    object Empty: BookState
}
