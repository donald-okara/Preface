package ke.don.feature_bookshelf.presentation.screens.bookshelf_details.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import ke.don.common_datasource.remote.domain.states.BookshelfUiState
import ke.don.shared_components.components.BookListItem
import ke.don.shared_domain.states.ResultState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookList(
    modifier: Modifier = Modifier,
    uiState: BookshelfUiState,
    onRefresh: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    onItemClick: (String) -> Unit
){
    val uniqueBooks = uiState.bookShelf.books.distinctBy { it.bookId } // Ensure uniqueness

    PullToRefreshBox(
        isRefreshing = uiState.resultState == ResultState.Loading,
        onRefresh = {onRefresh()},
        modifier = modifier,
    ){
        LazyColumn(
            modifier = modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .padding(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            item {
                BookshelfHeader(
                    coverImages = uiState.bookShelf.books.mapNotNull { it.highestImageUrl?.takeIf { image -> image.isNotEmpty() } },
                    bookshelfName = uiState.bookShelf.name,
                    bookshelfDescription = uiState.bookShelf.description,
                    bookshelfSize = "${uiState.bookShelf.books.size} books",
                    scrollBehavior = scrollBehavior,
                    modifier = modifier
                )
            }
            items(items = uniqueBooks, key = { book -> book.bookId }) { book ->
                BookListItem(
                    bookId = book.bookId,
                    imageUrl = book.highestImageUrl,
                    title = book.title,
                    description = book.description,
                    modifier = modifier.padding(4.dp),
                    onItemClick = onItemClick
                )
            }
        }
    }
}
