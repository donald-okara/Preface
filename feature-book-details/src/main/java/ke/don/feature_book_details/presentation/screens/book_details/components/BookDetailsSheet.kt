package ke.don.feature_book_details.presentation.screens.book_details.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ke.don.common_datasource.remote.domain.states.BookUiState
import ke.don.common_datasource.remote.domain.states.BookshelfBookDetailsState
import ke.don.common_datasource.remote.domain.states.ShowOptionState
import ke.don.feature_book_details.presentation.screens.book_details.BookDetailsEvent

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun BookDetailsSheet(
    modifier: Modifier = Modifier,
    onBookDetailsEvent: (BookDetailsEvent) -> Unit,
    bookUiState: BookUiState
){
    val sheetState = rememberModalBottomSheetState()

    if (bookUiState.showBottomSheet.showOption) {
        ModalBottomSheet(
            onDismissRequest = {
                onBookDetailsEvent(BookDetailsEvent.ShowBottomSheet)
            },
            sheetState = sheetState
        ) {
            LazyColumn(
                modifier = modifier.padding(16.dp)
            ) {
                stickyHeader {
                    BookDetailsSheetHeader(
                        modifier = modifier,
                        bookUrl = bookUiState.highestImageUrl,
                        title = bookUiState.bookDetails.volumeInfo.title
                    )
                }
                item {
                    BookshelfDropdownMenu(
                        loadingMessage = bookUiState.loadingJoke,
                        bookshelvesState = bookUiState.bookshelvesState,
                        showOptionState = bookUiState.showBookshelvesDropDown,
                        onRefreshBookshelves = { onBookDetailsEvent(BookDetailsEvent.FetchBookshelves) },
                        onExpandToggle = { onBookDetailsEvent(BookDetailsEvent.ToggleBookshelfDropDown) },
                        onItemClick = { onBookDetailsEvent(BookDetailsEvent.SelectBookshelf(it)) },
                        onConfirm = { onBookDetailsEvent(BookDetailsEvent.PushEditedBookshelfBooks) }
                    )
                }
            }
        }
    }
}


@Composable
fun BookDetailsSheetHeader(
    modifier: Modifier = Modifier,
    bookUrl: String?,
    title: String
){
    val imageSize = 80.dp
    val textSize = 18f
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            BookImage(
                onImageClick = {},
                imageUrl = bookUrl,
                modifier = modifier
                    .size(imageSize)
                    .padding(16.dp)
            )
            Spacer(modifier = modifier.width(12.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium.copy(fontSize = textSize.sp),
                maxLines = 1
            )
        }
        Spacer(modifier = modifier.height(8.dp))

        HorizontalDivider()

        Spacer(modifier = modifier.height(8.dp))

    }
}
