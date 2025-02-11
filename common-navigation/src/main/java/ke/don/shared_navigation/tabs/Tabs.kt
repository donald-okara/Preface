package ke.don.shared_navigation.tabs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.automirrored.filled.ManageSearch
import androidx.compose.material.icons.outlined.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import ke.don.shared_navigation.tabs.library.MyLibraryScreen
import ke.don.shared_navigation.tabs.search.SearchVoyagerScreen

object MyLibraryTab : Tab {
    private fun readResolve(): Any = MyLibraryTab

    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(image = Icons.AutoMirrored.Filled.LibraryBooks)

            val tabName = "My library"

            return remember { TabOptions(index = 0u, title = tabName, icon = icon) }
        }

    @Composable
    override fun Content() {
        Navigator(screen = MyLibraryScreen)
    }
}

object SearchTab : Tab {
    private fun readResolve(): Any = SearchTab

    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(image = Icons.AutoMirrored.Filled.ManageSearch)

            val tabName = "Search"

            return remember { TabOptions(index = 0u, title = tabName, icon = icon) }
        }

    @Composable
    override fun Content() {
        Navigator(screen = SearchVoyagerScreen)
    }
}

object ProfileTab : Tab {
    private fun readResolve(): Any = ProfileTab

    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(image = Icons.Outlined.Person)

            val tabName = "Profile"

            return remember { TabOptions(index = 0u, title = tabName, icon = icon) }
        }

    @Composable
    override fun Content() {
        Navigator(screen = SearchVoyagerScreen)
    }
}
