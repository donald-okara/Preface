package com.don.bookish.presentation.search

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.don.bookish.R
import com.don.bookish.data.model.BookItem
import com.don.bookish.ui.theme.RoundedCornerShapeCircle
import com.don.bookish.ui.theme.RoundedCornerShapeExtraExtraLarge
import com.don.bookish.ui.theme.RoundedCornerShapeExtraLarge
import com.don.bookish.ui.theme.RoundedCornerShapeLarge
import com.don.bookish.ui.theme.RoundedCornerShapeMedium
import com.don.bookish.ui.theme.RoundedCornerShapeSmall
import dagger.Provides

@Composable
fun BookSearchScreen(
    modifier: Modifier = Modifier,
    searchState: SearchState,
    viewModel: SearchViewModel
) {

    LaunchedEffect(searchState) {
        Log.d("SearchState", "searchState: $searchState")
    }

    Column(
        modifier = modifier
            .padding(8.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BookSearchBar(
            bookSearch = viewModel.searchQuery,
            onBookSearchChange = viewModel::onSearchQueryChange,
            suggestedBook = viewModel.suggestedBook,
            isSearchPopulated = viewModel.searchQuery.isNotEmpty(),
            onShuffle = viewModel::suggestRandomBook,
            onClear = viewModel::clearSearch,
            onSearch = viewModel::onSearch,
            shape = RoundedCornerShapeMedium
        )

        Spacer(modifier = modifier.height(16.dp))

        when (searchState) {
            is SearchState.Success -> {
                BooksGridScreen(books = searchState.data)
            }
            is SearchState.Error -> {
                Text(text = searchState.message)
            }
            is SearchState.Loading -> {
                Text(text = viewModel.searchMessage)
            }
            is SearchState.Empty -> {
                Text(text = "Hit the shuffle button for a new suggestion")
            }
        }
    }
}

@Composable
fun BookSearchBar(
    modifier: Modifier = Modifier,
    bookSearch: String,
    onBookSearchChange: (String) -> Unit,
    suggestedBook: String,
    isSearchPopulated: Boolean = false,
    onClear: () -> Unit = {},
    onShuffle: () -> Unit,
    shape: RoundedCornerShape,
    onSearch: () -> Unit,
    maxCharacters: Int = 50 // Add a maxCharacters parameter

){
    val focusRequester = remember { FocusRequester() } // For controlling focus
    val focusManager = LocalFocusManager.current // To clear focus and dismiss keyboard

    OutlinedTextField(
        value = bookSearch,
        singleLine = true,
        placeholder = {
            Text(
                stringResource(R.string.search_hint, suggestedBook),
                maxLines = 1, // Placeholder will be on a single line
                textAlign = TextAlign.Start
            )
        },
        onValueChange = { newValue ->
            if (newValue.length <= maxCharacters) {
                onBookSearchChange(newValue) // Only update the state if the limit isn't exceeded
            }
        },
        trailingIcon = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ){
                IconButton(
                    onClick = { onShuffle() }
                ) {
                    Icon(
                        imageVector = Icons.Default.Shuffle,
                        contentDescription = stringResource(R.string.shuffle)
                    )
                }
                IconButton(
                    onClick = {
                        onSearch()
                        focusManager.clearFocus() // Clear focus when search is pressed
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(R.string.search)
                    )
                }
            }
        },
        shape = shape,
        leadingIcon = {
            IconButton(
                onClick = {
                    onClear()
                },
                enabled = isSearchPopulated
            ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = stringResource(R.string.clear)
                )
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .focusRequester(focusRequester) // Attach the focusRequester to the text field

    )
}

@Composable
fun BooksGridScreen(
    books: List<BookItem>,
    modifier: Modifier = Modifier
){
    LazyVerticalGrid(
        columns = GridCells.Adaptive(150.dp),
        modifier = modifier.padding(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ){
        items(items = books, key = { book -> book.id }){book ->
            BookItem(
                book = book,
                modifier = Modifier.padding(4.dp)
            )

        }
    }
}

@Composable
fun BookItem(
    book : BookItem,
    modifier: Modifier = Modifier
){
    Card(
        modifier = modifier
            .padding(4.dp)
            .aspectRatio(0.7f)
            .fillMaxWidth(),
        shape = RoundedCornerShapeMedium,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ){
        Box {
            if (book.volumeInfo.imageLinks != null){
                AsyncImage(
                    model = ImageRequest.Builder(context = LocalContext.current)
                        .data(book.volumeInfo.imageLinks.thumbnail?.replace("http://", "https://")) // Replacing "http" with "https"
                        .crossfade(true)
                        .build(),
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
                text = book.volumeInfo.title,
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

@Preview
@Composable
fun SearchScreenPreview(){
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        BookSearchBar(
            bookSearch = "",
            onBookSearchChange = {},
            suggestedBook = "RoundedCornerShapeSmall",
            onShuffle = {},
            onSearch = {},
            shape = RoundedCornerShapeSmall
        )
        BookSearchBar(
            bookSearch = "",
            onBookSearchChange = {},
            suggestedBook = "RoundedCornerShapeMedium",
            onShuffle = {},
            onSearch = {},
            shape = RoundedCornerShapeMedium
        )
        BookSearchBar(
            bookSearch = "",
            onBookSearchChange = {},
            suggestedBook = "RoundedCornerShapeLarge",
            onShuffle = {},
            onSearch = {},
            shape = RoundedCornerShapeLarge
        )
        BookSearchBar(
            bookSearch = "",
            onBookSearchChange = {},
            suggestedBook = "RoundedCornerShapeExtraLarge",
            onShuffle = {},
            onSearch = {},
            shape = RoundedCornerShapeExtraLarge
        )
        BookSearchBar(
            bookSearch = "",
            onBookSearchChange = {},
            suggestedBook = "RoundedCornerShapeExtraExtraLarge",
            onShuffle = {},
            onSearch = {},
            shape = RoundedCornerShapeExtraExtraLarge
        )
        BookSearchBar(
            bookSearch = "",
            onBookSearchChange = {},
            suggestedBook = "RoundedCornerShapeCircle",
            onShuffle = {},
            onSearch = {},
            shape = RoundedCornerShapeCircle
        )
    }
}
