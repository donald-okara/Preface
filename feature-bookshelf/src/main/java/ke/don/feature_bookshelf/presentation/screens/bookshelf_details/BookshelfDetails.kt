package ke.don.feature_bookshelf.presentation.screens.bookshelf_details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import ke.don.common_datasource.remote.domain.states.BookshelfUiState
import ke.don.feature_bookshelf.R
import ke.don.feature_bookshelf.presentation.screens.bookshelf_details.components.BookList
import ke.don.feature_bookshelf.presentation.shared_components.BookshelfOptionsSheet
import ke.don.shared_domain.states.ResultState

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
                        uiState = uiState,
                        onRefresh = { eventHandler(BookshelfEventHandler.RefreshAction(bookshelfId)) },
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
                    text = stringResource(R.string.error_loading_bookshelf, state.message),
                    color = MaterialTheme.colorScheme.error
                )
            }

            else -> { // For Loading or Empty states
                CircularProgressIndicator()
            }
        }
    }

}
