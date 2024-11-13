package com.don.preface.presentation.screens.book_details

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.don.preface.R
import com.don.preface.presentation.utils.contracts.ImageUrlFetcherContract
import com.don.preface.data.model.BookDetailsResponse
import com.don.preface.presentation.utils.color_utils.model.ColorPallet
import com.don.preface.presentation.utils.color_utils.getTertiaryContainerColor
import com.don.preface.presentation.utils.color_utils.getTertiaryContentColor
import com.don.preface.presentation.utils.color_utils.downloadImage
import com.don.preface.presentation.utils.color_utils.extractColorPalette
import com.don.preface.presentation.utils.color_utils.extractPaletteFromImage
import com.don.preface.presentation.utils.formatting_utils.formatHtmlToAnnotatedString
import com.don.preface.ui.theme.BookishTheme
import com.don.preface.ui.theme.RoundedCornerShapeLarge
import com.don.preface.ui.theme.RoundedCornerShapeMedium
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailsScreen(
    modifier: Modifier = Modifier,
    bookId: String,
    onSearchAuthor: (String) -> Unit,
    bookState: StateFlow<BookState>, // Accept StateFlow here
    bookDetailsViewModel: BookDetailsViewModel,
    onBackPressed: () -> Unit
){
    val currentBookState by bookState.collectAsState()

    LaunchedEffect(key1 = bookId) {
        bookDetailsViewModel.getBookDetails(bookId)
    }

    BackHandler {
        bookDetailsViewModel.clearState()  // Add a function in the ViewModel to clear data
        onBackPressed()
    }


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        ""
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onBackPressed()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Back"

                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent, // Transparent background
                    )
            )
        }
    ) {
        Box(
            modifier = modifier
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter,
        ) {
            when (currentBookState) {
                is BookState.Success -> {

                    BookDetailsContent(
                        modifier = modifier.align(Alignment.TopCenter),
                        book = (currentBookState as BookState.Success).data,
                        onSearchAuthor = onSearchAuthor,
                    )
                }

                is BookState.Error -> {
                    DetailsErrorScreen(
                        text = (currentBookState as BookState.Error).message,
                        onRefresh = { bookDetailsViewModel.getBookDetails(bookId) }

                    )
                }

                is BookState.Loading -> {
                    DetailsLoadingScreen(
                        text = bookDetailsViewModel.loadingJoke
                    )
                }

                is BookState.Empty -> {

                }
                is BookState.FallbackError -> {
                    onBackPressed()
                }

            }
        }


    }


}




