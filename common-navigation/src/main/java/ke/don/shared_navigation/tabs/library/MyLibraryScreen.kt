package ke.don.shared_navigation.tabs.library

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import ke.don.feature_bookshelf.presentation.screens.add_bookshelf.AddBookshelfRoute
import ke.don.feature_bookshelf.presentation.screens.user_library.UserLibraryScreen
import ke.don.shared_navigation.MainBottomAppBar

object MyLibraryScreen : AndroidScreen() {
    private fun readResolve(): Any = MyLibraryScreen

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "My library",
                            modifier = Modifier.padding(8.dp),
                            maxLines = 1
                        )
                    }
                )
            },

            bottomBar = { MainBottomAppBar() },
        ){innerPadding->
            UserLibraryScreen(
                paddingValues = innerPadding,
                onAddBookshelf = {
                    navigator?.push(AddBookshelfVoyagerScreen)
                }
            )
        }
    }
}

object AddBookshelfVoyagerScreen : AndroidScreen() {
    private fun readResolve(): Any = AddBookshelfVoyagerScreen

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "Add Bookshelf"
                        )
                    }

                )
            }
        ) { innerPadding ->
            AddBookshelfRoute(
                modifier = Modifier.padding(innerPadding),
                paddingValues = innerPadding,
                onNavigateBack = { navigator?.pop()  }
            )
        }
    }

}

