package ke.don.feature_book_details.presentation.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.don.feature_book_details.domain.repositories.BooksRepository
import ke.don.feature_book_details.domain.usecase.BooksUseCases
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: BooksRepository,
    private val booksUseCases : BooksUseCases
) : ViewModel() {

    val searchUiState: StateFlow<SearchState> = repository.searchUiState

    var searchQuery = repository.searchQuery

    var suggestedBook = repository.suggestedBook

    var searchMessage = repository.searchMessage

    val isSearchPopulated: StateFlow<Boolean> = searchQuery
        .map { it.isNotEmpty() }
        .stateIn(
            scope = viewModelScope, // Coroutine scope for collecting updates
            started = SharingStarted.WhileSubscribed(5000L), // Keep emitting updates while there are subscribers
            initialValue = false // Initial value of the mapped StateFlow
        )

    fun clearSearch() = booksUseCases.clearSearch()

    fun onSearchQueryChange(query: String) = booksUseCases.onSearchQueryChange(query)

    fun shuffleBook() = booksUseCases.shuffleBook()

    fun onSearch() = viewModelScope.launch {
        booksUseCases.onSearch()

    }

    companion object {
        const val TAG = "BooksViewModel"
    }
}

sealed interface SearchState{
    data class Success(val data: List<ke.don.feature_book_details.data.model.BookItem>): SearchState
    data class Error(val message : String = "An error occurred"): SearchState
    data object Loading: SearchState
    data object Empty: SearchState
}
