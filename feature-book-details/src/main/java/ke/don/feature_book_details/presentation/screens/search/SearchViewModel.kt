package ke.don.feature_book_details.presentation.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.don.common_datasource.remote.domain.repositories.BooksRepository
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
) : ViewModel() {

    private var _searchUiState = MutableStateFlow(SearchState())
    val searchUiState: StateFlow<SearchState> = _searchUiState

    fun handleEvent(event: SearchEventHandler){
        when(event){
            is SearchEventHandler.ClearSearch -> clearSearch()
            is SearchEventHandler.SuggestBook -> suggestRandomBook()
            is SearchEventHandler.OnLoading -> onLoading()
            is SearchEventHandler.OnSearchQueryChange -> {
                onSearchQueryChange(event.query)
            }
            is SearchEventHandler.Search -> onSearch()
            is SearchEventHandler.Shuffle -> shuffleBook()
        }
    }


    fun updateSearchState(newState: SearchState){
        _searchUiState.update { newState }
    }

    fun clearSearch() {
        updateSearchState(
            SearchState()
        )
        suggestRandomBook()
    }

    fun onLoading(){
        updateSearchState(
            _searchUiState.value.copy(
                searchMessage = searchMessages[Random.nextInt(searchMessages.size)],
                resultState = ResultState.Loading
            )
        )
    }

    fun onSearchQueryChange(query: String) {
        updateSearchState(
            searchUiState.value.copy(
                searchQuery = query
            )
        )
    }

    fun assignSuggestedBook(){
        onSearchQueryChange(_searchUiState.value.suggestedBook)
    }

    fun shuffleBook() {
        suggestRandomBook()
        assignSuggestedBook()
    }

    fun onSearch() = viewModelScope.launch {
        if (_searchUiState.value.searchQuery.isEmpty() && _searchUiState.value.suggestedBook.isNotEmpty()) {
            assignSuggestedBook()
        }
        onLoading()
        when ( val result = repository.searchBooks(_searchUiState.value.searchQuery) ){
            is NetworkResult.Error -> {
                val errorMessage = buildString {
                    result.code?.let { append("code: $it, ") } // Add "code: " prefix if code is not null
                    append(listOfNotNull(result.message, result.hint).joinToString(", ")) // Join message & hint
                }.trimEnd().removeSuffix(",") // Remove trailing comma if present

                updateSearchState(
                    _searchUiState.value.copy(
                        resultState = ResultState.Error(),
                        errorMessage = errorMessage.ifEmpty { "Unknown error" })
                )
            }

            is NetworkResult.Success -> {
                updateSearchState(
                    _searchUiState.value.copy(
                        resultState = ResultState.Success,
                        data = result.data
                    )
                )
            }
        }
    }

    private fun suggestRandomBook() {
        updateSearchState(
            _searchUiState.value.copy(
                suggestedBook = suggestedBookTitles[Random.nextInt(suggestedBookTitles.size)]
            )
        )
    }
    companion object {
        const val TAG = "BooksViewModel"
    }
}

