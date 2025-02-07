package ke.don.shared_navigation

import android.util.Log
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.androidx.AndroidScreen
import ke.don.shared_domain.values.Screens
import ke.don.feature_authentication.presentation.OnboardingScreen
import ke.don.feature_book_details.presentation.screens.book_details.BookDetailsScreen
import ke.don.feature_book_details.presentation.screens.search.BookSearchScreen

// Voyager Screens
class BookDetailsVoyagerScreen(private val volumeId: String) : AndroidScreen() {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        BookDetailsScreen(
            onNavigateToSearch = { navigator?.pop() },
            volumeId = volumeId,
            onBackPressed = { navigator?.pop() }
        )
    }
}

object SearchVoyagerScreen : AndroidScreen() {
    private fun readResolve(): Any = SearchVoyagerScreen

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        BookSearchScreen(
            onNavigateToBookItem = { bookId ->
                Screens.BookDetails.route.replace("{bookId}", bookId)
                navigator?.push(BookDetailsVoyagerScreen(bookId))
            }
        )
    }
}

object OnBoardingVoyagerScreen : AndroidScreen() {
    private fun readResolve(): Any = OnBoardingVoyagerScreen

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        OnboardingScreen(
            onSuccessfulSignIn = {
                navigator?.replaceAll(SearchVoyagerScreen)
            }
        )
    }
}

object SplashVoyagerScreen : AndroidScreen() {
    private fun readResolve(): Any = SplashVoyagerScreen

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        PrefaceSplashScreen(
            onNavigateToMain = {
                navigator?.replaceAll(SearchVoyagerScreen)
            },
            onNavigateToOnBoarding = {
                navigator?.replaceAll(OnBoardingVoyagerScreen)
            }
        )
    }
}

@Composable
fun AppNavigation() {
    Log.d("NavGraph", "Start destination: ${Screens.Splash.route}")
    Navigator(SplashVoyagerScreen)
}
