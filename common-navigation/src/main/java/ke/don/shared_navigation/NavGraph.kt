package ke.don.shared_navigation

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.hilt.navigation.compose.hiltViewModel
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import ke.don.feature_authentication.presentation.OnboardingScreen
import ke.don.shared_navigation.app_scaffold.ConfigureAppBars
import ke.don.shared_navigation.app_scaffold.ScaffoldViewModel
import ke.don.shared_navigation.bottom_navigation.tabs.MyLibraryTab
import ke.don.shared_navigation.bottom_navigation.tabs.ProfileTab
import ke.don.shared_navigation.bottom_navigation.tabs.SearchTab


object OnBoardingVoyagerScreen : AndroidScreen() {
    private fun readResolve(): Any = OnBoardingVoyagerScreen

    @OptIn(ExperimentalMaterial3Api::class)
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
        val appBarViewModel: ScaffoldViewModel = hiltViewModel()
        val appBarState = appBarViewModel.state

        LaunchedEffect(appBarState) {
            Log.d("MainScreen", "State:: ${appBarState.title}, ${appBarState.navigationIcon}")
        }

        // Define tab instances
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

        val myLibraryTab = remember {
            MyLibraryTab()
        }
        val searchTab = remember {
            SearchTab()
        }
        val profileTab = remember {
            ProfileTab()
        }
        val tabs = listOf(myLibraryTab, searchTab, profileTab)

        TabNavigator(myLibraryTab) { tabNavigator ->
            Scaffold(
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    CenterAlignedTopAppBar(
                        scrollBehavior = appBarState.scrollBehavior,
                        title = { Text(appBarState.title) },
                        navigationIcon =  appBarState.navigationIcon ?:{},//Elvis monkey 😆
                        actions = appBarState.actions
                    )
                },
                bottomBar = {
                    if (appBarState.showBottomBar) {
                        BottomNavigationBar(tabs = tabs)
                    }

                }
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
