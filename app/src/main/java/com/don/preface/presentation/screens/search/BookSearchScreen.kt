package com.don.preface.presentation.screens.search

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.don.preface.R
import com.don.preface.data.model.BookItem
import com.don.preface.presentation.screens.search.components.BookSearchBar
import com.don.preface.presentation.screens.search.components.BooksGridScreen
import com.don.preface.ui.theme.BookishTheme
import com.don.preface.ui.theme.RoundedCornerShapeCircle
import com.don.preface.ui.theme.RoundedCornerShapeExtraExtraLarge
import com.don.preface.ui.theme.RoundedCornerShapeExtraLarge
import com.don.preface.ui.theme.RoundedCornerShapeLarge
import com.don.preface.ui.theme.RoundedCornerShapeMedium
import com.don.preface.ui.theme.RoundedCornerShapeSmall
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookSearchScreen(
    modifier: Modifier = Modifier,
    searchState: StateFlow<SearchState>,
    viewModel: SearchViewModel,
    onNavigateToBookItem: (String) -> Unit
) {

    val currentBookState by searchState.collectAsState()

    LaunchedEffect(searchState) {
        Log.d("SearchState", "searchState: $searchState")
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Search",
                        modifier = Modifier.padding(8.dp),
                        maxLines = 1
                    )
                }
            )
        }
    ) { innerPadding->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BookSearchBar(
                bookSearch = viewModel.searchQuery,
                onBookSearchChange = viewModel::onSearchQueryChange,
                suggestedBook = viewModel.suggestedBook,
                isSearchPopulated = viewModel.searchQuery.isNotEmpty(),
                onShuffle = viewModel::shuffleBook,
                onClear = viewModel::clearSearch,
                onSearch = viewModel::onSearch,
                shape = RoundedCornerShapeMedium
            )

            Spacer(modifier = modifier.height(16.dp))

            when (currentBookState) {
                is SearchState.Success -> {
                    if ((currentBookState as SearchState.Success).data.isEmpty()) {
                        Text(text = "No books found. Try searching for something else.")
                    }else{
                        BooksGridScreen(
                            books = (currentBookState as SearchState.Success).data,
                            onNavigateToBookItem = onNavigateToBookItem
                        )
                    }


                }
                is SearchState.Error ->{
                    SearchErrorScreen(
                        text = (currentBookState as SearchState.Error).message,
                        onRefresh = viewModel::onSearch

                    )
                }

                is SearchState.Loading -> {
                    SearchLoadingScreen(
                      text = viewModel.searchMessage
                    )
                }
                is SearchState.Empty -> {
                    Text(text = "Hit the shuffle button for a new suggestion")
                }
            }

        }
    }

}

@Composable
fun SearchErrorScreen(
    modifier: Modifier = Modifier,
    text: String,
    onRefresh: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
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

@Composable
fun SearchLoadingScreen(
    modifier: Modifier = Modifier,
    text: String,
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
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
            modifier = modifier
                .padding(bottom = 16.dp)
        )

    }
}



@Preview
@Composable
fun LoadingScreenPreview(){
    BookishTheme {
        SearchLoadingScreen(
            text = "Loading some jokes for you"
        )
    }
}

@Preview
@Composable
fun ErrorScreenPreview(){
    BookishTheme {
        SearchErrorScreen(
            onRefresh = {},
            text = "Something went wrong"
        )
    }
}

