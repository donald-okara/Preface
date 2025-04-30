package ke.don.feature_book_details.presentation.screens.search

sealed class SearchEventHandler {
    data object ClearSearch: SearchEventHandler()

    data object OnLoading: SearchEventHandler()

    data object Shuffle: SearchEventHandler()

    data object SuggestBook: SearchEventHandler()

    data class OnSearchQueryChange(val query: String): SearchEventHandler()

    data object Search: SearchEventHandler()

}