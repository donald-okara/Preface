package ke.don.feature_book_details.presentation.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.don.common_datasource.remote.domain.repositories.BooksRepository
import ke.don.shared_domain.states.SearchResult
import ke.don.common_datasource.remote.domain.usecases.BooksUseCases
import ke.don.shared_domain.states.NetworkResult
import ke.don.shared_domain.states.ResultState
import ke.don.shared_domain.states.SearchState
import ke.don.shared_domain.states.searchMessages
import ke.don.shared_domain.states.suggestedBookTitles
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: BooksRepository,
    private val booksUseCases : BooksUseCases
) : ViewModel() {

    private var _searchUiState = MutableStateFlow(SearchState())
    val searchUiState: StateFlow<SearchState> = _searchUiState

    private var _searchQuery = MutableStateFlow("")
    var searchQuery : StateFlow<String> = _searchQuery

    private var _searchMessage = MutableStateFlow("")
    var searchMessage : StateFlow<String> = _searchMessage

    private var _suggestedBook = MutableStateFlow("")
    var suggestedBook = _suggestedBook
        .onStart { suggestRandomBook() }
        .stateIn(
            scope = viewModelScope, // Coroutine scope for collecting updates
            started = SharingStarted.WhileSubscribed(5000L), // Keep emitting updates while there are subscribers
            initialValue = "" // Initial value of the mapped StateFlow
        )



    val isSearchPopulated: StateFlow<Boolean> = searchQuery
        .map { it.isNotEmpty() }
        .stateIn(
            scope = viewModelScope, // Coroutine scope for collecting updates
            started = SharingStarted.WhileSubscribed(5000L), // Keep emitting updates while there are subscribers
            initialValue = false // Initial value of the mapped StateFlow
        )

    fun updateSearchState(newState: SearchState){
        _searchUiState.update { newState }
    }

    fun clearSearch() {
        updateSearchState(SearchState())
        _searchQuery.update {
            ""
        }
    }

    fun onLoading(){
        _searchMessage.update {
            searchMessages[Random.nextInt(searchMessages.size)]
        }
        updateSearchState(SearchState(resultState = ResultState.Loading))
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.update {
            query
        }
    }

    fun assignSuggestedBook(){
        onSearchQueryChange(suggestedBook.value)
    }

    fun shuffleBook() {
        suggestRandomBook()
        assignSuggestedBook()
    }

    fun onSearch() = viewModelScope.launch {
        if (searchQuery.value.isEmpty() && suggestedBook.value.isNotEmpty()) {
            assignSuggestedBook()
        }
        onLoading()
        when ( val result =repository.searchBooks(searchQuery.value) ){
            is NetworkResult.Error -> {
                val errorMessage = buildString {
                    result.code?.let { append("code: $it, ") } // Add "code: " prefix if code is not null
                    append(listOfNotNull(result.message, result.hint).joinToString(", ")) // Join message & hint
                }.trimEnd().removeSuffix(",") // Remove trailing comma if present

                updateSearchState(
                    SearchState(
                        resultState = ResultState.Error(),
                        errorMessage = errorMessage.ifEmpty { "Unknown error" })
                )
            }

            is NetworkResult.Success -> {
                updateSearchState(
                    SearchState(
                        resultState = ResultState.Success,
                        data = result.result
                    )
                )
            }
        }
    }

    private fun suggestRandomBook() {
        _suggestedBook.update {
            suggestedBookTitles[Random.nextInt(suggestedBookTitles.size)]
        }
    }
    companion object {
        const val TAG = "BooksViewModel"
    }
}

