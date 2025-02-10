package ke.don.shared_navigation.tabs.search

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import ke.don.feature_book_details.presentation.screens.book_details.BookDetailsScreen
import ke.don.feature_book_details.presentation.screens.search.BookSearchScreen
import ke.don.shared_domain.values.Screens

object SearchVoyagerScreen : AndroidScreen() {
    private fun readResolve(): Any = SearchVoyagerScreen

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        BookSearchScreen(
            onNavigateToBookItem = { bookId ->
                Screens.BookDetails.route.replace("{bookId}", bookId)
                navigator?.push(BookDetailsVoyagerScreen(bookId))
            }
        )
    }
}

// Voyager Screens
class BookDetailsVoyagerScreen(private val volumeId: String) : AndroidScreen() {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        BookDetailsScreen(
            onNavigateToSearch = { navigator?.pop() },
            volumeId = volumeId,
            onBackPressed = { navigator?.pop() }
        )
    }
}
