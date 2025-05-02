package ke.don.shared_navigation

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.automirrored.filled.ManageSearch
import androidx.compose.material.icons.automirrored.outlined.LibraryBooks
import androidx.compose.material.icons.automirrored.outlined.ManageSearch
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import ke.don.feature_authentication.presentation.OnboardingScreen
import ke.don.shared_components.components.PrefaceSnackBar
import ke.don.shared_navigation.app_scaffold.ConfigureAppBars
import ke.don.shared_navigation.app_scaffold.ScaffoldViewModel
import ke.don.shared_navigation.bottom_navigation.tabs.MyLibraryTab
import ke.don.shared_navigation.bottom_navigation.tabs.ProfileTab
import ke.don.shared_navigation.bottom_navigation.tabs.SearchTab
import kotlinx.coroutines.launch

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
        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }

        val appBarViewModel: ScaffoldViewModel = hiltViewModel()
        val appBarState = appBarViewModel.state
        val internetStatus by appBarViewModel.internetAvailability.isInternetAvailable
            .collectAsState(initial = true)

        // Handle no-internet snackbar
        LaunchedEffect(internetStatus) {
            if (!internetStatus) {
                coroutineScope.launch {
                    val result = snackbarHostState.showSnackbar(
                        message = context.getString(R.string.no_internet_connection),
                        actionLabel = context.getString(R.string.turn_on),
                        duration = SnackbarDuration.Indefinite
                    )

                    if (result == SnackbarResult.ActionPerformed) {
                        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY)
                        } else {
                            Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        }

                        if (context is Activity) {
                            context.startActivityForResult(intent, 100)
                        } else {
                            context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                        }
                    }
                }
            }
        }

        // Define bottom tabs
        val myLibraryTab = remember { MyLibraryTab() }
        val searchTab = remember { SearchTab() }
        val profileTab = remember { ProfileTab() }
        val tabs = listOf(myLibraryTab, searchTab, profileTab)

        TabNavigator(myLibraryTab) { tabNavigator ->
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(appBarState.title) },
                        navigationIcon = appBarState.navigationIcon ?: {},
                        actions = appBarState.actions
                    )
                },
                bottomBar = {
                    if (appBarState.showBottomBar) {
                        BottomNavigationBar(tabs = tabs)
                    }
                },
                snackbarHost = {
                    SnackbarHost(hostState = snackbarHostState) { data ->

                        PrefaceSnackBar(
                            icon = Icons.Filled.WifiOff,
                            title = data.visuals.message,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer,
                            backgroundColor = MaterialTheme.colorScheme.errorContainer,
                            onOptionClick = {},
                            trailingItem = {
                                IconButton(onClick = {data.performAction()}) {
                                    Icon(
                                        tint = MaterialTheme.colorScheme.onErrorContainer,
                                        imageVector = Icons.Filled.ArrowUpward,
                                        contentDescription = stringResource(R.string.open_network_settings)
                                    )
                                }
                            }
                        )
                    }
                }
            ) { innerPadding ->
                AnimatedContent(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    label = "animated_tabs",
                    targetState = tabNavigator.current
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
                    animationSpec = tween(1000, easing = FastOutSlowInEasing),
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
