package ke.don.feature_book_details.presentation.screens.search.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ke.don.shared_components.components.BookListItem
import ke.don.shared_domain.data_models.BookItem

@Composable
fun BookList(
    books: List<BookItem>,
    modifier: Modifier = Modifier,
    onNavigateToBookItem: (String) -> Unit

){
    val uniqueBooks = books.distinctBy { it.id } // Ensure uniqueness

    LazyColumn(
        modifier = modifier.padding(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ){
        items(items = uniqueBooks, key = { book -> book.id }) { book ->
            BookListItem(
                modifier = Modifier
                    .padding(4.dp),
                bookId = book.id,
                imageUrl = book.volumeInfo.imageLinks?.getHighestQualityUrl()?.replace("http", "https"),
                title = book.volumeInfo.title,
                description = book.volumeInfo.description,
                authors = book.volumeInfo.authors,
                onItemClick = {
                    onNavigateToBookItem(book.id)
                },
            )
        }
    }
}