@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BookDetailsContent(
    modifier: Modifier = Modifier,
    book: BookDetailsResponse,
    lowestImageUrlFetcher: ImageUrlFetcherContract = lowestAvailableImageUrlFetcher, // Default to highest
    highestImageUrlFetcher: ImageUrlFetcherContract = highestAvailableImageUrlFetcher, // Default to highest
    onSearchAuthor: (String) -> Unit
) {
    val showPreview = remember{ mutableStateOf(false) }
    //val bookState = viewModel.bookState
    val colorPalette = remember { mutableStateOf(ColorPallet()) } // Initialize ColorPalette
    var contentColorErrorState by remember { mutableStateOf<String?>(null) }
    var contentContainerErrorState by remember { mutableStateOf<String?>(null) }
    var lowestUrl = lowestImageUrlFetcher.fetchImageUrl(book.volumeInfo.imageLinks)
    lowestUrl = lowestUrl?.replace("http://", "https://")


    var dominantColor by remember { mutableStateOf(Color.Transparent) }
    var isGradientVisible by remember { mutableStateOf(false) }


    val tabs = listOf("About", "Get book", "Publish details")

    var highestImageUrl = ""
    if (book.volumeInfo.imageLinks != null) {
        highestImageUrl = highestImageUrlFetcher.fetchImageUrl(book.volumeInfo.imageLinks).toString()
        highestImageUrl = highestImageUrl.replace("http://", "https://")

    }


    LaunchedEffect(lowestUrl) {
        try {
            colorPalette.value = extractColorPalette(lowestUrl)
            contentColorErrorState = null // Reset error state on success

            dominantColor = colorPalette.value.dominantColor
            isGradientVisible = true // Trigger the gradient visibility after color is loaded


        } catch (e: Exception) {
            contentColorErrorState = e.message // Handle the error
            contentContainerErrorState = e.message // Handle the error
        }
    }
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            dominantColor.copy(alpha = 0.4f), // Start with dominant color from below
            Color.Transparent // Transition to transparent at the top
        ),
        startY = 0f, // Start from the bottom
        endY = 800f // Adjust this value to control the gradient's end position (height)
    )


    val tertiaryContentColor = getTertiaryContentColor(colorPalette.value, contentContainerErrorState)

    val tertiaryContainerColor = getTertiaryContainerColor(colorPalette.value, contentContainerErrorState)

    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
    ){
        // Crossfade transition for the gradient
        AnimatedVisibility(
            visible = isGradientVisible,
            enter = fadeIn(animationSpec = tween(durationMillis = 800)),
            exit = fadeOut(animationSpec = tween(durationMillis = 800)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(gradientBrush)
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(8.dp)
                .fillMaxSize()
        ) {
            TitleHeader(
                book = book,
                onSearchAuthor = onSearchAuthor,
                textColor = tertiaryContentColor,
                onImageClick = {
                    showPreview.value = true
                },
                modifier = Modifier
                    .padding(8.dp)
            )

            val pagerState = rememberPagerState(initialPage = 0, pageCount = { tabs.size })
            val coroutineScope = rememberCoroutineScope()

            // Top Tab Row
            ScrollableTabRow(
                selectedTabIndex = pagerState.currentPage,
                modifier = Modifier
                    .fillMaxWidth(),
                containerColor = Color.Transparent,
                contentColor = tertiaryContentColor,
                indicator = { tabPositions ->
                    SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                        color = tertiaryContainerColor // Set your preferred color here
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.scrollToPage(index)
                            }
                        },
                        text = { Text(text = title) }
                    )
                }
            }

            // Swipe-able pager content for each tab
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth()
            ) { page ->
                when (page) {
                    0 -> AboutVolume(
                        book = book,
                        textColor = tertiaryContentColor
                    )
                    1 -> AcquireVolume(
                        book = book,
                        modifier = Modifier.fillMaxSize()
                    )
                    2 -> PublishDetails(
                        book = book,
                    )
                }
            }
        }


        if (showPreview.value) {
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
                    .clickable { showPreview.value = false } // Dismiss the preview when clicked
            ) {
                BookCoverPreview(
                    highestImageUrl = highestImageUrl,
                    modifier = Modifier
                        .fillMaxSize()
                        .scale(1f)
                        .graphicsLayer {
                            this.alpha = 1f // Keep the preview visible
                        }
                )
            }
        }


    }

}

@Composable
fun AcquireVolume(
    modifier: Modifier = Modifier,
    book: BookDetailsResponse,
){
    val context = LocalContext.current
    val volumeName = book.volumeInfo.title
    val amazonSearchUrl = "https://www.amazon.com/s?k=$volumeName&i=stripbooks"
    val somanamiUrl = "https://www.somanami.co.ke/search-results?q=$volumeName"
    val prestigeUrl = "https://prestigebookshop.com/?s=$volumeName&post_type=product"

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .padding(8.dp)
            .fillMaxSize()
    ) {

        AcquireBookItem(
            image = R.drawable.somanamilogo,
            contentDescription = "Soma nami",
            text = "Search for \"${book.volumeInfo.title}\" in Soma nami",
            onClick = {
                search(
                    url = somanamiUrl,
                    context = context
                )
            }
        )
        AcquireBookItem(
            image = R.drawable.prestigelogo,
            contentDescription = "Prestige bookstore",
            text = "Search for \"${book.volumeInfo.title}\" in Prestige",
            onClick = {
                search(
                    url = prestigeUrl,
                    context = context
                )
            }
        )
        AcquireBookItem(
            image = R.drawable.amazon_buy_logo,
            contentDescription = "Amazon",
            text = "Search for \"${book.volumeInfo.title}\" in Amazon",
            onClick = {
                search(
                    url = amazonSearchUrl,
                    context = context
                )
            }
        )
    }

}

