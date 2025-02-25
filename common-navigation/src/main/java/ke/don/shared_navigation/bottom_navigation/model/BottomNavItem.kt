package ke.don.shared_navigation.bottom_navigation.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.automirrored.filled.ManageSearch
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector
import cafe.adriel.voyager.navigator.tab.Tab
import ke.don.shared_navigation.bottom_navigation.tabs.MyLibraryTab
import ke.don.shared_navigation.bottom_navigation.tabs.ProfileTab
import ke.don.shared_navigation.bottom_navigation.tabs.SearchTab

sealed class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val tab: Tab
) {
    object MyLibrary : BottomNavItem(
        "My Library",
        Icons.AutoMirrored.Filled.LibraryBooks,
        MyLibraryTab
    )

    object Search : BottomNavItem(
        "Search",
        Icons.AutoMirrored.Filled.ManageSearch,
        SearchTab
    )

    object Profile : BottomNavItem(
        "Profile",
        Icons.Outlined.Person,
        ProfileTab
    )
}
