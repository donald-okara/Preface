package ke.don.feature_bookshelf.presentation.screens.user_library

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ke.don.feature_bookshelf.R
import ke.don.shared_domain.data_models.SupabaseBookshelf

@Composable
fun UserLibraryScreen(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues,
    onAddBookshelf: () -> Unit,
){
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .padding(paddingValues)
    ){
        LazyVerticalGrid(
        columns = GridCells.Adaptive(150.dp),
        modifier = modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        item {
            AddBookshelfButton(
                onAddBookshelf = onAddBookshelf
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
        modifier = modifier
            .padding(8.dp)
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
    bookshelf: SupabaseBookshelf,
    onNavigateToBookItem: (Int) -> Unit
){
    Card(
        modifier = modifier
            .padding(4.dp)
            .aspectRatio(0.7f)
            .fillMaxWidth()
            .clickable {
                onNavigateToBookItem(bookshelf.id)
            },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.background(
                color = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Image(
                painter = painterResource(R.drawable.bookshelf_placeholder),
                contentDescription = "Add Bookshelf",
                modifier = modifier.padding(8.dp)

            )

            Text(
                text = bookshelf.name
            )

        }
    }

}