@Composable
fun AcquireBookItem(
    modifier: Modifier = Modifier,
    image : Int,
    contentDescription : String,
    onClick : () -> Unit = {},
    text : String,
){
    ListItem(
        headlineContent = {
            Text(
                text = contentDescription,
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                modifier = modifier.padding(8.dp)
            )
        },
        supportingContent = {
            Text(
                text = text,
                textAlign = TextAlign.Start,
                maxLines = 2,
            )
        },
        leadingContent = {
            Image(
                painter = painterResource(id = image),
                contentDescription = contentDescription,
                contentScale = ContentScale.Crop,
                modifier = modifier
                    .size(40.dp)
                    .clip(RoundedCornerShapeLarge)
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    )
}

@Composable
fun PublishDetails(
    book: BookDetailsResponse,
){
    OutlinedCard(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(
                fraction = 0.9f
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ){
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(8.dp)
        ) {
            // Publisher Information
            book.volumeInfo.publisher.let {
                Text(
                    text = "Published by: $it",
                )
            }

            // Industry Identifiers
            book.volumeInfo.industryIdentifiers.forEach { identifier ->
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
            book.volumeInfo.categories.let { categories ->
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

@Composable
fun AboutVolume(
    book: BookDetailsResponse,
    textColor: Color = MaterialTheme.colorScheme.onTertiaryContainer
){
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .padding(8.dp)
    ) {
        DescriptionColumn(
            description = book.volumeInfo.description,
            textColor = textColor,
        )
    }
}

@Composable
fun DescriptionColumn(
    description: String,
    textColor: Color = MaterialTheme.colorScheme.onTertiaryContainer,
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
                color = textColor,
                style = TextStyle(fontWeight = FontWeight.Bold),
                modifier = Modifier
                    .clickable { isExpanded = true }
                    .padding(top = 4.dp)
            )
        } else if (isExpanded) {
            Text(
                text = "Show Less",
                color = textColor,
                style = TextStyle(fontWeight = FontWeight.Bold),
                modifier = Modifier
                    .clickable { isExpanded = false }
                    .padding(top = 4.dp)
            )
        }
    }
}


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
fun DetailsLoadingScreen(
    modifier: Modifier = Modifier,
    text: String
) {
    Row(
        modifier = modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Set a constant size for the loading image
        val imageSize = modifier
            .size(width = 120.dp, height = 180.dp) // Set a non-square size
            .clip(RoundedCornerShapeMedium) // Add rounded corners

        Image(
            painter = painterResource(R.drawable.undraw_writer_q06d),
            contentDescription = "Loading",
            contentScale = ContentScale.FillBounds, // Scale to fill the size while maintaining aspect ratio
            modifier = imageSize.padding(end = 16.dp) // Space between the image and the text
        )

        Column(
            modifier = modifier
                .fillMaxHeight()
                .weight(1f), // Allow the column to take up remaining space
            verticalArrangement = Arrangement.Center
        ) {
            // Display the book title
            Text(
                text = "Loading",
                style = MaterialTheme.typography.headlineSmall, // Use appropriate text style
                modifier = modifier.padding(bottom = 8.dp) // Space below the title
            )

            // Display the book authors, if available
            Text(
                text =text, // Join authors with a comma
                style = MaterialTheme.typography.bodyLarge, // Use appropriate text style
                modifier = modifier.padding(bottom = 16.dp) // Space below the authors
            )

        }
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

@Composable
fun DetailsErrorScreen(
    modifier: Modifier = Modifier,
    text: String,
    onRefresh: () -> Unit
) {
    Row(
        modifier = modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Set a constant size for the loading image
        val imageSize = Modifier
            .size(width = 200.dp, height = 240.dp) // Set a non-square size
            .clip(RoundedCornerShapeMedium) // Add rounded corners
            .padding(16.dp) // Add padding around the image


        Image(
            painter = painterResource(R.drawable.undraw_writer_q06d),
            contentDescription = "Error",
            contentScale = ContentScale.FillBounds, // Scale to fill the size while maintaining aspect ratio
            modifier = imageSize.padding(end = 16.dp) // Space between the image and the text
        )

        Column(
            modifier = modifier
                .fillMaxHeight()
                .weight(1f), // Allow the column to take up remaining space
            verticalArrangement = Arrangement.Center
        ) {
            // Display the book title
            Text(
                text = "Error",
                style = MaterialTheme.typography.headlineSmall, // Use appropriate text style
                modifier = modifier.padding(bottom = 8.dp) // Space below the title
            )

            // Display the book authors, if available
            Text(
                text =text, // Join authors with a comma
                style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onTertiaryContainer), // Use appropriate text style
                modifier = modifier
                    .padding(bottom = 16.dp)
                    .clickable {
                        onRefresh()
                    }
            )

        }
    }
}


fun search(url : String, context: Context) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    ContextCompat.startActivity(context, intent, null)
}

val highestAvailableImageUrlFetcher = ImageUrlFetcherContract { imageLinks ->
    imageLinks?.extraLarge.takeIf { !it.isNullOrEmpty() }
        ?: imageLinks?.large.takeIf { !it.isNullOrEmpty() }
        ?: imageLinks?.medium.takeIf { !it.isNullOrEmpty() }
        ?: imageLinks?.small.takeIf { !it.isNullOrEmpty() }
        ?: imageLinks?.thumbnail.takeIf { !it.isNullOrEmpty() }
        ?: imageLinks?.smallThumbnail.takeIf { !it.isNullOrEmpty() }
}

val lowestAvailableImageUrlFetcher = ImageUrlFetcherContract { imageLinks ->
    imageLinks?.smallThumbnail.takeIf { !it.isNullOrEmpty() }
        ?: imageLinks?.thumbnail.takeIf { !it.isNullOrEmpty() }
        ?: imageLinks?.medium.takeIf { !it.isNullOrEmpty() }
        ?: imageLinks?.small.takeIf { !it.isNullOrEmpty() }
        ?: imageLinks?.large.takeIf { !it.isNullOrEmpty() }
}


@Preview
@Composable
fun AcquirablePreview(){
    BookishTheme{
        Column {
            AcquireBookItem(
                image = R.drawable.undraw_empty_cart_co35,
                contentDescription = "Buy book",
                text = "https://pixabay.com/images/search/icon%20pdf/",

            )
            AcquireBookItem(
                image = R.drawable.amazon,
                contentDescription = "Buy book",
                text = "https://pixabay.com/images/search/icon%20pdf/",

            )
            AcquireBookItem(
                image = R.drawable.somanamilogo,
                contentDescription = "Buy book",
                text = "https://pixabay.com/images/search/icon%20pdf/",

            )
            AcquireBookItem(
                image = R.drawable.prestigelogo,
                contentDescription = "Buy book",
                text = "https://pixabay.com/images/search/icon%20pdf/",

            )
            AcquireBookItem(
                image = R.drawable.amazon_buy_logo,
                contentDescription = "Buy book",
                text = "https://pixabay.com/images/search/icon%20pdf/",

            )

        }

    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDescriptionColumn() {
    DescriptionColumn(description = "<b>This is bold text</b>, <i>this is italic text</i>, and <u>this is underlined</u>.")
}


