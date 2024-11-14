package com.don.preface.presentation.screens.book_details.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.don.preface.R
import com.don.preface.data.model.BookDetailsResponse
import com.don.preface.presentation.screens.book_details.highestAvailableImageUrlFetcher
import com.don.preface.presentation.utils.color_utils.downloadImage
import com.don.preface.presentation.utils.color_utils.extractPaletteFromImage
import com.don.preface.presentation.utils.contracts.ImageUrlFetcherContract
import com.don.preface.ui.theme.RoundedCornerShapeMedium
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun TitleHeader(
    modifier: Modifier = Modifier,
    onImageClick: () -> Unit,
    book: BookDetailsResponse,
    onSearchAuthor: (String) -> Unit, // Callback to handle author clicks
    textColor: Color = MaterialTheme.colorScheme.onTertiaryContainer
) {
    Column(
        modifier = modifier
            .wrapContentHeight(), // Take only the space needed by the content
        verticalArrangement = Arrangement.spacedBy(8.dp), // Space between elements
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BookImage(
            book = book,
            onImageClick = onImageClick,
            modifier = Modifier
                .size(400.dp)
                .padding(16.dp)
        )

        // Display the book title
        Text(
            text = book.volumeInfo.title,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 4.dp) // Minor padding if needed
        )

        // Display the book authors, if available
        book.volumeInfo.authors.let { authors ->
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                authors.forEachIndexed { index, author ->
                    Text(
                        text = AnnotatedString(author),
                        modifier = Modifier.clickable {
                            onSearchAuthor(author)
                        },
                        style = MaterialTheme.typography.bodyLarge.copy(color = textColor),
                    )
                    if (index < authors.size - 1) {
                        Text(
                            text = ", ",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }

        // Display the book published date, if available
        book.volumeInfo.publishedDate.let { date ->
            Text(
                text = date,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.graphicsLayer(alpha = 0.8f)
            )
        }
    }
}


@Composable
fun BookImage(
    modifier: Modifier = Modifier, // Allow external modification of size
    onImageClick: () -> Unit,
    book: BookDetailsResponse,
    imageUrlFetcher: ImageUrlFetcherContract = highestAvailableImageUrlFetcher, // Default to highest
) {
    // Display the book cover image
    if (book.volumeInfo.imageLinks != null) {
        var highestImageUrl = imageUrlFetcher.fetchImageUrl(book.volumeInfo.imageLinks)
        highestImageUrl = highestImageUrl?.replace("http://", "https://")

        var dominantColor by remember { mutableStateOf(Color.Transparent) }

        LaunchedEffect(highestImageUrl) {
            withContext(Dispatchers.IO) {
                val inputStream = highestImageUrl?.let { downloadImage(it) }
                inputStream?.let {
                    val palette = extractPaletteFromImage(it)
                    dominantColor = Color(palette.getDominantColor(Color.Transparent.value.toInt()))

                    Log.d("BookImage", "Dominant color: $dominantColor")
                }
            }
        }

        AsyncImage(
            model = ImageRequest.Builder(context = LocalContext.current)
                .data(highestImageUrl)
                .crossfade(true)
                .build(),
            contentDescription = stringResource(R.string.book_cover),
            contentScale = ContentScale.FillHeight,
            placeholder = painterResource(R.drawable.undraw_writer_q06d),
            modifier = modifier
                .clip(RoundedCornerShapeMedium)
                .clickable {
                    onImageClick()
                }
        )
    } else {
        Image(
            painter = painterResource(R.drawable.undraw_writer_q06d),
            contentDescription = "Loading",
            modifier = Modifier.fillMaxSize() // Fill the container
        )

    }
}

@Composable
fun BookCoverPreview(
    modifier: Modifier = Modifier,
    highestImageUrl: String?
) {
    AsyncImage(
        model = ImageRequest.Builder(context = LocalContext.current)
            .data(highestImageUrl)
            .crossfade(true)
            .build(),
        contentScale = ContentScale.Fit,
        contentDescription = stringResource(R.string.book_cover),
        placeholder = painterResource(R.drawable.undraw_writer_q06d),
        modifier = modifier

    )
}


