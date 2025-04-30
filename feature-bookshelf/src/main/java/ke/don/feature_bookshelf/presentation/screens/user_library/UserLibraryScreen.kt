package ke.don.feature_bookshelf.presentation.screens.user_library

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import ke.don.shared_components.components.EmptyScreen

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
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
    val uniqueBookshelves = bookshelves.distinctBy { it.bookshelfRef.id }

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
    ) {
        PullToRefreshBox(
            contentAlignment = Alignment.TopCenter,
            isRefreshing = userLibraryState.isRefreshing,
            onRefresh = { eventHandler(LibraryEventHandler.RefreshAction) },
            state = pullToRefreshState,
            modifier = modifier
                .fillMaxSize()
        ) {
            if(bookshelves.isEmpty()){
                EmptyScreen(
                    icon = Icons.Outlined.CollectionsBookmark,
                    message = "You have no bookshelves yet",
                    action = {onAddBookshelf()},
                    actionText = "Build your library book by book",
                    modifier = modifier.fillMaxWidth()
                )
            }
            LazyColumn (
                modifier = modifier
                    .align(Alignment.TopCenter),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                items(
                    items = uniqueBookshelves.sortedByDescending { it.books.size },
                    key = { bookShelf -> bookShelf.bookshelfRef.id }) { shelfItem ->
                    val showOptionSheet = userLibraryState.selectedBookshelfId == shelfItem.bookshelfRef.id
                    BookshelfItem(
                        modifier = modifier,
                        bookshelf = shelfItem,
                        onNavigateToBookshelfItem = onNavigateToBookshefItem,
                        eventHandler = eventHandler,
                        onNavigateToEdit = onNavigateToEdit,
                        showOptionsSheet = showOptionSheet
                    )
                }
            }
        }
    }
}
