package com.don.preface.presentation.search

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.don.preface.data.model.BookItem
import com.don.preface.data.repositories.BooksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: BooksRepository
) : ViewModel() {

    private var _searchUiState = MutableStateFlow<SearchState>(SearchState.Empty)
    val searchUiState: StateFlow<SearchState> = _searchUiState

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
        _searchUiState.update { SearchState.Empty }
    }

    fun onSearchQueryChange(query: String) {
        searchQuery = query
    }

    fun shuffleBook(){
        suggestRandomBook()
        searchQuery = suggestedBook
    }

    // New function to get a random book suggestion
    fun suggestRandomBook() {
        suggestedBook = suggestedBookTitles[Random.nextInt(suggestedBookTitles.size)]
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
        _searchUiState.update { SearchState.Loading }
        viewModelScope.launch {
            try {
                val response = repository.searchBooks(query)
                if (response.isSuccessful) {
                    _searchUiState.update{ SearchState.Success(response.body()?.items ?: emptyList()) }
                } else {
                    _searchUiState.update{ SearchState.Error("Failed with status: ${response.code()}") }
                }
            } catch (e: Exception) {
                _searchUiState.update{ SearchState.Error("An error occurred. Check your internet and try again") }
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
