package ke.don.common_datasource.remote.domain.repositories

import ke.don.common_datasource.remote.domain.states.BookUiState
import ke.don.shared_domain.states.SearchState
import kotlinx.coroutines.flow.StateFlow

interface BooksRepository {
    val bookUiState : StateFlow<BookUiState>

    val searchUiState: StateFlow<SearchState>

    var searchQuery: StateFlow<String>

    var suggestedBook: StateFlow<String>

    //val userLibraryState : StateFlow<UserLibraryState>

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

    fun onBookshelfSelected(bookshelfId: Int)

    suspend fun onSearch()

    suspend fun pushEditedBookshelfBooks() : Boolean
}

