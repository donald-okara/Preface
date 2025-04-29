package ke.don.shared_navigation.bottom_navigation.tabs.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import ke.don.feature_book_details.presentation.screens.book_details.BookDetailsRoute
import ke.don.feature_book_details.presentation.screens.book_details.BookDetailsViewModel
import ke.don.feature_book_details.presentation.screens.search.BookSearchScreen
import ke.don.feature_book_details.presentation.screens.search.SearchEventHandler
import ke.don.feature_book_details.presentation.screens.search.SearchViewModel
import ke.don.shared_navigation.bottom_navigation.tabs.SearchTab

class SearchVoyagerScreen(
    private val searchQuery: String?
): AndroidScreen() {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val viewModel: SearchViewModel = hiltViewModel()
        val state by viewModel.searchUiState.collectAsState()
        val eventHandler = viewModel::handleEvent

        LaunchedEffect(viewModel) {
            eventHandler(SearchEventHandler.SuggestBook)
            searchQuery?.let {
                eventHandler(SearchEventHandler.OnSearchQueryChange(it))
                eventHandler(SearchEventHandler.Search)
            }

        }

        BookSearchScreen(
            onNavigateToBookItem = { navigator?.push(BookDetailsVoyagerScreen(it)) },
            searchState = state,
            eventHandler = eventHandler,
        )
    }

}

class BookDetailsVoyagerScreen(private val volumeId: String) : AndroidScreen() {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val viewModel: BookDetailsViewModel = hiltViewModel()
        val bookUiState by viewModel.bookState.collectAsState()
        val onBookDetailsEvent = viewModel::onBookDetailsEvent

        BookDetailsRoute(
            onNavigateToSearch = { author ->
                navigator?.push(
                    SearchTab(
                        searchQuery = author
                    )
                )
            },
            volumeId = volumeId,
            onBackPressed = { navigator?.pop() },
            bookUiState = bookUiState,
            onBookDetailsEvent = onBookDetailsEvent
        )
    }
}
