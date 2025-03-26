package ke.don.shared_navigation.bottom_navigation.tabs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.automirrored.filled.ManageSearch
import androidx.compose.material.icons.outlined.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import ke.don.feature_book_details.presentation.screens.search.BookSearchScreen
import ke.don.feature_bookshelf.presentation.screens.user_library.UserLibraryScreen
import ke.don.feature_profile.tab.ProfileScreen

class MyLibraryTab(
    private val onNavigateToAddBookshelf: () -> Unit = {},
    private val onNavigateToEditBookshelf: (Int) -> Unit = {},
    private val onNavigateToBookshelfItem: (Int) -> Unit = {},
) : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(image = Icons.AutoMirrored.Filled.LibraryBooks)

            val tabName = "My library"

            return remember { TabOptions(index = 0u, title = tabName, icon = icon) }
        }

    @Composable
    override fun Content() {
        UserLibraryScreen(
            onNavigateToBookshefItem = { bookshelfId ->
                onNavigateToBookshelfItem(bookshelfId)
            },
            onAddBookshelf = {
                onNavigateToAddBookshelf
            },
            onNavigateToEdit = {bookshelfId->
                onNavigateToEditBookshelf(bookshelfId)
            }
        )
    }
}

class SearchTab(private val onNavigateToBookItem: (String) -> Unit) : Tab {

    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(image = Icons.AutoMirrored.Filled.ManageSearch)

            val tabName = "Search"

            return remember { TabOptions(index = 0u, title = tabName, icon = icon) }
        }

    @Composable
    override fun Content() {
        BookSearchScreen(
            onNavigateToBookItem = onNavigateToBookItem
        )
    }
}

class ProfileTab(private val onSignOut: () -> Unit) : Tab {

    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(image = Icons.Outlined.Person)

            val tabName = "Profile"

            return remember { TabOptions(index = 0u, title = tabName, icon = icon) }
        }

    @Composable
    override fun Content() {
        ProfileScreen(
            onNavigateToSignIn = { onSignOut() },
        )
    }
}
