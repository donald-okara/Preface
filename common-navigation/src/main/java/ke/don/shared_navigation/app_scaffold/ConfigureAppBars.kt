package ke.don.shared_navigation.app_scaffold

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import ke.don.shared_navigation.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigureAppBars(
    title: String = "",
    actions: @Composable RowScope.() -> Unit = {},
    showBackButton: Boolean = false,
    showBottomBar: Boolean = true,
) {
    val appBarViewModel: ScaffoldViewModel = hiltViewModel()
    val navigator = LocalNavigator.current

    LaunchedEffect(appBarViewModel) {
        appBarViewModel.updateState(
            AppBarState(
                title = title,
                actions = actions,
                navigationIcon = if (showBackButton) {
                    {
                        IconButton(onClick = { navigator?.pop() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back))
                        }
                    }
                } else null,
                showBottomBar = showBottomBar
            )
        )
    }
}