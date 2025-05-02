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
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import ke.don.common_datasource.remote.domain.states.BookUiState
import ke.don.common_datasource.remote.domain.utils.getDominantColor
import ke.don.feature_book_details.R
import ke.don.feature_book_details.presentation.screens.book_details.components.AboutVolume
import ke.don.feature_book_details.presentation.screens.book_details.components.BookCoverPreview
import ke.don.feature_book_details.presentation.screens.book_details.components.BookDetailsSheet
import ke.don.feature_book_details.presentation.screens.book_details.components.BookProgressTab
import ke.don.feature_book_details.presentation.screens.book_details.components.PublishDetails
import ke.don.feature_book_details.presentation.screens.book_details.components.TitleHeader
import ke.don.shared_components.components.EmptyScreen
import ke.don.shared_domain.states.ResultState
import kotlinx.coroutines.launch

@Composable
fun BookDetailsRoute(
    modifier: Modifier = Modifier,
    onNavigateToSearch: (String) -> Unit,
    volumeId: String,
    bookUiState: BookUiState,
    onBookDetailsEvent: (BookDetailsEvent) -> Unit,
) {
    LaunchedEffect(volumeId) {
        onBookDetailsEvent(BookDetailsEvent.VolumeIdPassed(volumeId))
    }

    val colorPallet = bookUiState.colorPallet
    val dominantColor =
        getDominantColor(
            colorPallet,
            isDarkTheme = isSystemInDarkTheme()
        )

    Box(
        modifier = modifier
            .fillMaxSize(),
    ) {
        when (bookUiState.resultState) {
            is ResultState.Success -> {
                if (bookUiState.bookDetails.id.isNotEmpty() && volumeId == bookUiState.volumeId) {
                    BookDetailsContent(
                        dominantColor = dominantColor,
                        onBookDetailsEvent = onBookDetailsEvent,
                        bookUiState = bookUiState,
                        onSearchAuthor = onNavigateToSearch
                    )

                    BookDetailsSheet(
                        modifier = modifier
                            .align(Alignment.Center),
                        bookUiState = bookUiState,
                        onBookDetailsEvent = onBookDetailsEvent,
                    )
                } else {
                    EmptyScreen(
                        modifier = modifier
                            .align(Alignment.Center),
                        icon = Icons.Outlined.HourglassEmpty,
                        message = stringResource(R.string.loading),
                        action = {},
                        actionText = bookUiState.loadingJoke
                    )
                }

            }

            is ResultState.Loading -> {
                EmptyScreen(
                    modifier = modifier
                        .align(Alignment.Center),
                    icon = Icons.Outlined.HourglassEmpty,
                    message = stringResource(R.string.loading),
                    action = {},
                    actionText = bookUiState.loadingJoke
                )
            }

            else -> {
                EmptyScreen(
                    modifier = modifier
                        .align(Alignment.Center),
                    icon = Icons.Outlined.Error,
                    message = stringResource(R.string.error),
                    action = { onBookDetailsEvent(BookDetailsEvent.Refresh) },
                    actionText = stringResource(R.string.something_went_wrong_please_try_again)
                )
            }
        }


    }

}




@Composable
fun BookDetailsContent(
    modifier: Modifier = Modifier,
    bookUiState: BookUiState,
    onSearchAuthor: (String) -> Unit,
    onBookDetailsEvent: (BookDetailsEvent) -> Unit,
    dominantColor : Color,
) {
    val tabs = listOf("About", "Publish details", "Read progress")
    val scrollState = rememberScrollState()

    val showPreview = remember{ mutableStateOf(false) }

   Box(
        modifier = modifier
    ){
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .verticalScroll(scrollState)
                .fillMaxSize()
        ) {
            TitleHeader(
                volumeInfo = bookUiState.bookDetails.volumeInfo,
                onSearchAuthor = onSearchAuthor,
                imageUrl = bookUiState.highestImageUrl,
                textColor = dominantColor,
                onImageClick = {
                    showPreview.value = true
                },
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
                                if (index == 2){
                                    onBookDetailsEvent(BookDetailsEvent.OnNavigateToProgressTab)
                                }
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
                        volumeInfo = bookUiState.bookDetails.volumeInfo,
                        modifier = modifier
                            .align(Alignment.CenterHorizontally)
                    )

                    1 -> PublishDetails(
                        volumeInfo = bookUiState.bookDetails.volumeInfo,
                        modifier = modifier
                            .align(Alignment.CenterHorizontally)
                    )

                    2 -> BookProgressTab(
                        progressColor = dominantColor,
                        bookUiState = bookUiState,
                        onBookDetailsEvent = onBookDetailsEvent,
                        modifier = modifier
                            .align(Alignment.CenterHorizontally)
                    )
                }
            }
        }

       BookCoverPreview(
           imageUrl = bookUiState.highestImageUrl,
           onDismiss = { showPreview.value = !showPreview.value },
           showPreview = showPreview.value
       )
    }

}

@Composable
fun BookGradientBrush(
    modifier: Modifier = Modifier,
    dominantColor: Color,
    isVisible : Boolean
){
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            dominantColor.copy(alpha = 1f),
            dominantColor.copy(alpha = 0.8f),
            dominantColor.copy(alpha = 0.6f),
            dominantColor.copy(alpha = 0.4f),
            dominantColor.copy(alpha = 0.2f),
            Color.Transparent
        ),
        startY = 0f, // Start from the bottom
        endY = 1500f // Adjust this value to control the gradient's end position (height)
    )

    // Crossfade transition for the gradient
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(durationMillis = 800)),
        exit = fadeOut(animationSpec = tween(durationMillis = 800)),
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(gradientBrush)
        )
    }
}


fun search(url : String, context: Context) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    ContextCompat.startActivity(context, intent, null)
}