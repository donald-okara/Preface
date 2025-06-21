package ke.don.shared_navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import ke.don.feature_authentication.presentation.OnboardingScreen
import ke.don.shared_navigation.app_scaffold.ConfigureAppBars
import ke.don.shared_navigation.app_scaffold.ScaffoldViewModel
import ke.don.shared_navigation.bottom_navigation.tabs.AppShell
import ke.don.shared_navigation.bottom_navigation.tabs.MyLibraryTab
import ke.don.shared_navigation.bottom_navigation.tabs.ProfileTab
import ke.don.shared_navigation.bottom_navigation.tabs.SearchTab

object OnBoardingVoyagerScreen : AndroidScreen() {
    private fun readResolve(): Any = OnBoardingVoyagerScreen

    @Composable
    override fun Content() {
        ConfigureAppBars(
            title = "",
            showBottomBar = false
        )

        val navigator = LocalNavigator.current
        OnboardingScreen(
            onSuccessfulSignIn = { navigator?.replaceAll(MainScreen) }
        )
    }
}

val navItems
    @Composable
    get() = remember {
        buildList {
            add(BottomNavScreen(MyLibraryTab(), "Home", Icons.Filled.Home, Icons.Outlined.Home))
            add(BottomNavScreen(SearchTab(), "Search", Icons.Filled.Search, Icons.Outlined.Search))
            add(BottomNavScreen(ProfileTab(), "Profile", Icons.Filled.Person, Icons.Outlined.Person))
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

object MainScreen : Screen {
    @Composable
    override fun Content() {
        Navigator(navItems.first().screen) { navigator ->
            navigator.lastItem.Content()
        }
    }
}

@Composable
fun AppNavigation() {
    val viewModel: ScaffoldViewModel = hiltViewModel()
    val appBarState = viewModel.state


    val initialScreen = OnBoardingVoyagerScreen

    Navigator(initialScreen) { navigator ->
        val currentScreen = navigator.lastItem

        AppShell(
            appBarState = appBarState,
            bottomNavItems = navItems,
            currentScreen = currentScreen,
            scaffoldViewModel = viewModel,
            navigator = navigator,
            onNavItemSelected = { selected ->
                if (navigator.lastItem::class != selected::class) {
                    navigator.replace(selected)
                }
            },
        ) { innerPadding ->

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
            ) {
                currentScreen.Content()
            }
        }
    }

}



data class BottomNavScreen(
    val screen: Screen,
    val label: String,
    val iconSelected: ImageVector,
    val iconUnselected: ImageVector,
)
