package com.don.preface.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.don.preface.PrefaceScreens
import com.don.preface.PrefaceSplashScreen
import com.don.preface.presentation.screens.book_details.BookDetailsScreen
import com.don.preface.presentation.screens.search.BookSearchScreen
import com.don.preface.presentation.screens.search.SearchViewModel

@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    searchViewModel: SearchViewModel
){
    NavHost(
        navController = navController,
        startDestination = Screens.Splash.route
    ) {
        val startDestinationScreen = Screens.Search.route

        composable(Screens.Splash.route) {
            PrefaceSplashScreen(
                onNavigateToMain = {
                    navController.navigate(startDestinationScreen){
                        // Clear splash screen from back stack
                        popUpTo(Screens.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screens.Search.route) {
            BookSearchScreen(
                onNavigateToBookItem = { bookId ->
                    navController.navigate(
                        Screens.BookDetails.route.replace(
                            oldValue = "{volumeId}",
                            newValue = bookId
                        )
                    ){
                        launchSingleTop = true
                        restoreState = true
                    }
                }

            )

        }


        composable(
            Screens.BookDetails.route,
            arguments = listOf(
                navArgument(Screens.BookDetails.volumeIdNavigationArgument) {
                    type = NavType.StringType
                }
            )
        ) {
            BookDetailsScreen(
                onSearchAuthor = { author ->
                    searchViewModel.onSearch()
                    searchViewModel.onSearchQueryChange(author)
                    navController.navigate(PrefaceScreens.Search.name)
                },
                onBackPressed = {
                    navController.popBackStack()
                }
            )

        }
    }
}