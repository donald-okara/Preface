package ke.don.feature_book_details.presentation.screens.book_details

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import ke.don.common_datasource.remote.domain.states.BookshelfBookDetailsState
import ke.don.common_datasource.remote.domain.utils.getDominantColor
import ke.don.feature_book_details.presentation.screens.book_details.components.AboutVolume
import ke.don.feature_book_details.presentation.screens.book_details.components.BookCoverPreview
import ke.don.feature_book_details.presentation.screens.book_details.components.PublishDetails
import ke.don.feature_book_details.presentation.screens.book_details.components.TitleHeader
import ke.don.shared_domain.data_models.VolumeInfoDet
import ke.don.shared_domain.states.EmptyResultState
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailsScreen(
    modifier: Modifier = Modifier,
    onNavigateToSearch: () -> Unit,
    volumeId: String,
    bookDetailsViewModel: BookDetailsViewModel = hiltViewModel(),
    onBackPressed: () -> Unit
){
    val bookUiState = bookDetailsViewModel.bookState.collectAsState()
    val loadingJoke = bookDetailsViewModel.loadingJoke
    val imageUrl = bookUiState.value.highestImageUrl

    val bookshelfList = bookUiState.value.bookshelvesState.bookshelves

    val colorPallet = bookUiState.value.colorPallet
    val dominantColor =
        getDominantColor(
            colorPallet,
            isDarkTheme = isSystemInDarkTheme()
        )

    LaunchedEffect(volumeId){
        bookDetailsViewModel.onVolumeIdPassed(volumeId)
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
            BookDetailsContent(
                modifier = modifier.align(Alignment.TopCenter),
                volumeInfo = bookUiState.value.bookDetails.volumeInfo,
                onSearchAuthor = { author->
                    bookDetailsViewModel.onSearchAuthor(author = author)
                    onNavigateToSearch()
                },
                isLoading = bookUiState.value.resultState != EmptyResultState.Success,
                imageUrl = imageUrl,
                dominantColor = dominantColor,
                isGradientVisible = bookUiState.value.resultState == EmptyResultState.Success,
                uniqueBookshelves = bookshelfList,
                onBookshelfClicked = {bookshelfId->
                    bookDetailsViewModel.onSelectBookshelf(bookshelfId)
                },
                onConfirm = bookDetailsViewModel::onPushEditedBookshelfBooks,
                uploadSuccess = bookUiState.value.pushSuccess,
                onResetSuccess = bookDetailsViewModel::resetPushSuccess
            )

            if (bookUiState.value.resultState != EmptyResultState.Success) {
                DetailsLoadingScreen(
                    modifier = modifier
                        .padding(16.dp),
                    text = if(bookUiState.value.resultState != EmptyResultState.Error()) loadingJoke else "Failed to load your book. Please try again",
                    onRetryAction = bookDetailsViewModel::refreshAction,
                    textColor = if(bookUiState.value.resultState == EmptyResultState.Loading) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onTertiaryContainer
                )


            }
        }
    }
}




@Composable
fun BookDetailsContent(
    modifier: Modifier = Modifier,
    volumeInfo: VolumeInfoDet,
    onBookshelfClicked: (Int) -> Unit,
    dominantColor : Color,
    imageUrl: String? = null,
    onResetSuccess: () -> Unit,
    uploadSuccess: Boolean,
    isLoading: Boolean = true,
    onConfirm: () -> Unit,
    isGradientVisible: Boolean,
    onSearchAuthor: (String) -> Unit,
    uniqueBookshelves: List<BookshelfBookDetailsState>
) {
    val tabs = listOf("About", "Publish details")
    val scrollState = rememberScrollState()

    val showPreview = remember{ mutableStateOf(false) }

   Box(
        modifier = modifier
            .fillMaxSize()
    ){
       val gradientBrush = Brush.verticalGradient(
           colors = listOf(
               dominantColor.copy(alpha = 1f), // Start with dominant color from below
               dominantColor.copy(alpha = 0.8f), // Start with dominant color from below
               dominantColor.copy(alpha = 0.6f), // Start with dominant color from below
               dominantColor.copy(alpha = 0.4f), // Start with dominant color from below
               dominantColor.copy(alpha = 0.2f), // Start with dominant color from below
               Color.Transparent // Transition to transparent at the top
           ),
           startY = 0f, // Start from the bottom
           endY = 1500f // Adjust this value to control the gradient's end position (height)
       )

        // Crossfade transition for the gradient
        AnimatedVisibility(
            visible = isGradientVisible,
            enter = fadeIn(animationSpec = tween(durationMillis = 800)),
            exit = fadeOut(animationSpec = tween(durationMillis = 800)),
        ) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(gradientBrush)
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .verticalScroll(scrollState)
                .padding(8.dp)
                .fillMaxSize()
        ) {
            TitleHeader(
                volumeInfo = volumeInfo,
                onSearchAuthor = onSearchAuthor,
                imageUrl = imageUrl,
                textColor = dominantColor,
                onImageClick = {
                    showPreview.value = true
                },
                isLoading = isLoading,
                uniqueBookshelves = uniqueBookshelves,
                onBookshelfClicked = onBookshelfClicked,
                onConfirm = onConfirm,
                uploadSuccess = uploadSuccess,
                onResetSuccess = onResetSuccess,
                modifier = modifier
                    .padding(8.dp)
            )

            val pagerState = rememberPagerState(initialPage = 0, pageCount = { tabs.size })
            val coroutineScope = rememberCoroutineScope()

            // Top Tab Row
            ScrollableTabRow(
                selectedTabIndex = pagerState.currentPage,
                modifier = modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth(),
                containerColor = Color.Transparent,
                contentColor = dominantColor,
                indicator = { tabPositions ->
                    SecondaryIndicator(
                        modifier = modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                        color = dominantColor
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
                modifier = modifier.fillMaxWidth()
            ) { page ->
                when (page) {
                    0 -> AboutVolume(
                        textColor = dominantColor,
                        volumeInfo = volumeInfo,
                        modifier = modifier
                            .align(Alignment.CenterHorizontally)
                    )

                    1 -> PublishDetails(
                        volumeInfo = volumeInfo,
                        modifier = modifier
                            .align(Alignment.CenterHorizontally)
                    )
                }
            }
        }

       if (showPreview.value) {
           Box(
               modifier = modifier
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
               modifier = modifier
                   .fillMaxSize()
                   .background(Color.Transparent)
                   .clickable { showPreview.value = false } // Dismiss the preview when clicked
           ) {
               BookCoverPreview(
                   highestImageUrl = imageUrl,
                   modifier = modifier
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
fun DetailsLoadingScreen(
    modifier: Modifier = Modifier,
    onRetryAction: () -> Unit,
    textColor: Color,
    text: String
) {
    Column(
        modifier = modifier
            .fillMaxHeight(),
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
            text = text, // Join authors with a comma
            color = textColor,
            style = MaterialTheme.typography.bodyLarge, // Use appropriate text style
            modifier = modifier
                .padding(bottom = 16.dp) // Space below the authors
                .clickable {
                    onRetryAction()
                }
        )

    }
}



@Composable
fun DetailsErrorScreen(
    modifier: Modifier = Modifier,
    text: String,
    onRefresh: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxHeight(),
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
            text = text, // Join authors with a comma
            style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onTertiaryContainer), // Use appropriate text style
            modifier = modifier
                .padding(bottom = 16.dp)
                .clickable {
                    onRefresh()
                }
        )

    }
}



fun search(url : String, context: Context) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    ContextCompat.startActivity(context, intent, null)
}








