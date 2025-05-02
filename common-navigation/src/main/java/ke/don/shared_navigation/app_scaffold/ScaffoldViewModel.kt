@file:OptIn(ExperimentalMaterial3Api::class)

package ke.don.shared_navigation.app_scaffold

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.don.common_datasource.remote.domain.error_handler.InternetAvailability
import javax.inject.Inject

data class AppBarState (
    val title: String = "",
    val actions: @Composable RowScope.() -> Unit = {},
    val navigationIcon: @Composable (() -> Unit)? = null,
    val scrollBehavior: TopAppBarScrollBehavior? = null,
    val showBackButton: Boolean = false,
    val showBottomBar: Boolean = true // New property
)

@HiltViewModel
class ScaffoldViewModel @Inject constructor(
    val internetAvailability: InternetAvailability
) : ViewModel() {
    var state by mutableStateOf(AppBarState())
        private set

    fun updateState(newState: AppBarState) {
        state = newState
    }
}