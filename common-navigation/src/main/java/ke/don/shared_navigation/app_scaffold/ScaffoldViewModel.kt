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

data class AppBarState @OptIn(ExperimentalMaterial3Api::class) constructor(
    val title: String = "",
    val actions: @Composable RowScope.() -> Unit = {},
    val navigationIcon: @Composable (() -> Unit)? = null,
    val scrollBehavior: TopAppBarScrollBehavior? = null,
    val showBottomBar: Boolean = true // New property
)

@HiltViewModel
class ScaffoldViewModel : ViewModel() {
    var state by mutableStateOf(AppBarState())
        private set

    fun updateState(newState: AppBarState) {
        state = newState
    }
}