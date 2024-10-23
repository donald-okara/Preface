package com.don.bookish

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.don.bookish.presentation.book_details.BookDetailsScreen
import com.don.bookish.presentation.book_details.BookDetailsViewModel
import com.don.bookish.presentation.search.BookSearchScreen
import com.don.bookish.presentation.search.SearchViewModel
import com.don.bookish.ui.theme.BookishTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {


            BookishTheme {
                val searchViewModel = hiltViewModel<SearchViewModel>()
                val bookDetailsViewModel = hiltViewModel<BookDetailsViewModel>()

                val navController = rememberNavController()
                val startDestination = BookishScreen.Search
                Surface(modifier = Modifier.fillMaxSize()) {
                    NavHost(
                        navController = navController,
                        startDestination = startDestination.name
                    ) {

                        composable(BookishScreen.Search.name) {
                            BookSearchScreen(
                                searchState = searchViewModel.searchUiState,
                                viewModel = searchViewModel,
                                onNavigateToBookItem = { bookId ->
                                    navController.navigate("${BookishScreen.Details.name}/$bookId")
                                }
                            )
                        }
                        // Example of a future screen (e.g., book details)
                        composable(BookishScreen.Details.name+"/{bookId}") {backStackEntry ->
                            val bookId = backStackEntry.arguments?.getString("bookId")

                            LaunchedEffect(bookId) {
                                Log.d("MainActivity", "Attempting to navigate to: $bookId")
                            }
                            if (bookId != null) {
                                BookDetailsScreen(
                                    bookId = bookId ,
                                    viewModel = bookDetailsViewModel,
                                    onSearchAuthor = { author ->
                                        searchViewModel.onSearch()
                                        searchViewModel.onSearchQueryChange(author)
                                        navController.navigate(BookishScreen.Search.name)
                                    }
                                )
                            }

                        }
                    }
                }
            }
        }
    }
}

enum class BookishScreen {
    Search,
    Details, // Future screens can be added here
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BookishTheme {
        Greeting("Android")
    }
}