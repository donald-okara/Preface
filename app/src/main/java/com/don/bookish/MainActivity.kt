package com.don.bookish

import android.os.Bundle
import android.util.Log
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
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
import kotlinx.coroutines.delay

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
                        startDestination = BookishScreen.Splash.name
                    ) {
                        composable(BookishScreen.Splash.name) {
                            BookishSplashScreen(
                                onNavigateToMain = {
                                    navController.navigate(startDestination.name){
                                        // Clear splash screen from back stack
                                        popUpTo(BookishScreen.Splash.name) { inclusive = true }
                                    }
                                }
                            )
                        }

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
                            val initialBookId = backStackEntry.arguments?.getString("bookId")

                            // Use rememberSaveable to persist the bookId across configuration changes
                            val bookId = rememberSaveable { mutableStateOf(initialBookId) }
                            LaunchedEffect(bookId) {
                                Log.d("MainActivity", "Attempting to navigate to: $bookId")
                            }
                            bookId.value?.let {
                                BookDetailsScreen(
                                    bookId = it,
                                    bookDetailsViewModel = bookDetailsViewModel,
                                    onSearchAuthor = { author ->
                                        searchViewModel.onSearch()
                                        searchViewModel.onSearchQueryChange(author)
                                        navController.navigate(BookishScreen.Search.name)
                                    },
                                    bookState = bookDetailsViewModel.bookState,
                                    onBackPressed = {
                                        navController.popBackStack()
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
    Splash,
    Search,
    Details, // Future screens can be added here
}

@Composable
fun BookishSplashScreen(
    modifier: Modifier = Modifier,
    onNavigateToMain : () -> Unit
) {
    val scale = remember{
        Animatable(initialValue = 0f)
    }

    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 500,
                easing = {
                    OvershootInterpolator(2f).getInterpolation(it)
                }
            )
        )
        delay(3000L)
        onNavigateToMain()
    }


    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .background(color = colorResource(R.color.brown))
    ){
        Image(
            painter = painterResource(id = R.drawable.splash_screen2),
            contentDescription = null,
            modifier = modifier
                .fillMaxSize()
                .scale(scale.value)
        )
    }
}