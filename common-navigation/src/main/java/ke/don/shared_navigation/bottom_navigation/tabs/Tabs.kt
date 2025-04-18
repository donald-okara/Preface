package ke.don.shared_navigation.bottom_navigation.tabs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.automirrored.filled.ManageSearch
import androidx.compose.material.icons.automirrored.outlined.LibraryBooks
import androidx.compose.material.icons.outlined.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.hilt.navigation.compose.hiltViewModel
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import ke.don.feature_book_details.presentation.screens.search.BookSearchScreen
import ke.don.feature_bookshelf.presentation.screens.user_library.UserLibraryScreen
import ke.don.feature_profile.tab.ProfileScreen
import ke.don.feature_profile.tab.ProfileViewModel

class MyLibraryTab(
    private val onNavigateToAddBookshelf: () -> Unit = {},
    private val onNavigateToEditBookshelf: (Int) -> Unit = {},
    private val onNavigateToBookshelfItem: (Int) -> Unit = {},
) : Tab {
    override val options: TabOptions
        @Composable
        get() {
            // Get the current tab state
            val tabNavigator = LocalTabNavigator.current
            val isSelected = tabNavigator.current == this

            val icon = rememberVectorPainter(
                image = if (isSelected) {
                    Icons.AutoMirrored.Filled.LibraryBooks
                } else {
                    Icons.AutoMirrored.Outlined.LibraryBooks
                }
            )
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
                onNavigateToAddBookshelf()
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

class ProfileTab(private val onSignOut: () -> Unit, private val onNavigateToBookItem: (String) -> Unit) : Tab {

    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(image = Icons.Outlined.Person)

            val tabName = "Profile"

            return remember { TabOptions(index = 0u, title = tabName, icon = icon) }
        }

    @Composable
    override fun Content() {
        val viewModel: ProfileViewModel = hiltViewModel()
        val state by viewModel.profileState.collectAsState()

        ProfileScreen(
            onNavigateToSignIn = { onSignOut() },
            viewModel = viewModel,
            profileState = state,
            profileTabEventHandler = viewModel::handleEvent,
            onNavigateToBook = onNavigateToBookItem
        )
    }
}
