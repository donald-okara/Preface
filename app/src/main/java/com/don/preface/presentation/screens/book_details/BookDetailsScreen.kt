package com.don.preface.presentation.screens.book_details

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.don.preface.R
import com.don.preface.data.model.VolumeInfoDet
import com.don.preface.domain.states.ResultState
import com.don.preface.domain.utils.color_utils.getDominantColor
import com.don.preface.presentation.screens.book_details.components.AboutVolume
import com.don.preface.presentation.screens.book_details.components.AcquireVolume
import com.don.preface.presentation.screens.book_details.components.BookCoverPreview
import com.don.preface.presentation.screens.book_details.components.PublishDetails
import com.don.preface.presentation.screens.book_details.components.TitleHeader
import com.don.preface.ui.theme.RoundedCornerShapeMedium
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailsScreen(
    modifier: Modifier = Modifier,
    onSearchAuthor: (String) -> Unit,
    bookDetailsViewModel: BookDetailsViewModel = hiltViewModel(),
    onBackPressed: () -> Unit
){
    val bookUiState = bookDetailsViewModel.bookState.collectAsState()
    val loadingJoke = bookDetailsViewModel.loadingJoke
    val imageUrl = bookUiState.value.highestImageUrl

    val colorPallet = bookUiState.value.colorPallet
    val dominantColor = getDominantColor(colorPallet)


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
                onSearchAuthor = onSearchAuthor,
                imageUrl = imageUrl,
                dominantColor = dominantColor,
                isGradientVisible = bookUiState.value.resultState == ResultState.Success
            )

            if (bookUiState.value.resultState == ResultState.Loading) {
                DetailsLoadingScreen(
                    modifier = modifier
                        .padding(16.dp),
                    text = loadingJoke
                )


            }else if (bookUiState.value.resultState == ResultState.Error()) {
                DetailsErrorScreen(
                    modifier = modifier
                        .padding(16.dp),
                    text = "We could not fetch your book at this moment. Please try again later",
                    onRefresh = {
                        bookDetailsViewModel.refreshAction()
                    }

                )
            }


        }



    }


}




@Composable
fun BookDetailsContent(
    modifier: Modifier = Modifier,
    volumeInfo: VolumeInfoDet,
    dominantColor : Color,
    imageUrl: String? = null,
    isGradientVisible: Boolean,
    onSearchAuthor: (String) -> Unit
) {
    val tabs = listOf("About", "Get book", "Publish details")
    val scrollState = rememberScrollState()

    val showPreview = remember{ mutableStateOf(false) }

   Box(
        modifier = modifier
            .fillMaxSize()
    ){
       val gradientBrush = Brush.verticalGradient(
           colors = listOf(
               dominantColor.copy(alpha = 0.4f), // Start with dominant color from below
               Color.Transparent // Transition to transparent at the top
           ),
           startY = 0f, // Start from the bottom
           endY = 800f // Adjust this value to control the gradient's end position (height)
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
                modifier = modifier
                    .padding(8.dp)
            )

            val pagerState = rememberPagerState(initialPage = 0, pageCount = { tabs.size })
            val coroutineScope = rememberCoroutineScope()

            // Top Tab Row
            ScrollableTabRow(
                selectedTabIndex = pagerState.currentPage,
                modifier = modifier
                    .fillMaxWidth(),
                containerColor = Color.Transparent,
                contentColor = dominantColor,
                indicator = { tabPositions ->
                    SecondaryIndicator(
                        modifier = modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                        //color = tertiaryContainerColor // Set your preferred color here
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
                        volumeInfo = volumeInfo,
                    )
                    1 -> AcquireVolume(
                        volumeInfo = volumeInfo,
                        modifier = modifier.fillMaxSize()
                    )
                    2 -> PublishDetails(
                        volumeInfo = volumeInfo,
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
                   highestImageUrl = imageUrl,
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
fun DetailsLoadingScreen(
    modifier: Modifier = Modifier,
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
            style = MaterialTheme.typography.bodyLarge, // Use appropriate text style
            modifier = modifier.padding(bottom = 16.dp) // Space below the authors
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






