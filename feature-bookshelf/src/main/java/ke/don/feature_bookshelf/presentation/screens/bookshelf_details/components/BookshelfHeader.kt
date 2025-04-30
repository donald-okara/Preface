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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ke.don.feature_bookshelf.R
import ke.don.shared_components.components.BookStack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookshelfHeader(
    modifier: Modifier = Modifier,
    coverImages: List<String> = emptyList(),
    bookshelfName: String,
    bookshelfDescription: String = "",
    bookshelfSize: String,
) {
    val animationDuration = 200
    val imageSize = 300.dp
    val imageHeight = 175.dp
    val textSize = 28f
    val alpha = 1f

    val contentModifier = modifier
        .fillMaxWidth()
        .padding(8.dp)
        .graphicsLayer {
            this.alpha = alpha
        }
        .animateContentSize(
            animationSpec = tween(
                animationDuration,
                easing = FastOutSlowInEasing
            )
        ) // Ensures smooth resizing

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = contentModifier
    ) {
        if (coverImages.isNotEmpty()) {
            BookStack(
                bookCoverUrls = coverImages,
                modifier = modifier,
                size = imageSize
            )
        } else {
            Image(
                painter = painterResource(R.drawable.bookshelf_placeholder),
                contentDescription = "Bookshelf item",
                modifier = modifier
                    .fillMaxWidth()
                    .height(imageHeight)
            )
        }

        Text(
            text = bookshelfName,
            style = MaterialTheme.typography.headlineLarge.copy(fontSize = textSize.sp),
            maxLines = 2,
            modifier = modifier.padding(top = 8.dp)
        )



        Text(
            text = bookshelfSize,
            style = MaterialTheme.typography.headlineSmall,
            modifier = modifier.padding(top = 8.dp)
        )
        if (bookshelfDescription.isNotEmpty()) {
            Text(
                text = bookshelfDescription,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                modifier = modifier.padding(top = 8.dp)
            )
        }
        Spacer(modifier = modifier.height(16.dp))

        HorizontalDivider()
    }
}




