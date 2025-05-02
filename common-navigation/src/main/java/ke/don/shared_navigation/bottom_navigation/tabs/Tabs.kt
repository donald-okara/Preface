package ke.don.shared_navigation.bottom_navigation.tabs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.automirrored.filled.ManageSearch
import androidx.compose.material.icons.automirrored.outlined.LibraryBooks
import androidx.compose.material.icons.outlined.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import ke.don.shared_navigation.R
import ke.don.shared_navigation.bottom_navigation.tabs.library.MyLibraryScreen
import ke.don.shared_navigation.bottom_navigation.tabs.profile.ProfileVoyagerScreen
import ke.don.shared_navigation.bottom_navigation.tabs.search.SearchVoyagerScreen

class MyLibraryTab() : Tab {
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
            val tabName = stringResource(R.string.my_library)

            return remember { TabOptions(index = 0u, title = tabName, icon = icon) }
        }

    @Composable
    override fun Content() {
        Navigator(MyLibraryScreen()) { innerNavigator ->
            innerNavigator.lastItemOrNull?.Content()
        }
    }
}

class SearchTab(
    private val searchQuery: String? = null
) : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(image = Icons.AutoMirrored.Filled.ManageSearch)

            val tabName = stringResource(R.string.search)

            return remember { TabOptions(index = 0u, title = tabName, icon = icon) }
        }

    @Composable
    override fun Content() {
        Navigator(SearchVoyagerScreen(searchQuery)) { innerNavigator ->
            innerNavigator.lastItemOrNull?.Content()
        }
    }
}

class ProfileTab() : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(image = Icons.Outlined.Person)

            val tabName = stringResource(R.string.profile)

            return remember { TabOptions(index = 0u, title = tabName, icon = icon) }
        }

    @Composable
    override fun Content() {
        Navigator(ProfileVoyagerScreen()) { innerNavigator ->
            innerNavigator.lastItemOrNull?.Content()
        }
    }
}
