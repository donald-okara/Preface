package ke.don.shared_domain.states

import ke.don.shared_domain.data_models.BookItem

data class SearchState(
    val resultState: ResultState = ResultState.Empty,
    val errorMessage: String = "",
    val searchQuery: String = "",
    val suggestedBook: String = "",
    val searchMessage: String = "",
    val data: List<BookItem> = emptyList(),
)