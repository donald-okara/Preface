package ke.don.feature_book_details.presentation.screens.book_details.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import ke.don.feature_book_details.R
import ke.don.shared_domain.data_models.Dimensions
import ke.don.shared_domain.data_models.VolumeInfoDet

@Composable
fun TitleHeader(
    modifier: Modifier = Modifier,
    volumeInfo: VolumeInfoDet,
    onImageClick: () -> Unit,
    imageUrl: String?,
    onSearchAuthor: (String) -> Unit,
    textColor: Color = MaterialTheme.colorScheme.primary
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BookImage(
            onImageClick = onImageClick,
            imageUrl = imageUrl,
            modifier = modifier
                .padding(bottom = 8.dp)
                .size(volumeInfo.dimensions.toDpSize())
        )
        Text(
            text = volumeInfo.title,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )

        volumeInfo.authors.let { authors ->
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                authors.forEachIndexed { index, author ->
                    Text(
                        text = AnnotatedString(author),
                        modifier = modifier.clickable {
                            onSearchAuthor(author)
                        },
                        style = MaterialTheme.typography.bodyMedium.copy(color = textColor),
                    )
                    if (index < authors.size - 1) {
                        Text(
                            text = ", ",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        // Display the book published date, if available
        if (volumeInfo.publishedDate.isNotEmpty()) {
            Text(
                text = volumeInfo.publishedDate,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

    }
}

@Composable
fun BookImage(
    modifier: Modifier = Modifier,
    onImageClick: () -> Unit,
    imageUrl: String? = null,
) {
    val shape = RoundedCornerShape(8.dp)
    val context = LocalContext.current

    val imageModifier = modifier
        .clip(shape)
        .clickable { onImageClick() }


    if (imageUrl.isNullOrEmpty()) {
        Image(
            painter = painterResource(R.drawable.undraw_writer_q06d),
            contentDescription = stringResource(R.string.book_cover),
            contentScale = ContentScale.Fit,  // Fills container, cropping excess
            modifier = imageModifier
        )
    } else {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = stringResource(R.string.book_cover),
            contentScale = ContentScale.FillWidth,  // Fills container, cropping excess
            placeholder = painterResource(R.drawable.undraw_writer_q06d),
            modifier = imageModifier
        )
    }
}



@Composable
fun BookCoverPreview(
    imageUrl: String?,
    onDismiss: () -> Unit,
    showPreview: Boolean
) {
    if (showPreview) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    alpha = 0.5f // Adjust transparency to enhance blur effect
                    shadowElevation = 8.dp.toPx() // Simulates depth for blur effect
                    shape = RoundedCornerShape(0) // Apply shape for additional control
                    clip = true // Required for proper blur clipping
                }
                .background(Color.Black.copy(alpha = 0.6f)) // Semi-transparent overlay
        )

        // Fullscreen Book Cover Preview
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
                .clickable { onDismiss() } // Dismiss the preview when clicked
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context = LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentScale = ContentScale.FillWidth,
                contentDescription = stringResource(R.string.book_cover),
                placeholder = painterResource(R.drawable.undraw_writer_q06d),
                modifier = Modifier.fillMaxSize()

            )
        }
    }


}


@Composable
fun Dimensions.toDpSize(fallbackAspectRatio: Float = 2f / 3f): DpSize {
    val aspectRatio = calculateAspectRatio(fallbackAspectRatio)
    val widthDp = 200.dp
    val heightDp = widthDp / aspectRatio
    return DpSize(widthDp, heightDp)
}