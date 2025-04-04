package ke.don.feature_book_details.presentation.screens.book_details.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ke.don.common_datasource.remote.domain.states.BookshelfBookDetailsState
import ke.don.common_datasource.remote.domain.states.ShowOptionState
import ke.don.shared_components.SheetOptionItem

@Composable
fun BookshelfDropdownMenu(
    modifier: Modifier = Modifier,
    uniqueBookshelves: List<BookshelfBookDetailsState>,
    showBookshelvesState: ShowOptionState,
    onConfirm: () -> Unit,
    onExpandToggle: () -> Unit,
    onItemClick: (Int) -> Unit = {},
) {

    val bookIsPresentList = uniqueBookshelves.filter { it.isBookPresent }
    val bookIsNotPresentList = uniqueBookshelves.filter { !it.isBookPresent }

    Box(
        modifier = modifier
    ) {
        SheetOptionItem(
            modifier = modifier,
            icon = Icons.AutoMirrored.Filled.LibraryBooks,
            title = "Add to library",
            onOptionClick = {
                onExpandToggle()
            }
        )
        DropdownMenu(
            expanded = showBookshelvesState.showOption,
            onDismissRequest = { onExpandToggle() },
            modifier = modifier
                .padding(8.dp)
        ) {
            DropdownMenuItem(
                text = { Text("Add a bookshelf") },
                onClick = { /* Do something... */ }
            )
            // You can wrap your content in a scrollable Column
            Column(
                modifier = modifier
                    .height(200.dp) // Set your desired height
                    .verticalScroll(rememberScrollState()) // Make it scrollable
            ) {
                bookIsPresentList
                    .forEach { bookshelf ->
                        BookshelfItem(
                            bookshelfName = bookshelf.bookshelfBookDetails.name,
                            isSelected = bookshelf.isBookPresent,
                            bookshelfId = bookshelf.bookshelfBookDetails.id,
                            onItemClick = {
                                onItemClick(bookshelf.bookshelfBookDetails.id)
                            }
                        )
                    }
                HorizontalDivider()
                bookIsNotPresentList
                    .forEach { bookshelf ->
                        BookshelfItem(
                            bookshelfName = bookshelf.bookshelfBookDetails.name,
                            isSelected = bookshelf.isBookPresent,
                            bookshelfId = bookshelf.bookshelfBookDetails.id,
                            onItemClick = {
                                onItemClick(bookshelf.bookshelfBookDetails.id)
                            }
                        )
                    }
            }

            AddBookRow(
                onCancel = {
                    onExpandToggle()
                },
                isLoading = showBookshelvesState.isLoading,
                onConfirm = {
                    onConfirm()
                }
            )
        }

    }
}

@Composable
fun BookshelfItem(
    modifier: Modifier = Modifier,
    bookshelfName: String,
    bookshelfId: Int,
    onItemClick: (Int) -> Unit = {},
    isSelected: Boolean = false,
){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .padding(8.dp)
            .width(150.dp)
    ) {
        Text(
            text = bookshelfName
        )

        Spacer(modifier = modifier.weight(1f))

        RadioButton(
            selected = isSelected,
            onClick = { onItemClick(bookshelfId) }
        )


    }

}

@Composable
fun AddBookRow(
    modifier: Modifier = Modifier,
    onCancel: () -> Unit,
    isLoading: Boolean,
    onConfirm: () -> Unit,
){
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(8.dp)
    ) {
        OutlinedButton(
            onCancel
        ) {
            Text(
                text = "Cancel"
            )
        }
        Spacer(modifier = modifier.weight(1f))
        Button(
            onConfirm,
            enabled = !isLoading
        ) {
            Text(
                text = "Done"
            )
        }
    }

}
