package ke.don.feature_bookshelf.presentation.shared_components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage

@Composable
fun Bookstack(
    bookCoverUrls: List<String>,
    modifier: Modifier = Modifier,
    maxBooks: Int = 5,
    size: Dp = 150.dp,
    aspectRatio: Float = 2f / 3f
) {
    val displayedBooks = bookCoverUrls.takeLast(maxBooks).reversed()
    val bookCount = displayedBooks.size.coerceAtLeast(1) // Prevent division by zero

    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = modifier
            .width(size * aspectRatio)
            .height(size)
    ) {
        displayedBooks.forEachIndexed { index, url ->
            val progress = index.toFloat() / (bookCount - 1)
            val alpha = 1f - (progress * 0.5f)
            val offsetX = (index * size.value * 0.1f).dp
            val width = size * aspectRatio * (1 - progress * 0.3f)
            val height = size * (1 - progress * 0.4f)

            AsyncImage(
                model = url,
                contentDescription = "Book cover ${index + 1}",
                modifier = Modifier
                    .offset(x = offsetX)
                    .size(width, height)
                    .alpha(alpha)
                    .zIndex(bookCount - index.toFloat()), // Ensure proper stacking order
                contentScale = ContentScale.Crop
            )
        }
    }
}
