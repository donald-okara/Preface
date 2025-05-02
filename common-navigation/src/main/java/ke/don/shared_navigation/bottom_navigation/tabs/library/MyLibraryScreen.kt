package ke.don.shared_navigation.bottom_navigation.tabs.library

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import ke.don.feature_bookshelf.presentation.screens.add_bookshelf.AddBookshelfEventHandler
import ke.don.feature_bookshelf.presentation.screens.add_bookshelf.AddBookshelfRoute
import ke.don.feature_bookshelf.presentation.screens.add_bookshelf.AddBookshelfViewModel
import ke.don.feature_bookshelf.presentation.screens.bookshelf_details.BookshelfDetailsRoute
import ke.don.feature_bookshelf.presentation.screens.bookshelf_details.BookshelfDetailsViewModel
import ke.don.feature_bookshelf.presentation.screens.bookshelf_details.BookshelfEventHandler
import ke.don.feature_bookshelf.presentation.screens.user_library.LibraryEventHandler
import ke.don.feature_bookshelf.presentation.screens.user_library.UserLibraryScreen
import ke.don.feature_bookshelf.presentation.screens.user_library.UserLibraryViewModel
import ke.don.shared_navigation.R
import ke.don.shared_navigation.app_scaffold.ConfigureAppBars
import ke.don.shared_navigation.bottom_navigation.tabs.search.BookDetailsVoyagerScreen

class MyLibraryScreen (): AndroidScreen(){
    @Composable
    override fun Content() {
        ConfigureAppBars(
            title = "My Library",
            showBottomBar = true
        )

        val navigator = LocalNavigator.current
        val viewModel: UserLibraryViewModel = hiltViewModel()
        val state by viewModel.userLibraryState.collectAsState()
        val eventHandler = viewModel::handleEvent

        LaunchedEffect(viewModel) {
            eventHandler(LibraryEventHandler.FetchBookshelves)
        }

        UserLibraryScreen(
            userLibraryState = state,
            eventHandler = eventHandler,
            onNavigateToBookshefItem = { bookshelfId ->
                navigator?.push(BookshelfDetailsScreen(bookshelfId))
            },
            onAddBookshelf = {
                navigator?.push(AddBookshelfVoyagerScreen(null))
            },
            onNavigateToEdit = {bookshelfId->
                navigator?.push(AddBookshelfVoyagerScreen(bookshelfId))
            }
        )
    }

}

class AddBookshelfVoyagerScreen(private val bookshelfId: Int?) : AndroidScreen() {
    private fun readResolve(): Any = AddBookshelfVoyagerScreen(bookshelfId = bookshelfId)

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val viewModel: AddBookshelfViewModel = hiltViewModel()
        val state by viewModel.addBookshelfState.collectAsState()
        val handleEvent = viewModel::handleEvent

        DisposableEffect(viewModel) {
            handleEvent(AddBookshelfEventHandler.FetchBookshelf(bookshelfId))

            onDispose {
                handleEvent(AddBookshelfEventHandler.OnCleared)
            }
        }

        ConfigureAppBars(
            title = "",
            showBottomBar = false,
            showBackButton = true,
        )

        AddBookshelfRoute(
            onNavigateBack = { navigator?.pop() },
            state = state,
            handleEvent = handleEvent
        )
    }

}

class BookshelfDetailsScreen(private val bookshelfId: Int) : AndroidScreen() {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val viewModel: BookshelfDetailsViewModel = hiltViewModel()
        val bookshelfUiState by viewModel.bookshelfUiState.collectAsState()
        val eventHandler = viewModel::handleEvents

        LaunchedEffect(viewModel) {
            eventHandler(BookshelfEventHandler.FetchBookshelf(bookshelfId))
        }

        ConfigureAppBars(
            title = "",
            showBottomBar = false,
            showBackButton = true,
            actions = {
                IconButton(onClick = { eventHandler(BookshelfEventHandler.ToggleBottomSheet) }) {
                    Icon(
                        imageVector = Icons.Outlined.MoreVert,
                        contentDescription = stringResource(R.string.options)
                    )
                }
            }
        )

        BookshelfDetailsRoute(
            bookshelfId = bookshelfId,
            uiState = bookshelfUiState,
            eventHandler = eventHandler,
            navigateBack = { navigator?.pop() },
            onItemClick = { bookId ->
                navigator?.push(BookDetailsVoyagerScreen(bookId))
            },
            onNavigateToEdit = { bookshelfId ->
                navigator?.push(AddBookshelfVoyagerScreen(bookshelfId))
            }
        )
    }
}
