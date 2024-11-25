package com.don.preface

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
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.don.preface.data.model.BookItem
import com.don.preface.presentation.screens.book_details.BookDetailsScreen
import com.don.preface.presentation.screens.book_details.BookDetailsViewModel
import com.don.preface.presentation.screens.search.BookSearchScreen
import com.don.preface.presentation.screens.search.SearchViewModel
import com.don.preface.presentation.utils.network_checks.isInternetAvailable
import com.don.preface.presentation.utils.network_checks.isNetworkAvailable
import com.don.preface.ui.theme.PrefaceTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PrefaceTheme {
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
                            PrefaceSplashScreen(
                                onNavigateToMain = {
                                    navController.navigate(startDestination.name){
                                        // Clear splash screen from back stack
                                        popUpTo(BookishScreen.Splash.name) { inclusive = true }
                                    }
                                },
                                modifier = Modifier.testTag("SplashScreen")
                            )
                        }

                        composable(BookishScreen.Search.name) {
                            val context = applicationContext
                            val coroutineScope = rememberCoroutineScope()
                            val snackbarHostState = remember { SnackbarHostState() }

                            lateinit var internetAvailability: suspend () -> Boolean

                            lifecycleScope.launch {
                                internetAvailability = {
                                    isInternetAvailable()
                                }
                            }

                            BookSearchScreen(
                                searchState = searchViewModel.searchUiState,
                                viewModel = searchViewModel,
                                onNavigateToBookItem = { book ->
                                    coroutineScope.launch {
                                        if (context.isNetworkAvailable() && internetAvailability()) {
                                            navController.currentBackStackEntry?.savedStateHandle?.set("book", book)
                                            navController.navigate(BookishScreen.Details.name)
                                        } else {
                                            // Show an error message or navigate to an offline error screen
                                            snackbarHostState.showSnackbar(
                                                message = "No internet connection!",
                                                duration = SnackbarDuration.Long
                                            )
                                        }
                                      }
                                }

                            )
                            // SnackbarHost at the bottom of the screen
                            SnackbarHost(
                                hostState = snackbarHostState,
                                modifier = Modifier
                                    .padding(top = 16.dp) // Optional padding for spacing
                            )
                        }


                        composable(BookishScreen.Details.name) {
                            val book = navController.previousBackStackEntry?.savedStateHandle?.get<BookItem>("book")
                            val context = applicationContext

                            if (book != null && context.isNetworkAvailable()) {
                                BookDetailsScreen(
                                    book = book,
                                    bookDetailsViewModel = bookDetailsViewModel,
                                    onSearchAuthor = { author ->
                                        searchViewModel.onSearch()
                                        searchViewModel.onSearchQueryChange(author)
                                        navController.navigate(BookishScreen.Search.name)
                                    },
                                    //bookState = bookDetailsViewModel.bookState,
                                    onBackPressed = {
                                        navController.popBackStack()
                                    }
                                )
                            } else {
                                // Handle null case
                                Log.e("MainActivity", "Failed to retrieve book data")
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
fun PrefaceSplashScreen(
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
            painter = painterResource(id = R.drawable.splash_screen_2),
            contentDescription = "Splash Screen",
            modifier = modifier
                .fillMaxSize()
                .scale(scale.value)
        )
    }
}