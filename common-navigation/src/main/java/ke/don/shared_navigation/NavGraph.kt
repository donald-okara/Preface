package ke.don.shared_navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import ke.don.feature_authentication.presentation.OnboardingScreen
import ke.don.shared_navigation.bottom_navigation.tabs.MyLibraryTab
import ke.don.shared_navigation.bottom_navigation.tabs.ProfileTab
import ke.don.shared_navigation.bottom_navigation.tabs.SearchTab
import ke.don.shared_navigation.bottom_navigation.tabs.library.AddBookshelfVoyagerScreen
import ke.don.shared_navigation.bottom_navigation.tabs.library.BookshelfDetailsScreen
import ke.don.shared_navigation.bottom_navigation.tabs.search.BookDetailsVoyagerScreen


object OnBoardingVoyagerScreen : AndroidScreen() {
    private fun readResolve(): Any = OnBoardingVoyagerScreen

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        OnboardingScreen(
            onSuccessfulSignIn = { navigator?.replaceAll(MainScreen) }
        )
    }
}

object SplashVoyagerScreen : AndroidScreen() {
    private fun readResolve(): Any = SplashVoyagerScreen

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        PrefaceSplashScreen(
            onNavigateToMain = { navigator?.replaceAll(MainScreen) },
            onNavigateToOnBoarding = { navigator?.replaceAll(OnBoardingVoyagerScreen) }
        )
    }
}

object MainScreen : AndroidScreen() {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current // Top-level navigator managing MainScreen
        // Define tab instances
        val myLibraryTab = remember {
            MyLibraryTab(
                onNavigateToAddBookshelf = {
                    navigator?.push(AddBookshelfVoyagerScreen(null))
                },
                onNavigateToEditBookshelf = {
                    navigator?.push(AddBookshelfVoyagerScreen(it))
                },
                onNavigateToBookshelfItem = {
                    navigator?.push(BookshelfDetailsScreen(it))
                }
            )
        }
        val searchTab = remember {
            SearchTab(
                onNavigateToBookItem = {
                    navigator?.push(BookDetailsVoyagerScreen(it))
                }
            )
        }
        val profileTab = remember {
            ProfileTab(onSignOut = { navigator?.replaceAll(OnBoardingVoyagerScreen) })
        }
        val tabs = listOf(myLibraryTab, searchTab, profileTab)

        TabNavigator(myLibraryTab) { tabNavigator ->
            Scaffold(
                bottomBar = {
                    BottomNavigationBar(
                        tabs = tabs,
                        selectedTab = tabNavigator.current,
                        onTabSelected = { tabNavigator.current = it }
                    )
                }
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    CurrentTab()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    Navigator(SplashVoyagerScreen) { navigator ->
        navigator.lastItemOrNull?.Content()
    }
}


@Composable
fun BottomNavigationBar(
    tabs: List<Tab>,
    selectedTab: Tab,
    onTabSelected: (Tab) -> Unit
) {

    Column(Modifier.fillMaxWidth()) {
        HorizontalDivider()
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            tabs.forEach { item ->
                NavigationBarItem(
                    selected = selectedTab.key == item.key, // Compare by key (Voyager Tab)
                    onClick = { onTabSelected(item) }, // Switch tabs via TabNavigator
                    icon = {
                        item.options.icon?.let {
                            Icon(
                                painter = it,
                                contentDescription = item.options.title,
                                tint = if (selectedTab.key == item.key)
                                    MaterialTheme.colorScheme.primary // Apply primary color if selected
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant // Default color for unselected
                            )
                        }
                    },
                    label = { Text(text = item.options.title) }
                )
            }
        }
    }
}
