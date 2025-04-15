package ke.don.shared_navigation.bottom_navigation.tabs.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.hilt.navigation.compose.hiltViewModel
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import ke.don.common_datasource.remote.domain.states.BookUiState
import ke.don.feature_book_details.presentation.screens.book_details.BookDetailsEvent
import ke.don.feature_book_details.presentation.screens.book_details.BookDetailsScreen
import ke.don.feature_book_details.presentation.screens.book_details.BookDetailsViewModel


// Voyager Screens
class BookDetailsVoyagerScreen(private val volumeId: String) : AndroidScreen() {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val viewModel: BookDetailsViewModel = hiltViewModel()
        val bookUiState by viewModel.bookState.collectAsState()
        val onBookDetailsEvent = viewModel::onBookDetailsEvent

        BookDetailsScreen(
            onNavigateToSearch = { navigator?.pop() },
            volumeId = volumeId,
            onBackPressed = { navigator?.pop() },
            bookUiState = bookUiState,
            onBookDetailsEvent = onBookDetailsEvent
        )
    }
}
