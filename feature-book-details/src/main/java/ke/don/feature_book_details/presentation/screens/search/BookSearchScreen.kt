package ke.don.feature_book_details.presentation.screens.search

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ke.don.shared_domain.states.SearchState
import ke.don.feature_book_details.presentation.screens.search.components.BookSearchBar
import ke.don.feature_book_details.presentation.screens.search.components.BooksGridScreen

@Composable
fun BookSearchScreen(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues,
    viewModel: SearchViewModel = hiltViewModel(),
    onNavigateToBookItem: (String) -> Unit
) {

    val searchState by viewModel.searchUiState.collectAsState()
    val searchMessage by viewModel.searchMessage.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val suggestedBook by viewModel.suggestedBook.collectAsState()
    val isSearchPopulated by viewModel.isSearchPopulated.collectAsState()

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .padding(paddingValues)
    ){
        LookaheadScope{
            Column(
                modifier = modifier
                    .padding(8.dp)
                    .animateContentSize(animationSpec = tween(1000)),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BookSearchBar(
                    bookSearch = searchQuery,
                    onBookSearchChange = viewModel::onSearchQueryChange,
                    suggestedBook = suggestedBook,
                    isSearchPopulated = isSearchPopulated,
                    onShuffle = viewModel::shuffleBook,
                    onClear = viewModel::clearSearch,
                    onSearch = viewModel::onSearch,
                    shape = RoundedCornerShape(16.dp),
                    modifier = modifier
                        .padding(8.dp)
                )

                Spacer(modifier = modifier.padding(8.dp))
                when (searchState) {
                    is SearchState.Success -> {
                        if ((searchState as SearchState.Success).data.isEmpty()) {
                            Text(text = "No books found. Try searching for something else.")
                        } else {
                            BooksGridScreen(
                                books = (searchState as SearchState.Success).data,
                                onNavigateToBookItem = onNavigateToBookItem
                            )
                        }

                    }

                    is SearchState.Error -> {
                        SearchErrorScreen(
                            text = (searchState as SearchState.Error).message,
                            onRefresh = viewModel::onSearch

                        )
                    }

                    is SearchState.Loading -> {
                        SearchLoadingScreen(
                            text = searchMessage
                        )
                    }

                    is SearchState.Empty -> {
                        Text(text = "Hit the shuffle button for a new suggestion")
                    }
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



