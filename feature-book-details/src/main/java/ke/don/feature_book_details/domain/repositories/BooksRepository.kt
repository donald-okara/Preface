package ke.don.feature_book_details.domain.repositories

import ke.don.feature_book_details.domain.states.BookUiState
import ke.don.feature_book_details.presentation.screens.search.SearchState
import kotlinx.coroutines.flow.StateFlow

interface BooksRepository {
    val bookUiState : StateFlow<BookUiState>

    val searchUiState: StateFlow<SearchState>

    var searchQuery: StateFlow<String>

    var suggestedBook: StateFlow<String>

    var searchMessage: StateFlow<String>

    suspend fun searchBooks(query: String)

    suspend fun getBookDetails(bookId: String)

    fun updateBookState(newState: BookUiState)

    fun clearSearch()

    fun updateSearchState(newState: SearchState)

    fun suggestRandomBook()

    fun onLoading()

    fun onSearchQueryChange(query: String)

    fun assignSuggestedBook()

    fun shuffleBook()

    suspend fun onSearch()

}

