package ke.don.shared_components.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import ke.don.shared_components.R
import ke.don.shared_domain.utils.formatting_utils.formatHtmlToAnnotatedString


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BookListItem(
    modifier: Modifier = Modifier,
    bookId: String,
    imageUrl: String?,
    title: String?,
    description: String?,
    authors: List<String>?,
    onItemClick: (String) -> Unit
) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .clickable {
                onItemClick(bookId)
            }
    ) {

        if (imageUrl.isNullOrEmpty()) {
            // Show a placeholder image if thumbnail is null
            Image(
                painter = painterResource(R.drawable.undraw_writer_q06d), // Add a placeholder drawable
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = modifier
                    .clip(RoundedCornerShape(8.dp))
                    .scale(1f)
                    .size(width = 100.dp, height = 150.dp),
            )

        } else {
            AsyncImage(
                model = ImageRequest.Builder(context = LocalContext.current)
                    .data(imageUrl)
                    .build(),
                contentDescription = title,
                placeholder = painterResource(R.drawable.undraw_writer_q06d),
                contentScale = ContentScale.Crop,
                modifier = modifier
                    .clip(RoundedCornerShape(8.dp))
                    .scale(1f)
                    .size(width = 100.dp, height = 150.dp),
            )
        }

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start,
            modifier = modifier.padding(8.dp)
        ) {
            if (title != null) {
                Text(
                    text = title, // Handle null title
                    maxLines = 2,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge,
                    overflow = TextOverflow.Ellipsis,
                    modifier = modifier
                )
            }
            authors?.let { authors ->
                if (authors.isNotEmpty()) {
                    val displayAuthors = authors.take(2)
                    val textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.primary)
                    FlowRow(
                        modifier = modifier,
                        horizontalArrangement = Arrangement.Center,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        displayAuthors.forEachIndexed { index, author ->
                            Text(
                                text = AnnotatedString(author),
                                style = textStyle,
                            )
                            if (index < displayAuthors.size - 1) {
                                Text(
                                    text = ", ",
                                    style = textStyle,
                                )
                            }
                        }
                        if (authors.size > 2) {
                            Text(
                                text = " +${authors.size - 2} more",
                                style = textStyle,
                            )
                        }
                    }
                }
            }
            if (description != null) {
                Text(
                    text = formatHtmlToAnnotatedString(
                        description, // Handle null title
                    ),
                    maxLines = 2,
                    style = MaterialTheme.typography.bodySmall,
                    overflow = TextOverflow.Ellipsis,
                    modifier = modifier
                )
            }


        }

    }


}
