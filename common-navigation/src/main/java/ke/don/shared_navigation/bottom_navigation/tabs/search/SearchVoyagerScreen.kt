package ke.don.shared_navigation.bottom_navigation.tabs.search

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import ke.don.feature_book_details.presentation.screens.book_details.BookDetailsScreen


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
