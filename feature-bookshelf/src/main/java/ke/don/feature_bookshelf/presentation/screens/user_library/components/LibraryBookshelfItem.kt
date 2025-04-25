package ke.don.feature_bookshelf.presentation.screens.user_library.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CollectionsBookmark
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ke.don.common_datasource.remote.domain.states.UserLibraryState
import ke.don.feature_bookshelf.presentation.screens.user_library.LibraryEventHandler
import ke.don.feature_bookshelf.presentation.shared_components.BookshelfOptionsSheet
import ke.don.shared_components.components.EmptyScreen
import ke.don.shared_domain.data_models.BookShelf

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BookshelfItem(
    modifier: Modifier= Modifier,
    bookshelf: BookShelf,
    onNavigateToBookshelfItem: (Int)-> Unit,
    showOptionsSheet: Boolean,
    eventHandler: (LibraryEventHandler) -> Unit,
    onNavigateToEdit: (Int) -> Unit,
) {
    val coverImages = bookshelf.books.mapNotNull { book ->
        book.highestImageUrl.takeIf { !it.isNullOrBlank() }
    }
    Card(
        modifier = modifier
            .combinedClickable(
                onClick = {
                    onNavigateToBookshelfItem(bookshelf.bookshelfRef.id)
                },
                onLongClick = { eventHandler(LibraryEventHandler.SelectBookshelf(bookshelf.bookshelfRef.id)) }
            )
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = bookshelf.bookshelfRef.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "${bookshelf.books.size} books",
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = modifier.height(16.dp))

            if (coverImages.isEmpty()){
                EmptyScreen(
                    icon = Icons.Outlined.CollectionsBookmark,
                    message = "This bookshelf is empty",
                    action = {},
                    actionText = "",
                    modifier = modifier.fillMaxWidth()
                )
            }else{
                LazyRow (
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(end = 16.dp)
                ) {
                    items(
                        coverImages.take(3)
                    ) { imageUrl ->
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = null,
                            modifier = modifier
                                .clip(RoundedCornerShape(8.dp))
                                .size(width = 100.dp, height = 150.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

        }

    }

    BookshelfOptionsSheet(
        modifier = modifier,
        onNavigateToEdit = onNavigateToEdit,
        bookCovers = coverImages,
        showOptionsSheet = showOptionsSheet,
        onToggleBottomSheet = {
            eventHandler(LibraryEventHandler.SelectBookshelf(null))
        },
        title = bookshelf.bookshelfRef.name,
        bookshelfSize = "${bookshelf.books.size} books",
        bookshelfId = bookshelf.bookshelfRef.id,
        onDeleteBookshelf = { eventHandler(LibraryEventHandler.DeleteBookshelf(bookshelf.bookshelfRef.id)) },
    )
}
