package ke.don.feature_book_details.presentation.screens.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ke.don.feature_book_details.data.model.BookItem
import ke.don.feature_book_details.domain.repositories.BooksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: ke.don.feature_book_details.domain.repositories.BooksRepository
) : ViewModel() {

    private var _searchUiState = MutableStateFlow<SearchState>(SearchState.Empty)
    val searchUiState: StateFlow<SearchState> = _searchUiState

    var searchQuery by mutableStateOf("")

    var suggestedBook by mutableStateOf("")

    var searchMessage by mutableStateOf("")
        private set

    init {
        suggestRandomBook()
    }
    fun clearSearch() {
        searchQuery = ""
        updateSearchState(SearchState.Empty)
    }

    fun updateSearchState(newState: SearchState){
        _searchUiState.update { newState }
    }
    // New function to get a random book suggestion
    fun suggestRandomBook() {
        suggestedBook = suggestedBookTitles[Random.nextInt(suggestedBookTitles.size)]
    }

    fun onLoading(){
        searchMessage = searchMessages[Random.nextInt(searchMessages.size)]
    }


    fun onSearchQueryChange(query: String) {
        searchQuery = query
    }

    fun assignSuggestedBook(){
        onSearchQueryChange(suggestedBook)
    }

    fun shuffleBook(){
        suggestRandomBook()
        assignSuggestedBook()
    }



    fun onSearch() {
        if (searchQuery.isEmpty() && suggestedBook.isNotEmpty()) {
            assignSuggestedBook()
        }
        onLoading()
        searchBooks(searchQuery)
    }

    fun searchBooks(query: String) {
        updateSearchState(SearchState.Loading)
        viewModelScope.launch {
            try {
                val response = repository.searchBooks(query)
                if (response.isSuccessful) {
                    updateSearchState(
                        SearchState.Success(
                            response.body()?.items ?: emptyList()
                        )
                    )

                } else {
                    updateSearchState(
                        SearchState.Error("Failed with status: ${response.code()}")
                    )

                }
            } catch (e: Exception) {
                updateSearchState(
                    SearchState.Error("An error occurred. Check your internet and try again")

                )

            }
        }
    }
}

sealed interface SearchState{
    data class Success(val data: List<ke.don.feature_book_details.data.model.BookItem>): SearchState
    data class Error(val message : String = "An error occurred"): SearchState
    object Loading: SearchState
    object Empty: SearchState
}
