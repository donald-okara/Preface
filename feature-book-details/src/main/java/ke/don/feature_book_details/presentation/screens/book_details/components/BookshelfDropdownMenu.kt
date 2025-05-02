package ke.don.feature_book_details.presentation.screens.book_details.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.outlined.CollectionsBookmark
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ke.don.common_datasource.remote.domain.states.BookshelvesState
import ke.don.common_datasource.remote.domain.states.ShowOptionState
import ke.don.feature_book_details.R
import ke.don.shared_components.components.EmptyScreen
import ke.don.shared_components.components.SheetOptionItem
import ke.don.shared_domain.states.ResultState

@Composable
fun BookshelfDropdownMenu(
    modifier: Modifier = Modifier,
    bookshelvesState: BookshelvesState,
    showOptionState: ShowOptionState,
    onConfirm: () -> Unit,
    loadingMessage: String,
    onRefreshBookshelves: () -> Unit,
    onExpandToggle: () -> Unit,
    onItemClick: (Int) -> Unit = {},
) {
    val bookIsPresentList = bookshelvesState.bookshelves.filter { it.isBookPresent }
    val bookIsNotPresentList = bookshelvesState.bookshelves.filter { !it.isBookPresent }

    // Store the initial set of bookshelf IDs where the book is present
    var initialPresentIds by remember { mutableStateOf<Set<Int>>(emptySet()) }

    // Capture the initial state when the dropdown opens and the result is Success
    LaunchedEffect(showOptionState.showOption, bookshelvesState.resultState) {
        if (showOptionState.showOption && bookshelvesState.resultState is ResultState.Success) {
            initialPresentIds = bookshelvesState.bookshelves
                .filter { it.isBookPresent }
                .map { it.bookshelfBookDetails.id }
                .toSet()
        }
    }

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
            expanded = showOptionState.showOption,
            onDismissRequest = { onExpandToggle() },
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp)
        ){
            when(bookshelvesState.resultState){
                is ResultState.Success -> {

                    if (bookshelvesState.bookshelves.isEmpty()){
                        EmptyScreen(
                            modifier = modifier,
                            icon = Icons.Outlined.CollectionsBookmark,
                            message = "Empty library",
                            action = {},
                            actionText = "You have no bookshelves. Please add one"
                        )
                    }else {
                        // Compute current state and check for changes
                        val currentPresentIds = bookshelvesState.bookshelves
                            .filter { it.isBookPresent }
                            .map { it.bookshelfBookDetails.id }
                            .toSet()
                        val isChanged = initialPresentIds != currentPresentIds

                        Column(
                            modifier = modifier
                                .height(200.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            bookIsPresentList
                                .forEach { bookshelf ->
                                    BookshelfItem(
                                        modifier = modifier.fillMaxWidth(),
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
                            modifier = modifier.fillMaxWidth(),
                            onCancel = {
                                onExpandToggle()
                            },
                            enabled = isChanged && !showOptionState.isLoading,
                            onConfirm = {
                                onConfirm()
                            }
                        )
                    }

                }

                is ResultState.Loading -> {
                    EmptyScreen(
                        modifier = modifier,
                        icon = Icons.Outlined.HourglassEmpty,
                        message = "Loading",
                        action = {},
                        actionText = loadingMessage
                    )
                }

                else -> {
                    EmptyScreen(
                        modifier = modifier,
                        icon = Icons.Outlined.Error,
                        message = "Error",
                        action = {onRefreshBookshelves()},
                        actionText = "Something went wrong. Please try again"
                    )
                }
            }
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
        horizontalArrangement = Arrangement.Start,
        modifier = modifier
            .padding(8.dp)
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
    enabled: Boolean,
    onConfirm: () -> Unit,
){
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(8.dp)
    ) {
        OutlinedButton(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.weight(1f), // Takes half the available width
            onClick = onCancel
        ) {
            Text(text = stringResource(R.string.cancel))
        }
        Button(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.weight(1f), // Takes half the available width
            onClick = onConfirm,
            enabled = enabled
        ) {
            Text(text = stringResource(R.string.done))
        }
    }
}
