package ke.don.feature_bookshelf.presentation.screens.bookshelf_details.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import ke.don.feature_bookshelf.R
import ke.don.shared_domain.data_models.BookShelf
import ke.don.shared_domain.data_models.SupabaseBook
import ke.don.shared_domain.utils.formatting_utils.formatHtmlToAnnotatedString


@Composable
fun BookList(
    modifier: Modifier = Modifier,
    bookShelf: BookShelf,
){
    val uniqueBooks = bookShelf.books.distinctBy { it.bookId } // Ensure uniqueness

    LazyColumn(
        modifier = modifier.padding(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ){
        item{
            BookshelfHeader(
                coverImages = bookShelf.books.mapNotNull { it.highestImageUrl?.takeIf {image->  image.isNotEmpty() } },
                bookshelfName = bookShelf.supabaseBookShelf.name,
                bookshelfDescription = bookShelf.supabaseBookShelf.description,
                bookshelfSize = "${bookShelf.books.size} books"
            )
        }
        items(items = uniqueBooks, key = { book -> book.bookId }) { book ->
            BookComponent(
                book = book,
                modifier = Modifier.padding(4.dp),
                onItemClick = {}
            )
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
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .clickable {
                onItemClick(book.bookId)
            }
    ) {

        if (book.highestImageUrl != null) {
            AsyncImage(
                model = ImageRequest.Builder(context = LocalContext.current)
                    .data(book.highestImageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = book.title,
                contentScale = ContentScale.Crop,
                modifier = modifier.size(
                    width = 100.dp,
                    height = 150.dp
                )
            )
        } else {
            // Show a placeholder image if thumbnail is null
            Image(
                painter = painterResource(R.drawable.undraw_writer_q06d), // Add a placeholder drawable
                contentDescription = book.title,
                contentScale = ContentScale.Crop,
                modifier = modifier.size(
                    width = 100.dp,
                    height = 150.dp
                )
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
                overflow = TextOverflow.Ellipsis,
                modifier = modifier
            )

            Text(
                text = formatHtmlToAnnotatedString(
                    book.description, // Handle null title
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = modifier
            )
        }

    }


}
