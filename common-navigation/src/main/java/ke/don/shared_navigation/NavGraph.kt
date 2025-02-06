package ke.don.shared_navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
    navViewModel: NavViewModel = hiltViewModel()
) {
    val isUserSignedIn by navViewModel.isSignedIn.collectAsState()

    val startDestinationScreen = Screens.Splash.route


    Log.d("NavGraph", "Start destination: $startDestinationScreen")

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Screens.Splash.route
    ) {

        composable(Screens.Splash.route) {
            PrefaceSplashScreen(
                onNavigateToMain = {
                    if (isUserSignedIn) {
                        navController.navigate(Screens.Search.route) {
                            popUpTo(Screens.Splash.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screens.OnBoarding.route) {
                            popUpTo(Screens.Splash.route) { inclusive = true }
                        }
                    }
                }
            )
        }
        composable(Screens.OnBoarding.route) {
            OnboardingScreen(
                onSuccessfulSignIn = {
                    navController.navigate(Screens.Search.route) {
                        popUpTo(Screens.OnBoarding.route) { inclusive = true }
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