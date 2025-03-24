package ke.don.shared_navigation

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.CurrentTab
import ke.don.feature_authentication.presentation.OnboardingScreen
import ke.don.shared_domain.values.Screens
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import ke.don.shared_navigation.bottom_navigation.model.BottomNavItem
import ke.don.shared_navigation.bottom_navigation.tabs.MyLibraryTab
import ke.don.shared_navigation.bottom_navigation.tabs.library.MyLibraryScreen
import ke.don.shared_navigation.bottom_navigation.tabs.search.SearchVoyagerScreen


object OnBoardingVoyagerScreen : AndroidScreen() {
    private fun readResolve(): Any = OnBoardingVoyagerScreen

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        OnboardingScreen(
            onSuccessfulSignIn = {
                navigator?.replaceAll(SearchVoyagerScreen)
            }
        )
    }
}

object SplashVoyagerScreen : AndroidScreen() {
    private fun readResolve(): Any = SplashVoyagerScreen

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        PrefaceSplashScreen(
            onNavigateToMain = {
                navigator?.replaceAll(SearchVoyagerScreen)
            },
            onNavigateToOnBoarding = {
                navigator?.replaceAll(OnBoardingVoyagerScreen)
            }
        )
    }
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AppNavigation() {
    Log.d("NavGraph", "Start destination: ${Screens.Splash.route}")

    Navigator(SplashVoyagerScreen) { navigator ->
        when (val currentScreen = navigator.lastItemOrNull) {
            is SearchVoyagerScreen, is MyLibraryScreen -> {
                // Screens that require bottom navigation should be inside TabNavigator
                TabNavigator(MyLibraryTab) {
                    CurrentTab()
                }
            }

            else -> {
                // For all other screens (like Book Details), just show their content without TabNavigator
                currentScreen?.Content()
            }
        }
    }
}


@Composable
fun BottomNavigationBar(
    selectedTab: Tab,
    onTabSelected: (Tab) -> Unit
) {
    val tabs = listOf(
        BottomNavItem.MyLibrary,
        BottomNavItem.Search,
        BottomNavItem.Profile
    )

    Column(Modifier.fillMaxWidth()) {
        HorizontalDivider()

        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            tabs.forEach { item ->
                NavigationBarItem(
                    selected = selectedTab.key == item.tab.key, // Compare by key (Voyager Tab)
                    onClick = { onTabSelected(item.tab) }, // Switch tabs via TabNavigator
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title
                        )
                    },
                    label = { Text(text = item.title) }
                )
            }
        }
    }
}
