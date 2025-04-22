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
import ke.don.feature_bookshelf.presentation.shared_components.BookshelfOptionsSheet


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BookshelfItem(
    modifier: Modifier= Modifier,
    bookshelfTitle: String = "",
    coverImages: List<String> = emptyList(),
    bookshelfSize: String = "",
    bookshelfId: Int = 0,
    onNavigateToEdit: (Int) -> Unit,
    onDeleteBookshelf: (Int) -> Unit = {},
    onNavigateToBookshefItem: (Int) -> Unit,
    onShowBottomSheet: () -> Unit,
    showBottomSheet: Boolean,
    onDismissBottomSheet: () -> Unit
) {
    Card(
        modifier = modifier
            .combinedClickable(
                onClick = {
                    onNavigateToBookshefItem(bookshelfId)
                },
                onLongClick = { onShowBottomSheet() }
            )
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = bookshelfTitle,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = bookshelfSize,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = modifier.height(16.dp))
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
                            .size(width = 96.dp, height = 144.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }

    }

    BookshelfOptionsSheet(
        modifier = modifier,
        bookCovers = coverImages,
        title = bookshelfTitle,
        bookshelfSize = bookshelfSize,
        showBottomSheet = showBottomSheet,
        onDismissSheet = { onDismissBottomSheet() },
        bookshelfId = bookshelfId,
        onNavigateToEdit = onNavigateToEdit,
        onDeleteBookshelf = onDeleteBookshelf
    )
}
