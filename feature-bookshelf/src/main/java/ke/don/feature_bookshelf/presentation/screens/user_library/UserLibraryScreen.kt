package ke.don.feature_bookshelf.presentation.screens.user_library

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ke.don.feature_bookshelf.R
import ke.don.shared_domain.data_models.BookshelfRef

@Composable
fun UserLibraryScreen(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues,
    userLibraryViewModel: UserLibraryViewModel = hiltViewModel(),
    onAddBookshelf: () -> Unit,
) {
    val userLibraryState by userLibraryViewModel.userLibraryState.collectAsState()

    val uniqueBookshelves =
        userLibraryState.userBookshelves.distinctBy { it.supabaseBookShelf.id } // Ensure uniqueness

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(150.dp),
            modifier = modifier
                .padding(8.dp)
                .align(Alignment.Center),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {

            item {
                Box(
                    modifier = modifier
                        .size(200.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center // Centers the AddBookshelfButton
                ) {
                    AddBookshelfButton(
                        onAddBookshelf = onAddBookshelf,
                        modifier = modifier
                    )
                }

            }
            items(
                items = uniqueBookshelves,
                key = { bookShelf -> bookShelf.supabaseBookShelf.id }) { shelfItem ->
                BookshelfItem(
                    onNavigateToBookItem = {},
                    bookshelfTitle = shelfItem.supabaseBookShelf.name,
                    bookshelfSize = "${shelfItem.books.size} + books",
                    bookshelfId = shelfItem.supabaseBookShelf.id
                )
            }

        }

    }

}

@Composable
fun AddBookshelfButton(
    modifier: Modifier = Modifier,
    onAddBookshelf: () -> Unit,
){
    Card(
        onClick = onAddBookshelf,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        modifier = modifier
            .padding(8.dp)
            .size(150.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .padding(8.dp)
                .fillMaxSize()
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.LibraryBooks,
                contentDescription = "Add Bookshelf",
                modifier = modifier.padding(8.dp)
            )

            Text(
                text = "Add Bookshelf",
                modifier = modifier.padding(8.dp)
            )
        }

    }
}

@Composable
fun BookshelfItem(
    modifier: Modifier= Modifier,
    bookshelfTitle: String = "",
    bookshelfSize: String = "",
    bookshelfId: Int = 0,
    onNavigateToBookItem: (Int) -> Unit
){
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start,
        modifier = modifier
            .size(height = 250.dp, width = 200.dp)
            .clickable {
                onNavigateToBookItem(bookshelfId)
            }
    ) {
        Card(
            modifier = modifier
                .padding(4.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.bookshelf_placeholder),
                contentDescription = "Bookshelf item",
                modifier = modifier
                    .fillMaxWidth()
                    .height(175.dp)
            )
        }

        Spacer(modifier = modifier.height(8.dp)) // Space between the image and the text


        Text(
            text = bookshelfTitle,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyLarge, // Or any style you prefer
            modifier = modifier.padding(horizontal = 8.dp) // Optional padding for text
        )

        Text(
            text = bookshelfSize,
            style = MaterialTheme.typography.bodySmall, // Or any style you prefer
            modifier = modifier.padding(horizontal = 8.dp) // Optional padding for text
        )
    }

}