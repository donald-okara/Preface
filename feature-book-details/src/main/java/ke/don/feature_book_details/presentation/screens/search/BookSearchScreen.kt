package ke.don.feature_book_details.presentation.screens.search

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ManageSearch
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ke.don.feature_book_details.R
import ke.don.feature_book_details.presentation.screens.search.components.BookList
import ke.don.feature_book_details.presentation.screens.search.components.BookSearchBar
import ke.don.shared_components.components.EmptyScreen
import ke.don.shared_domain.states.ResultState
import ke.don.shared_domain.states.SearchState

@Composable
fun BookSearchScreen(
    modifier: Modifier = Modifier,
    searchState: SearchState,
    eventHandler: (SearchEventHandler) -> Unit,
    paddingValues: PaddingValues = PaddingValues(),
    onNavigateToBookItem: (String) -> Unit
) {
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
                    searchQuery = searchState.searchQuery,
                    onBookSearchChange = { eventHandler(SearchEventHandler.OnSearchQueryChange(it)) },
                    suggestedBook = searchState.suggestedBook,
                    isSearchPopulated = searchState.searchQuery.isNotEmpty(),
                    onShuffle = {eventHandler(SearchEventHandler.Shuffle)},
                    onClear = {eventHandler(SearchEventHandler.ClearSearch)},
                    onSearch = {eventHandler(SearchEventHandler.Search)},
                    shape = RoundedCornerShape(16.dp),
                    modifier = modifier
                        .padding(8.dp)
                )

                Spacer(modifier = modifier.padding(8.dp))
                when (searchState.resultState) {
                    is ResultState.Success -> {
                        if (searchState.data.isEmpty()) {
                            EmptyScreen(
                                icon = Icons.Outlined.SearchOff,
                                message = stringResource(R.string.no_books),
                                action = {},
                                actionText = ""
                            )
                        } else {
                            BookList(
                                books = searchState.data,
                                onNavigateToBookItem = onNavigateToBookItem
                            )
                        }

                    }

                    is ResultState.Error -> {
                        EmptyScreen(
                            icon = Icons.Outlined.SearchOff,
                            message = stringResource(R.string.something_went_wrong_please_try_again),
                            action = {},
                            actionText = searchState.errorMessage
                        )
                    }

                    is ResultState.Loading -> {
                        EmptyScreen(
                            icon = Icons.Outlined.ManageSearch,
                            message = searchState.searchMessage,
                            action = {},
                            actionText = ""
                        )
                    }

                    is ResultState.Empty -> {
                        Text(text = stringResource(R.string.hit_the_shuffle))
                    }
                }

            }
        }
    }


}



