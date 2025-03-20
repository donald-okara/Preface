package ke.don.shared_domain.states

import ke.don.shared_domain.data_models.BookItem

sealed interface SearchResult{
    data class Success(val data: List<BookItem>): SearchResult
    data class Error(val message : String = "An error occurred"): SearchResult
    data object Loading: SearchResult
    data object Empty: SearchResult
}

data class SearchState(
    val resultState: ResultState = ResultState.Empty,
    val errorMessage: String = "",
    val data: List<BookItem> = emptyList(),
)