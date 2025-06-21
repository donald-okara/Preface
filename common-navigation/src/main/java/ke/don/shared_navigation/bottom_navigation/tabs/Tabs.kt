package ke.don.shared_navigation.bottom_navigation.tabs

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import ke.don.shared_navigation.bottom_navigation.tabs.library.MyLibraryScreen
import ke.don.shared_navigation.bottom_navigation.tabs.profile.ProfileVoyagerScreen
import ke.don.shared_navigation.bottom_navigation.tabs.search.SearchVoyagerScreen

class MyLibraryTab() : Screen {

    @Composable
    override fun Content() {
        MyLibraryScreen().Content()
    }
}

class SearchTab(
    private val searchQuery: String? = null
) : Screen {

    @Composable
    override fun Content() {
        SearchVoyagerScreen(searchQuery).Content()
    }
}

class ProfileTab() : Screen {

    @Composable
    override fun Content() {
        ProfileVoyagerScreen().Content()
    }
}
