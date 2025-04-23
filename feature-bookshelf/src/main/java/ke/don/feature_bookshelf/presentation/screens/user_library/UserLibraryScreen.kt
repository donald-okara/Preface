package ke.don.feature_bookshelf.presentation.screens.user_library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CollectionsBookmark
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ke.don.common_datasource.remote.domain.states.UserLibraryState
import ke.don.feature_bookshelf.presentation.screens.user_library.components.BookshelfItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserLibraryScreen(
    modifier: Modifier = Modifier,
    eventHandler: (LibraryEventHandler)-> Unit,
    userLibraryState: UserLibraryState,
    onNavigateToEdit: (Int) -> Unit,
    onNavigateToBookshefItem: (Int) -> Unit,
    onAddBookshelf: () -> Unit,
) {
    val bookshelves = userLibraryState.userBookshelves
    val uniqueBookshelves = bookshelves.distinctBy { it.supabaseBookShelf.id }
    val selectedBookshelfId = userLibraryState.selectedBookshelfId

    val pullToRefreshState = rememberPullToRefreshState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                shape = CircleShape,
                onClick = onAddBookshelf
            ) {
                Icon(
                    imageVector = Icons.Outlined.CollectionsBookmark,
                    contentDescription = "Add Bookshelf",
                )
            }
        }
    ) {innerPadding ->
        PullToRefreshBox(
            contentAlignment = Alignment.TopCenter,
            isRefreshing = userLibraryState.isRefreshing,
            onRefresh = { eventHandler(LibraryEventHandler.RefreshAction) },
            state = pullToRefreshState,
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn (
                modifier = modifier
                    .padding(8.dp)
                    .align(Alignment.TopCenter),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {

                items(
                    items = uniqueBookshelves.sortedByDescending { it.books.size },
                    key = { bookShelf -> bookShelf.supabaseBookShelf.id }) { shelfItem ->
                    BookshelfItem(
                        onNavigateToBookshefItem = onNavigateToBookshefItem,
                        coverImages = shelfItem.books.mapNotNull { it.highestImageUrl?.takeIf { image -> image.isNotEmpty() } },
                        bookshelfTitle = shelfItem.supabaseBookShelf.name,
                        bookshelfSize = "${shelfItem.books.size} books",
                        bookshelfId = shelfItem.supabaseBookShelf.id,
                        onDeleteBookshelf = { bookshelfId ->
                            eventHandler(
                                LibraryEventHandler.DeleteBookshelf(bookshelfId = bookshelfId)
                            )
                        },
                        showBottomSheet = selectedBookshelfId == shelfItem.supabaseBookShelf.id, // Only show for selected item
                        onNavigateToEdit = onNavigateToEdit,
                        onShowBottomSheet = {
                            eventHandler(
                                LibraryEventHandler.SelectBookshelf(
                                    shelfItem.supabaseBookShelf.id
                                )
                            )
                        },
                        onDismissBottomSheet = {
                            eventHandler(LibraryEventHandler.SelectBookshelf(null))
                        }
                    )
                }

            }

        }

    }

}
