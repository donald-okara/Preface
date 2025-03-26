package ke.don.shared_navigation.bottom_navigation.tabs.library

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import ke.don.feature_bookshelf.presentation.screens.add_bookshelf.AddBookshelfRoute
import ke.don.feature_bookshelf.presentation.screens.bookshelf_details.BookshelfDetailsRoute
import ke.don.shared_navigation.bottom_navigation.tabs.search.BookDetailsVoyagerScreen


class AddBookshelfVoyagerScreen(private val bookshelfId: Int?) : AndroidScreen() {
    private fun readResolve(): Any = AddBookshelfVoyagerScreen(bookshelfId = bookshelfId)

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "Add Bookshelf"
                        )
                    }

                )
            }
        ) { innerPadding ->
            AddBookshelfRoute(
                bookshelfId = bookshelfId,
                modifier = Modifier.padding(innerPadding),
                paddingValues = innerPadding,
                onNavigateBack = { navigator?.pop() }
            )
        }
    }

}

class BookshelfDetailsScreen(private val bookshelfId: Int) : AndroidScreen() {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        BookshelfDetailsRoute(
            bookshelfId = bookshelfId,
            navigateBack = { navigator?.pop() },
            onItemClick = { bookId ->
                navigator?.push(BookDetailsVoyagerScreen(bookId))
            },
            onNavigateToEdit = { bookshelfId ->
                navigator?.push(AddBookshelfVoyagerScreen(bookshelfId))
            }
        )
    }
}
