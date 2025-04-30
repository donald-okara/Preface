package ke.don.shared_navigation.bottom_navigation.tabs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.automirrored.filled.ManageSearch
import androidx.compose.material.icons.automirrored.outlined.LibraryBooks
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.hilt.navigation.compose.hiltViewModel
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import ke.don.feature_bookshelf.presentation.screens.user_library.LibraryEventHandler
import ke.don.feature_bookshelf.presentation.screens.user_library.UserLibraryScreen
import ke.don.feature_bookshelf.presentation.screens.user_library.UserLibraryViewModel
import ke.don.feature_profile.tab.ProfileScreen
import ke.don.feature_profile.tab.ProfileTabEventHandler
import ke.don.feature_profile.tab.ProfileViewModel
import ke.don.shared_navigation.app_scaffold.ConfigureAppBars
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
            val tabName = "My library"

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

            val tabName = "Search"

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

            val tabName = "Profile"

            return remember { TabOptions(index = 0u, title = tabName, icon = icon) }
        }

    @Composable
    override fun Content() {
        val viewModel: ProfileViewModel = hiltViewModel()
        val state by viewModel.profileState.collectAsState()
        val profileEventHandler = viewModel::handleEvent

        LaunchedEffect(viewModel) {
            profileEventHandler(ProfileTabEventHandler.FetchProfile)
            profileEventHandler(ProfileTabEventHandler.FetchUserProgress)
        }

        Navigator(ProfileVoyagerScreen()) { innerNavigator ->
            innerNavigator.lastItemOrNull?.Content()
        }
    }
}
