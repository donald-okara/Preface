    package ke.don.shared_navigation

    import android.annotation.SuppressLint
    import android.util.Log
    import androidx.compose.foundation.clickable
    import androidx.compose.foundation.layout.Arrangement
    import androidx.compose.foundation.layout.Column
    import androidx.compose.foundation.layout.Row
    import androidx.compose.foundation.layout.RowScope
    import androidx.compose.foundation.layout.fillMaxSize
    import androidx.compose.foundation.layout.fillMaxWidth
    import androidx.compose.foundation.layout.padding
    import androidx.compose.foundation.layout.size
    import androidx.compose.material3.BottomAppBar
    import androidx.compose.material3.BottomAppBarDefaults
    import androidx.compose.material3.Icon
    import androidx.compose.material3.MaterialTheme
    import androidx.compose.material3.Scaffold
    import androidx.compose.material3.Text
    import androidx.compose.runtime.Composable
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.graphics.Color
    import androidx.compose.ui.graphics.painter.Painter
    import androidx.compose.ui.unit.dp
    import cafe.adriel.voyager.androidx.AndroidScreen
    import cafe.adriel.voyager.navigator.LocalNavigator
    import cafe.adriel.voyager.navigator.Navigator
    import cafe.adriel.voyager.navigator.tab.CurrentTab
    import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
    import ke.don.feature_authentication.presentation.OnboardingScreen
    import ke.don.shared_domain.values.Screens
    import cafe.adriel.voyager.navigator.tab.Tab
    import cafe.adriel.voyager.navigator.tab.TabNavigator
    import ke.don.shared_navigation.tabs.search.MyLibraryTab
    import ke.don.shared_navigation.tabs.search.ProfileTab
    import ke.don.shared_navigation.tabs.search.SearchTab
    import ke.don.shared_navigation.tabs.search.SearchVoyagerScreen


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
                is SearchVoyagerScreen -> {
                    // Screens that require bottom navigation should be inside TabNavigator
                    TabNavigator(SearchTab) {
                        Scaffold(
                            modifier = Modifier.fillMaxSize(),
                            bottomBar = { MainBottomAppBar() },
                            containerColor = MaterialTheme.colorScheme.primary
                        ) {
                            CurrentTab()
                        }
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
    fun MainBottomAppBar(
        modifier: Modifier = Modifier
    ) {
        BottomAppBar(
            containerColor = MaterialTheme.colorScheme.surface,
            windowInsets = BottomAppBarDefaults.windowInsets
        ) {
            Row(
                modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TabNavigationItems(tab = MyLibraryTab)
                TabNavigationItems(tab = SearchTab)
                TabNavigationItems(tab = ProfileTab)

            }
        }
    }

    @Composable
    fun RowScope.TabNavigationItems(tab: Tab) {
        val tabNavigator = LocalTabNavigator.current

        BottomNavItem(
            selected = tabNavigator.current == tab,
            onClick = { tabNavigator.current = tab },
            icon = tab.options.icon!!,
            label = tab.options.title
        )
    }

    @SuppressLint("SuspiciousIndentation")
    @Composable
    fun BottomNavItem(
        modifier: Modifier = Modifier,
        selected: Boolean,
        onClick: () -> Unit,
        icon: Painter,
        label: String
    ) {
        val color = if (selected) MaterialTheme.colorScheme.secondary else Color.Gray
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
            modifier = modifier.clickable(onClick = onClick)
        ) {
            Icon(
                painter = icon,
                contentDescription = label,
                tint = color,
                modifier = modifier.size(24.dp)
            )
            Text(text = label, style = MaterialTheme.typography.labelMedium, color = color)
        }
    }
