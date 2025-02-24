package ke.don.feature_bookshelf.presentation.shared_components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage

@Composable
fun BooksCoverStack(
    imageUrls: List<String>,
    modifier: Modifier = Modifier
) {
    val maxVisibleBooks = 5
    val booksCount = imageUrls.size.coerceAtMost(maxVisibleBooks) // Limit to max 5 books

    // Adaptive overlap: More books -> Smaller overlap, Fewer books -> Larger overlap
    val adaptiveOverlapFraction = when (booksCount) {
        1 -> 0f
        2 -> 0.5f
        3 -> 0.35f
        4 -> 0.25f
        else -> 0.2f
    }

    // Adaptive alpha increment: Ensure top book is most visible
    val alphaStep = if (booksCount > 1) (1f - 0.4f) / (booksCount - 1) else 0f

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        imageUrls.take(maxVisibleBooks).forEachIndexed { index, imageUrl ->
            val offsetX = index * (adaptiveOverlapFraction * 100).dp // Adaptive overlap
            val alphaValue = if (booksCount == 1) 1f else 0.4f + (index * alphaStep)

            AsyncImage(
                model = imageUrl,
                contentDescription = "Book Cover",
                contentScale = ContentScale.FillHeight,
                modifier = Modifier
                    .fillMaxSize() // Fixed size to prevent unintended scaling
                    .clip(RoundedCornerShape(8.dp))
                    .offset(x = offsetX) // Overlap effect
                    .zIndex(index.toFloat()) // Ensure proper stacking
                    .alpha(alphaValue) // Adjust transparency dynamically
            )
        }
    }
}



