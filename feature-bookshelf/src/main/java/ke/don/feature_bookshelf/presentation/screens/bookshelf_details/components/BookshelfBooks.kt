package ke.don.feature_bookshelf.presentation.screens.bookshelf_details.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import ke.don.common_datasource.local.roomdb.entities.BookshelfEntity
import ke.don.common_datasource.remote.domain.states.BookshelfUiState
import ke.don.feature_bookshelf.R
import ke.don.shared_domain.data_models.BookShelf
import ke.don.shared_domain.data_models.SupabaseBook
import ke.don.shared_domain.states.ResultState
import ke.don.shared_domain.utils.formatting_utils.formatHtmlToAnnotatedString


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun BookList(
    modifier: Modifier = Modifier,
    uiState: BookshelfUiState,
    onRefresh: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    onItemClick: (String) -> Unit
){
    val uniqueBooks = uiState.bookShelf.books.distinctBy { it.bookId } // Ensure uniqueness

    PullToRefreshBox(
        isRefreshing = uiState.resultState == ResultState.Loading,
        onRefresh = {onRefresh()},
        modifier = modifier,
    ){
        LazyColumn(
            modifier = modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .padding(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            item {
                BookshelfHeader(
                    coverImages = uiState.bookShelf.books.mapNotNull { it.highestImageUrl?.takeIf { image -> image.isNotEmpty() } },
                    bookshelfName = uiState.bookShelf.name,
                    bookshelfDescription = uiState.bookShelf.description,
                    bookshelfSize = "${uiState.bookShelf.books.size} books",
                    scrollBehavior = scrollBehavior,
                    modifier = modifier
                )
            }
            items(items = uniqueBooks, key = { book -> book.bookId }) { book ->
                BookComponent(
                    book = book,
                    modifier = modifier.padding(4.dp),
                    onItemClick = onItemClick
                )
            }
        }
    }
}

@Composable
fun BookComponent(
    modifier: Modifier = Modifier,
    book: SupabaseBook,
    onItemClick: (String) -> Unit
) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .clickable {
                onItemClick(book.bookId)
            }
    ) {

        if (book.highestImageUrl.isNullOrEmpty()) {
            // Show a placeholder image if thumbnail is null
            Image(
                painter = painterResource(R.drawable.undraw_writer_q06d), // Add a placeholder drawable
                contentDescription = book.title,
                contentScale = ContentScale.Crop,
                modifier = modifier
                    .clip(RoundedCornerShape(8.dp))
                    .scale(1f)
                    .size(width = 100.dp, height = 150.dp),
            )

        } else {
            AsyncImage(
                model = ImageRequest.Builder(context = LocalContext.current)
                    .data(book.highestImageUrl)
                    .build(),
                contentDescription = book.title,
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
            Text(
                text = book.title, // Handle null title
                maxLines = 2,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge,
                overflow = TextOverflow.Ellipsis,
                modifier = modifier
            )

            Text(
                text = formatHtmlToAnnotatedString(
                    book.description, // Handle null title
                ),
                maxLines = 2,
                style = MaterialTheme.typography.bodySmall,
                overflow = TextOverflow.Ellipsis,
                modifier = modifier
            )
        }

    }


}
