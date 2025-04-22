package ke.don.shared_navigation

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.automirrored.filled.ManageSearch
import androidx.compose.material.icons.automirrored.outlined.LibraryBooks
import androidx.compose.material.icons.automirrored.outlined.ManageSearch
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import ke.don.feature_authentication.presentation.OnboardingScreen
import ke.don.feature_bookshelf.R
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
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current // Top-level navigator managing MainScreen
        // Define tab instances
        val myLibraryTab = remember {
            MyLibraryTab(
                onNavigateToAddBookshelf = {
                    Log.d("MainScreen", "Attempting to add bookshelf")
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
            ProfileTab(
                onSignOut = { navigator?.replaceAll(OnBoardingVoyagerScreen) },
                onNavigateToBookItem = {
                    navigator?.push(BookDetailsVoyagerScreen(it))
                }
            )
        }
        val tabs = listOf(myLibraryTab, searchTab, profileTab)

        TabNavigator(myLibraryTab) { tabNavigator ->
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = {
                            Image(
                                painter = painterResource(R.drawable.app_logo),
                                contentDescription = "My Library",
                                modifier = Modifier
                                    .clip(shape = RoundedCornerShape(16.dp))
                                    .scale(1.2f)
                                    .fillMaxHeight()
                            )
                        }
                    )
                },
                bottomBar = { BottomNavigationBar(tabs = tabs) }
            ) { innerPadding ->
                AnimatedContent(
                    modifier =
                    Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    label = "animated_tabs",
                    targetState = tabNavigator.current, // Animate when tab changes
                ) { tab ->
                    tab.Content()
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
fun BottomNavigationBar(tabs: List<Tab>) {
    val tabNavigator = LocalTabNavigator.current

    Column(Modifier.fillMaxWidth()) {
        HorizontalDivider()
        NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
            tabs.forEach { tab ->
                val isSelected = tab == tabNavigator.current

                val flipRotation by animateFloatAsState(
                    targetValue = if (isSelected) 360f else 0f,
                    animationSpec = tween(400, easing = FastOutSlowInEasing),
                    label = "icon_flip"
                )

                NavigationBarItem(
                    selected = isSelected,
                    onClick = { tabNavigator.current = tab },
                    icon = {
                        Icon(
                            painter = rememberVectorPainter(getTabIcon(tab, isSelected)),
                            contentDescription = tab.options.title,
                            modifier = Modifier.graphicsLayer {
                                if (isSelected) rotationY = flipRotation
                            }
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedIconColor = MaterialTheme.colorScheme.primary
                    ),
                    label = { Text(tab.options.title) }
                )
            }
        }
    }
}

@Composable
private fun getTabIcon(tab: Tab, isSelected: Boolean) = when (tab) {
    is MyLibraryTab -> if (isSelected) Icons.AutoMirrored.Filled.LibraryBooks else Icons.AutoMirrored.Outlined.LibraryBooks
    is SearchTab -> if (isSelected) Icons.AutoMirrored.Filled.ManageSearch else Icons.AutoMirrored.Outlined.ManageSearch
    else -> if (isSelected) Icons.Filled.Person else Icons.Outlined.Person
}
