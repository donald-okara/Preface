package ke.don.shared_components.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage

@Composable
fun BookStack(
    bookCoverUrls: List<String>,
    modifier: Modifier = Modifier,
    maxBooks: Int = 2,
    size: Dp = 150.dp,
    aspectRatio: Float = 2f / 3f
) {
    val displayedBooks = bookCoverUrls.takeLast(maxBooks).reversed()
    val bookCount = displayedBooks.size
    if (bookCount == 0) return // Handle empty list

    val offsetStep = 25.dp
    val boxWidth = size * aspectRatio + (bookCount - 1) * offsetStep

    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = modifier
            .width(boxWidth)
            .height(size)
    ) {
        displayedBooks.forEachIndexed { index, url ->
            val progress = if (bookCount > 1) index.toFloat() / (bookCount - 1) else 0f
            val alpha = 1f - (progress * 0.5f)
            val offsetX = ((bookCount - 1 - index) * offsetStep)
            val width = size * aspectRatio * (1 - progress * 0.3f)
            val height = size * (1 - progress * 0.4f)

            AsyncImage(
                model = url,
                contentDescription = "Book cover ${index + 1}",
                modifier = Modifier
                    .offset(x = offsetX)
                    .size(width, height)
                    .clip(RoundedCornerShape(8.dp))
                    .alpha(alpha)
                    .zIndex(bookCount - index.toFloat()),
                contentScale = ContentScale.Crop
            )
        }
    }
}
