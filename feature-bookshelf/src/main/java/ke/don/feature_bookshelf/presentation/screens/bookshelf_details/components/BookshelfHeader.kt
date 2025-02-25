package ke.don.feature_bookshelf.presentation.screens.bookshelf_details.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ke.don.feature_bookshelf.R
import ke.don.feature_bookshelf.presentation.shared_components.BooksCoverStack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookshelfHeader(
    modifier: Modifier = Modifier,
    coverImages: List<String> = emptyList(),
    bookshelfName: String,
    bookshelfDescription: String,
    bookshelfSize: String,
    scrollBehavior: TopAppBarScrollBehavior
) {
    val collapsedFraction = scrollBehavior.state.collapsedFraction
    val isCollapsed = collapsedFraction > 0.3f  // Switch layout at 30% scroll
    val animationDuration= 200

    LookaheadScope {
        val imageSize by animateDpAsState(
            targetValue = if (isCollapsed) 80.dp else 300.dp,
            animationSpec = tween(durationMillis = animationDuration, easing = FastOutSlowInEasing),
            label = "Image Size"
        )
        val imageHeight by animateDpAsState(
            targetValue = if (isCollapsed) 80.dp else 175.dp,
            animationSpec = tween(durationMillis = animationDuration, easing = FastOutSlowInEasing),
            label = "Image Height"
        )
        val textSize by animateFloatAsState(
            targetValue = if (isCollapsed) 18f else 28f,
            animationSpec = tween(durationMillis = animationDuration, easing = FastOutSlowInEasing),
            label = "Text Size"
        )
        val alpha by animateFloatAsState(
            targetValue = if (isCollapsed) 0.9f else 1f,
            animationSpec = tween(durationMillis = animationDuration, easing = FastOutSlowInEasing),
            label = "Alpha"
        )

        val contentModifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .graphicsLayer {
                this.alpha = alpha
            }
            .animateContentSize(animationSpec = tween(animationDuration, easing = FastOutSlowInEasing)) // Ensures smooth resizing

        if (isCollapsed) {
            Row(
                modifier = contentModifier,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
                    modifier = modifier
                        .padding(4.dp)
                        .size(imageSize)
                ) {
                    if (coverImages.isNotEmpty()) {
                        BooksCoverStack(imageUrls = coverImages, modifier = modifier.fillMaxSize())
                    } else {
                        Image(
                            painter = painterResource(R.drawable.bookshelf_placeholder),
                            contentDescription = "Bookshelf item",
                            modifier = modifier.fillMaxWidth().height(imageHeight)
                        )
                    }
                }

                Spacer(modifier = modifier.width(12.dp))

                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = modifier.weight(1f)
                ) {
                    Text(
                        text = bookshelfName,
                        style = MaterialTheme.typography.headlineMedium.copy(fontSize = textSize.sp),
                        maxLines = 1
                    )

                    Text(
                        text = bookshelfSize,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = modifier.padding(top = 4.dp)
                    )
                }
            }
        } else {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = contentModifier
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
                    modifier = modifier
                        .padding(4.dp)
                        .size(imageSize)
                ) {
                    if (coverImages.isNotEmpty()) {
                        BooksCoverStack(imageUrls = coverImages, modifier = modifier.fillMaxSize())
                    } else {
                        Image(
                            painter = painterResource(R.drawable.bookshelf_placeholder),
                            contentDescription = "Bookshelf item",
                            modifier = modifier.fillMaxWidth().height(imageHeight)
                        )
                    }
                }

                Text(
                    text = bookshelfName,
                    style = MaterialTheme.typography.headlineLarge.copy(fontSize = textSize.sp),
                    maxLines = 2,
                    modifier = modifier.padding(top = 8.dp)
                )

                if (bookshelfDescription.isNotEmpty()) {
                    Text(
                        text = bookshelfDescription,
                        maxLines = 2,
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = modifier.padding(top = 8.dp)
                    )
                }

                Text(
                    text = bookshelfSize,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = modifier.padding(top = 8.dp)
                )
            }
        }
    }
}



