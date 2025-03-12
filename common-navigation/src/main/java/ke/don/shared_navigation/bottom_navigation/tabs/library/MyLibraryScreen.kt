package ke.don.shared_navigation.bottom_navigation.tabs.library

import android.util.Log
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
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import ke.don.feature_bookshelf.presentation.screens.add_bookshelf.AddBookshelfRoute
import ke.don.feature_bookshelf.presentation.screens.bookshelf_details.BookshelfDetailsRoute
import ke.don.feature_bookshelf.presentation.screens.user_library.UserLibraryScreen
import ke.don.shared_navigation.BottomNavigationBar
import ke.don.shared_navigation.bottom_navigation.tabs.MyLibraryTab
import ke.don.shared_navigation.bottom_navigation.tabs.search.BookDetailsVoyagerScreen

object MyLibraryScreen : AndroidScreen() {
    private fun readResolve(): Any = MyLibraryScreen


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val tabNavigator = LocalTabNavigator.current
        val navigator = LocalNavigator.current
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "My library",
                            modifier = Modifier.padding(8.dp),
                            maxLines = 1
                        )
                    }
                )
            },

            bottomBar = {
                BottomNavigationBar(
                    selectedTab = tabNavigator.current,
                    onTabSelected = { tabNavigator.current = it }
                )
            },
        ) { innerPadding ->
            UserLibraryScreen(
                paddingValues = innerPadding,
                onNavigateToBookshefItem = { bookshelfId ->
                    Log.d("UserLibraryScreenNav", bookshelfId.toString())
                    navigator?.push(BookshelfDetailsScreen(bookshelfId))

                },
                onAddBookshelf = {
                    navigator?.push(AddBookshelfVoyagerScreen(null))
                },
                onNavigateToEdit = {bookshelfId->
                    navigator?.push(AddBookshelfVoyagerScreen(bookshelfId))

                }
            )
        }
    }
}

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
