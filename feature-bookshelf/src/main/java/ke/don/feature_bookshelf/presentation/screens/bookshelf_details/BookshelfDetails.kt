package ke.don.feature_bookshelf.presentation.screens.bookshelf_details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ke.don.common_datasource.remote.domain.states.BookshelfUiState
import ke.don.feature_bookshelf.presentation.screens.bookshelf_details.components.BookList
import ke.don.feature_bookshelf.presentation.shared_components.BookshelfOptionsSheet
import ke.don.shared_domain.states.ResultState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookshelfDetailsRoute(
    modifier: Modifier = Modifier,
    bookshelfId: Int,
    uiState: BookshelfUiState,
    eventHandler: (BookshelfEventHandler) -> Unit,
    onNavigateToEdit: (Int) -> Unit,
    navigateBack: () -> Unit,
    onItemClick: (String) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
    ) {
        when (val state = uiState.resultState) {
            is ResultState.Success -> {
                if (uiState.bookShelf.id == -1 || uiState.bookShelf.name == "" || uiState.bookShelf.id != bookshelfId) { // Loading state indicator
                    CircularProgressIndicator()
                } else {
                    BookList(
                        modifier = modifier,
                        uiState = uiState,
                        onRefresh = { eventHandler(BookshelfEventHandler.RefreshAction(bookshelfId)) },
                        scrollBehavior = scrollBehavior,
                        onItemClick = onItemClick
                    )
                    BookshelfOptionsSheet(
                        modifier = modifier,
                        onNavigateToEdit = onNavigateToEdit,
                        showOptionsSheet = uiState.showOptionsSheet,
                        onToggleBottomSheet = { eventHandler(BookshelfEventHandler.ToggleBottomSheet) },
                        bookCovers = uiState.bookShelf.books.mapNotNull { supabaseBook ->
                            supabaseBook.highestImageUrl.takeIf { !it.isNullOrBlank() }
                        },
                        title = uiState.bookShelf.name,
                        bookshelfSize = "${uiState.bookShelf.books.size} books",
                        bookshelfId = bookshelfId,
                        onDeleteBookshelf = {
                            eventHandler(
                                BookshelfEventHandler.DeleteBookshelf(
                                    onNavigateBack = navigateBack,
                                    bookshelfId
                                )
                            )
                        },
                    )
                }
            }

            is ResultState.Error -> {
                Text(
                    text = "Error loading bookshelf: ${state.message}",
                    color = MaterialTheme.colorScheme.error
                )
            }

            else -> { // For Loading or Empty states
                CircularProgressIndicator()
            }
        }
    }

}
