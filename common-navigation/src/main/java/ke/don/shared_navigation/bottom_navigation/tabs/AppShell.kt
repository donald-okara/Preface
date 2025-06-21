package ke.don.shared_navigation.bottom_navigation.tabs

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import ke.don.shared_components.components.PrefaceSnackBar
import ke.don.shared_navigation.BottomNavScreen
import ke.don.shared_navigation.R
import ke.don.shared_navigation.app_scaffold.AppBarState
import ke.don.shared_navigation.app_scaffold.ScaffoldViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppShell(
    appBarState: AppBarState,
    bottomNavItems: List<BottomNavScreen>,
    currentScreen: Screen?,
    scaffoldViewModel: ScaffoldViewModel,
    onNavItemSelected: (Screen) -> Unit,
    navigator: Navigator?, // Optional if needed for back nav
    drawerContent: @Composable (() -> Unit)? = null,
    content: @Composable (PaddingValues) -> Unit,
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val internetStatus by scaffoldViewModel.internetAvailability.isInternetAvailable
        .collectAsState(initial = true)

    // Handle no-internet snackbar
    LaunchedEffect(internetStatus) {
        if (!internetStatus) {
            scope.launch {
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


    val navigationIcon: @Composable (() -> Unit)? = when {
        appBarState.showBackButton -> {
            {
                IconButton(onClick = { navigator?.pop() }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                    )
                }
            }
        }
        else -> null
    }


    val bottomBarContent: @Composable () -> Unit = {
        AnimatedVisibility(
            visible = appBarState.showBottomBar && bottomNavItems.isNotEmpty(),
        ) {
            BottomNavigationBar(
                items = bottomNavItems,
                currentScreen = currentScreen,
                onItemSelected = onNavItemSelected,
            )
        }
    }

    val scaffoldContent: @Composable () -> Unit = {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.surface,
            topBar =
                {
                    TopAppBar(
                        title = { Text(appBarState.title) },
                        navigationIcon = appBarState.navigationIcon ?: {},
                        actions = appBarState.actions
                    )
                },
            bottomBar = bottomBarContent,
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
            },
            content = content,
        )
    }


    scaffoldContent()

}

@Composable
fun BottomNavigationBar(
    items: List<BottomNavScreen>,
    currentScreen: Screen?,
    onItemSelected: (Screen) -> Unit,
) {
    Column(Modifier.fillMaxWidth()) {
        HorizontalDivider()
        NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
            items.forEach { item ->
                val isSelected = currentScreen?.javaClass == item.screen.javaClass

                val flipRotation by animateFloatAsState(
                    targetValue = if (isSelected) 360f else 0f,
                    animationSpec = tween(1000, easing = FastOutSlowInEasing),
                    label = "icon_flip",
                )

                NavigationBarItem(
                    selected = isSelected,
                    onClick = { onItemSelected(item.screen) },
                    icon = {
                        Icon(
                            imageVector = if (isSelected) item.iconSelected else item.iconUnselected,
                            contentDescription = item.label,
                            modifier = Modifier.graphicsLayer {
                                if (isSelected) rotationY = flipRotation
                            },
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedIconColor = MaterialTheme.colorScheme.primary
                    ),
                    label = {
                        Text(
                            text = item.label,
                            textAlign = TextAlign.Center,
                        )
                    },
                )
            }
        }
    }
}