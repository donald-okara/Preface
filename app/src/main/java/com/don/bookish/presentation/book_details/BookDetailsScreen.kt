package com.don.bookish.presentation.book_details

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.don.bookish.R
import com.don.bookish.data.model.ImageLinks
import com.don.bookish.data.model.VolumeData
import com.don.bookish.presentation.shared_components.formatHtmlToAnnotatedString
import com.don.bookish.ui.theme.RoundedCornerShapeMedium

@Composable
fun BookDetailsScreen(
    modifier: Modifier = Modifier,
    bookId: String,
    onSearchAuthor: (String) -> Unit,
    viewModel: BookDetailsViewModel
){
    val bookState = viewModel.bookState
    LaunchedEffect(bookId) {
        viewModel.getBookDetails(bookId)
        Log.d("BookDetailsScreen", "Book details: $bookState")
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        contentAlignment = Alignment.Center,
    ) {
        when (bookState) {
            is BookState.Success -> {
                BookDetailsContent(
                    book = bookState.data,
                    onSearchAuthor = onSearchAuthor
                )
            }

            is BookState.Error -> {
                ErrorScreen(
                    text = bookState.message,
                    onRefresh = { viewModel.getBookDetails(bookId) }

                )
            }

            is BookState.Loading -> {
                LoadingScreen(
                    text = viewModel.loadingJoke
                )
            }


        }
    }


}



@Composable
fun BookDetailsContent(
    book: VolumeData,
    onSearchAuthor: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.verticalScroll(scrollState)
    ){
        TitleHeader(
            book = book,
            onSearchAuthor = onSearchAuthor
        )

        OverviewTab(
            book = book
        )
    }
}

@Composable
fun OverviewTab(
    book: VolumeData
){
    AboutVolume(
        book = book
    )

}

@Composable
fun AboutVolume(
    book: VolumeData
){
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(8.dp)
    ) {
        Text(
            text = "About this edition",
            style = MaterialTheme.typography.titleLarge,
        )

        DescriptionColumn(
            description = book.volumeInfo.description ?: ""
        )

        OutlinedCard(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(
                    fraction = 0.9f

                )
        ){
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(8.dp)
            ) {
                // Publisher Information
                book.volumeInfo.publisher?.let {
                    Text(
                        text = "Published by: $it",
                    )
                }

                // Industry Identifiers
                book.volumeInfo.industryIdentifiers?.forEach { identifier ->
                    Text(
                        text = "${identifier.type}: ${identifier.identifier}",
                    )
                }

                // Pages
                Text(
                    text = "Pages: ${book.volumeInfo.pageCount}",
                )

                // Language
                Text(
                    text = "Language: ${book.volumeInfo.language}",
                )

                // Maturity Ratings
                Text(
                    text = "Maturity Ratings: ${book.volumeInfo.maturityRating}",
                )

                // Categories
                book.volumeInfo.categories?.let { categories ->
                    val processedCategories = categories.flatMap { it.split("/") }.toSet().toList()

                    Text(text = "Categories:")
                    Text(
                        text = processedCategories.joinToString(", "),
                        modifier = Modifier.padding(start = 16.dp) // Indent the categories
                    )
                }
            }

        }

    }
}

@Composable
fun DescriptionColumn(
    description: String,
    maxLinesCollapsed: Int = 3 // You can set the number of lines to show when collapsed
) {
    // State to track if the description is expanded or collapsed
    var isExpanded by remember { mutableStateOf(false) }

    // Format the description
    val styledText = formatHtmlToAnnotatedString(description)

    // Text with conditional maxLines


    Column {
        // Display the styled text
        Text(
            text = styledText,
            maxLines = if (isExpanded) Int.MAX_VALUE else maxLinesCollapsed,
            overflow = TextOverflow.Ellipsis // Show ellipsis if text is truncated
        )

        // Show More/Less toggle
        if (!isExpanded && styledText.length > maxLinesCollapsed * 50) {
            Text(
                text = "Show More",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clickable { isExpanded = true }
                    .padding(top = 4.dp)
            )
        } else if (isExpanded) {
            Text(
                text = "Show Less",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clickable { isExpanded = false }
                    .padding(top = 4.dp)
            )
        }
    }
}



