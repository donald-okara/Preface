package com.don.bookish.presentation.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.don.bookish.data.model.BookItem
import com.don.bookish.data.repositories.BooksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: BooksRepository
) : ViewModel() {

    var searchUiState: SearchState by mutableStateOf(SearchState.Empty)
        private set

    var searchQuery by mutableStateOf("")
        private set

    var suggestedBook by mutableStateOf("")
        private set

    var searchMessage by mutableStateOf("")
        private set

    init {
        suggestRandomBook()
    }

    fun clearSearch() {
        searchQuery = ""
        searchUiState = SearchState.Empty
    }

    fun onSearchQueryChange(query: String) {
        searchQuery = query
    }

    // New function to get a random book suggestion
    fun suggestRandomBook() {
        suggestedBook = suggestedBookTitles[Random.nextInt(suggestedBookTitles.size)]
        searchQuery = suggestedBook
    }

    fun onLoading(){
        searchMessage = searchMessages[Random.nextInt(searchMessages.size)]
    }

    fun onSearch() {
        if (searchQuery.isEmpty() && suggestedBook.isNotEmpty()) {
            searchQuery = suggestedBook
        }
        onLoading()
        searchBooks(searchQuery)
    }

    fun searchBooks(query: String) {
        searchUiState = SearchState.Loading
        viewModelScope.launch {
            try {
                val response = repository.searchBooks(query)
                if (response.isSuccessful) {
                    searchUiState = SearchState.Success(response.body()?.items ?: emptyList())
                } else {
                    searchUiState = SearchState.Error("Failed with status: ${response.code()}")
                }
            } catch (e: Exception) {
                searchUiState = SearchState.Error("An error occurred.")
            }
        }
    }
}

sealed interface SearchState{
    data class Success(val data: List<BookItem>): SearchState
    data class Error(val message : String = "An error occurred"): SearchState
    object Loading: SearchState
    object Empty: SearchState
}
