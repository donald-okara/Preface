package ke.don.feature_book_details.presentation.screens.book_details.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import ke.don.common_datasource.remote.domain.states.BookshelfBookDetailsState
import ke.don.common_datasource.remote.domain.states.ShowOptionState
import ke.don.feature_book_details.R
import ke.don.shared_domain.data_models.VolumeInfoDet

@Composable
fun TitleHeader(
    modifier: Modifier = Modifier,
    onImageClick: () -> Unit,
    volumeInfo: VolumeInfoDet,
    imageUrl: String? = null,
    onConfirm: () -> Unit,
    showBookshelves : ShowOptionState,
    isLoading: Boolean = true,
    onExpandBookshelves: () -> Unit,
    onBookshelfClicked: (Int) -> Unit,
    onSearchAuthor: (String) -> Unit,
    onResetSuccess: () -> Unit,
    uniqueBookshelves: List<BookshelfBookDetailsState>,
    textColor: Color = MaterialTheme.colorScheme.onTertiaryContainer
) {
    Column(
        modifier = modifier
            .wrapContentHeight(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BookImage(
            onImageClick = onImageClick,
            imageUrl = imageUrl,
            modifier = modifier
                .size(400.dp)
                .padding(16.dp)
        )

        // Display the book title
        Text(
            text = volumeInfo.title,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineSmall,
            modifier = modifier.padding(bottom = 4.dp) // Minor padding if needed
        )

//        if(!isLoading){
//            BookshelfDropdownMenu(
//                uniqueBookshelves = uniqueBookshelves,
//                onExpandToggle = { onExpandBookshelves() },
//                onItemClick = onBookshelfClicked,
//                showBookshelvesState = showBookshelves,
//                onConfirm = onConfirm
//            )
//        }

        // Display the book authors, if available
        volumeInfo.authors.let { authors ->
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
        if (volumeInfo.publishedDate.isNotEmpty()) {

            Text(
                text = volumeInfo.publishedDate,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = modifier.graphicsLayer(alpha = 0.8f)
            )
        }

    }
}


@Composable
fun BookImage(
    modifier: Modifier = Modifier, // Allow external modification of size
    onImageClick: () -> Unit,
    imageUrl : String? = null,
) {

    LaunchedEffect(imageUrl) {
        Log.d("Image", "imageUrl : $imageUrl")
    }
    if (imageUrl.isNullOrEmpty()) {
        Image(
            painter = painterResource(R.drawable.undraw_writer_q06d),
            contentDescription = stringResource(R.string.book_cover),
            contentScale = ContentScale.FillHeight,
            modifier = modifier
                .clip(RoundedCornerShape(16.dp))

        )
    }else{
        AsyncImage(
            model = ImageRequest.Builder(context = LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = stringResource(R.string.book_cover),
            contentScale = ContentScale.FillHeight,
            placeholder = painterResource(R.drawable.undraw_writer_q06d),
            modifier = modifier
                .clip(RoundedCornerShape(16.dp))
                .clickable {
                    onImageClick()
                }
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