@Composable
fun TitleHeader(
    book: VolumeData,
    onSearchAuthor: (String) -> Unit // Callback to handle author clicks
) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BookImage(book = book)

        // Column for title and authors
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f), // Allow the column to take up remaining space
            verticalArrangement = Arrangement.Center
        ) {
            // Display the book title
            Text(
                text = book.volumeInfo.title,
                style = MaterialTheme.typography.headlineSmall, // Use appropriate text style
                modifier = Modifier.padding(bottom = 8.dp) // Space below the title
            )

            // Display the book authors, if available
            book.volumeInfo.authors?.let { authors ->
                Row(
                    modifier = Modifier.padding(bottom = 16.dp) // Space below authors
                ) {
                    authors.forEachIndexed { index, author ->
                        Text(
                            text = AnnotatedString(author),
                            modifier = Modifier.clickable {
                                onSearchAuthor(author)
                            },
                            style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onTertiaryContainer),
                        )
                        // Add a separator if it's not the last author
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
            book.volumeInfo.publishedDate?.let { date ->
                Text(
                    text = date,
                    style = MaterialTheme.typography.bodyMedium, // Use appropriate text style
                    modifier = Modifier
                        .padding(bottom = 16.dp) // Space below the date
                        .graphicsLayer(alpha = 0.8f) // Set alpha to 0.8
                )
            }
        }
    }
}


@Composable
fun BookImage(
    book: VolumeData
){
    // Set a constant size for the image
    val imageSize = Modifier
        .size(width = 120.dp, height = 160.dp) // Set a non-square size
        .clip(RoundedCornerShapeMedium) // Add rounded corners

    // Display the book cover image
    if (book.volumeInfo.imageLinks != null) {
        val highestImageUrl = getHighestAvailableImageUrl(book.volumeInfo.imageLinks)

        AsyncImage(
            model = ImageRequest.Builder(context = LocalContext.current)
                .data(highestImageUrl?.replace("http://", "https://")) // Replacing "http" with "https"
                .crossfade(true)
                .build(),
            contentDescription = stringResource(R.string.book_cover),
            placeholder = painterResource(R.drawable.undraw_sharing_knowledge_svg),
            contentScale = ContentScale.FillBounds, // Scale to fill the size while maintaining aspect ratio
            modifier = imageSize.padding(end = 16.dp) // Space between the image and the text
        )
    } else {
        Image(
            painter = painterResource(R.drawable.undraw_sharing_knowledge_svg),
            contentDescription = "Loading",
            contentScale = ContentScale.FillBounds, // Scale to fill the size while maintaining aspect ratio
            modifier = imageSize.padding(end = 16.dp) // Space between the image and the text
        )
    }
}



@Composable
fun LoadingScreen(
    modifier: Modifier = Modifier,
    text: String
) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Set a constant size for the loading image
        val imageSize = Modifier
            .size(width = 120.dp, height = 180.dp) // Set a non-square size
            .clip(RoundedCornerShapeMedium) // Add rounded corners

        Image(
            painter = painterResource(R.drawable.undraw_sharing_knowledge_svg),
            contentDescription = "Loading",
            contentScale = ContentScale.FillBounds, // Scale to fill the size while maintaining aspect ratio
            modifier = imageSize.padding(end = 16.dp) // Space between the image and the text
        )

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f), // Allow the column to take up remaining space
            verticalArrangement = Arrangement.Center
        ) {
            // Display the book title
            Text(
                text = "Loading",
                style = MaterialTheme.typography.headlineSmall, // Use appropriate text style
                modifier = Modifier.padding(bottom = 8.dp) // Space below the title
            )

            // Display the book authors, if available
            Text(
                text =text, // Join authors with a comma
                style = MaterialTheme.typography.bodyLarge, // Use appropriate text style
                modifier = Modifier.padding(bottom = 16.dp) // Space below the authors
            )

        }
    }
}

@Composable
fun ErrorScreen(
    modifier: Modifier = Modifier,
    text: String,
    onRefresh: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Set a constant size for the loading image
        val imageSize = Modifier
            .size(width = 120.dp, height = 160.dp) // Set a non-square size
            .clip(RoundedCornerShapeMedium) // Add rounded corners

        Image(
            painter = painterResource(R.drawable.undraw_sharing_knowledge_svg),
            contentDescription = "Error",
            contentScale = ContentScale.FillBounds, // Scale to fill the size while maintaining aspect ratio
            modifier = imageSize.padding(end = 16.dp) // Space between the image and the text
        )

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f), // Allow the column to take up remaining space
            verticalArrangement = Arrangement.Center
        ) {
            // Display the book title
            Text(
                text = "Error",
                style = MaterialTheme.typography.headlineSmall, // Use appropriate text style
                modifier = Modifier.padding(bottom = 8.dp) // Space below the title
            )

            // Display the book authors, if available
            Text(
                text =text, // Join authors with a comma
                style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onTertiaryContainer), // Use appropriate text style
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .clickable {
                        onRefresh()
                    }
            )

        }
    }
}

fun getHighestAvailableImageUrl(imageLinks: ImageLinks?): String? {
    return imageLinks?.large
        ?: imageLinks?.medium
        ?: imageLinks?.small
        ?: imageLinks?.thumbnail
        ?: imageLinks?.smallThumbnail
}


@Preview(showBackground = true)
@Composable
fun PreviewDescriptionColumn() {
    DescriptionColumn(description = "<b>This is bold text</b>, <i>this is italic text</i>, and <u>this is underlined</u>.")
}

