package ke.don.feature_bookshelf.presentation.shared_components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ke.don.feature_bookshelf.R
import ke.don.shared_components.components.BookStack
import ke.don.shared_components.components.ConfirmationDialog
import ke.don.shared_components.components.DialogType
import ke.don.shared_components.components.SheetOptionItem

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun BookshelfOptionsSheet(
    modifier: Modifier = Modifier,
    bookCovers: List<String>,
    showOptionsSheet: Boolean,
    onToggleBottomSheet: () -> Unit,
    title: String,
    bookshelfSize: String,
    bookshelfId: Int,
    onNavigateToEdit: (Int) -> Unit,
    onDeleteBookshelf: (Int) -> Unit,
){
    val sheetState = rememberModalBottomSheetState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    if (showOptionsSheet) {
        ModalBottomSheet(
            onDismissRequest = {onToggleBottomSheet()},
            sheetState = sheetState
        ) {
            LazyColumn(
                modifier = modifier.padding(16.dp)
            ) {
                stickyHeader {
                    BookshelfSheetHeader(
                        modifier = modifier,
                        bookCovers = bookCovers,
                        title = title,
                        bookshelfSize = bookshelfSize
                    )
                }
                item {
                    SheetOptionItem(
                        modifier = modifier,
                        icon = Icons.Outlined.Edit,
                        title = stringResource(R.string.edit_bookshelf),
                        onOptionClick = {
                            onNavigateToEdit(bookshelfId)
                        }
                    )
                }
                item {
                    SheetOptionItem(
                        modifier = modifier,
                        icon = Icons.Outlined.Close,
                        title = stringResource(R.string.delete_bookshelf),
                        onOptionClick = {
                            showDeleteDialog = true
                        }
                    )
                }
            }
        }
    }

    if (showDeleteDialog){
        ConfirmationDialog(
            onDismissRequest = { showDeleteDialog = false },
            onConfirmation = {
                onDeleteBookshelf(bookshelfId)
                showDeleteDialog = false
            },
            dialogType = DialogType.DANGER,
            dialogTitle = stringResource(R.string.delete_bookshelf),
            dialogText = stringResource(R.string.delete_bookshelf_confirmation),
            icon = Icons.Outlined.Close
        )
    }

}


@Composable
fun BookshelfSheetHeader(
    modifier: Modifier = Modifier,
    bookCovers: List<String>,
    title: String,
    bookshelfSize: String,
){
    val imageSize = 80.dp
    val textSize = 18f
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            if (bookCovers.isNotEmpty()) {
                BookStack(
                    bookCoverUrls = bookCovers,
                    modifier = modifier,
                    size = imageSize
                )
            } else {
                Image(
                    painter = painterResource(R.drawable.bookshelf_placeholder),
                    contentDescription = stringResource(R.string.bookshelf_item),
                    modifier = modifier.size(imageSize)
                )
            }
            Spacer(modifier = modifier.width(12.dp))

            Column(
                horizontalAlignment = Alignment.Start,
                modifier = modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium.copy(fontSize = textSize.sp),
                    maxLines = 1
                )

                Text(
                    text = bookshelfSize,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = modifier.padding(top = 4.dp)
                )
            }
        }
        Spacer(modifier = modifier.height(8.dp))

        HorizontalDivider()

        Spacer(modifier = modifier.height(8.dp))

    }
}


