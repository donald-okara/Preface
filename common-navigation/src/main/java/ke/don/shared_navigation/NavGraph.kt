package ke.don.shared_navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ke.don.shared_domain.values.Screens
import ke.don.feature_book_details.presentation.screens.book_details.BookDetailsScreen
import ke.don.feature_book_details.presentation.screens.search.BookSearchScreen
import ke.don.feature_authentication.presentation.OnboardingScreen

@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    //searchViewModel: SearchViewModel = hiltViewModel()
){

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Screens.Splash.route
    ) {
        val startDestinationScreen = Screens.OnBoarding.route

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

        composable(Screens.OnBoarding.route){
            OnboardingScreen()
        }


        composable(Screens.Search.route) {
            BookSearchScreen(
                onNavigateToBookItem = { bookId ->
                    navController.navigate(
                        Screens.BookDetails.route.replace(
                            oldValue = "{volumeId}",
                            newValue = bookId
                        )
                    ) {
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
                onNavigateToSearch = {
                    //searchViewModel.onSearch()
                    //searchViewModel.onSearchQueryChange(author)
                    navController.navigate(Screens.Search.route)
                },
                onBackPressed = {
                    navController.popBackStack()
                }
            )

        }
    }
}