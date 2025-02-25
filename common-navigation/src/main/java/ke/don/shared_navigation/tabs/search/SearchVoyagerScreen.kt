package ke.don.shared_navigation.tabs.search

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import ke.don.feature_book_details.presentation.screens.book_details.BookDetailsScreen
import ke.don.feature_book_details.presentation.screens.search.BookSearchScreen
import ke.don.shared_domain.values.Screens
import ke.don.shared_navigation.MainBottomAppBar

object SearchVoyagerScreen : AndroidScreen() {
    private fun readResolve(): Any = SearchVoyagerScreen

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "Search",
                            modifier = Modifier.padding(8.dp),
                            maxLines = 1
                        )
                    }
                )
            },

            bottomBar = { MainBottomAppBar() },
        ){innerPadding->
            BookSearchScreen(
                paddingValues = innerPadding,
                onNavigateToBookItem = { bookId ->
                    navigator?.push(BookDetailsVoyagerScreen(bookId))
                }
            )
        }
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
