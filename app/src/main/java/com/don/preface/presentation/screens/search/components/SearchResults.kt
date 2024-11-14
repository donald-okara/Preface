package com.don.preface.presentation.screens.search.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.don.preface.R
import com.don.preface.data.model.BookItem
import com.don.preface.ui.theme.RoundedCornerShapeMedium

@Composable
fun BooksGridScreen(
    books: List<BookItem>,
    modifier: Modifier = Modifier,
    onNavigateToBookItem: (String) -> Unit

){
    LazyVerticalGrid(
        columns = GridCells.Adaptive(150.dp),
        modifier = modifier.padding(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ){
        items(items = books, key = { book -> book.id }) { book ->
            BookItem(
                book = book,
                modifier = Modifier.padding(4.dp),
                onNavigateToBookItem = onNavigateToBookItem
            )
        }
    }
}

@Composable
fun BookItem(
    modifier: Modifier = Modifier,
    book : BookItem,
    onNavigateToBookItem: (String) -> Unit
) {
    Card(
        modifier = modifier
            .padding(4.dp)
            .aspectRatio(0.7f)
            .fillMaxWidth()
            .clickable {
                onNavigateToBookItem(book.id)
            },
        shape = RoundedCornerShapeMedium,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box {
            if (book.volumeInfo.imageLinks?.thumbnail != null) {
                AsyncImage(
                    model = ImageRequest.Builder(context = LocalContext.current)
                        .data(book.volumeInfo.imageLinks.thumbnail.replace("http://", "https://")) // Replacing "http" with "https"
                        .crossfade(true)
                        .build(),
                    contentDescription = stringResource(R.string.book_cover),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                // Show a placeholder image if thumbnail is null
                Image(
                    painter = painterResource(R.drawable.undraw_writer_q06d), // Add a placeholder drawable
                    contentDescription = stringResource(R.string.book_cover),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Black overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.Black.copy(alpha = 0.3f)) // Semi-transparent black
            )

            Text(
                text = book.volumeInfo.title, // Handle null title
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = Color.White, // White text color
                modifier = Modifier
                    .align(Alignment.BottomStart) // Align the text to the bottom start
                    .padding(8.dp)
            )
        }
    }
}