package ke.don.shared_navigation.bottom_navigation.tabs.profile

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import ke.don.feature_profile.tab.ProfileScreen
import ke.don.feature_profile.tab.ProfileTabEventHandler
import ke.don.feature_profile.tab.ProfileViewModel
import ke.don.shared_navigation.OnBoardingVoyagerScreen
import ke.don.shared_navigation.app_scaffold.ConfigureAppBars
import ke.don.shared_navigation.bottom_navigation.tabs.search.BookDetailsVoyagerScreen

@OptIn(ExperimentalMaterial3Api::class)
class ProfileVoyagerScreen(): AndroidScreen(){

    @Composable
    override fun Content() {
        ConfigureAppBars(
            title = "Profile",
            showBottomBar = true
        )
        val navigator = LocalNavigator.current
        val viewModel: ProfileViewModel = hiltViewModel()
        val state by viewModel.profileState.collectAsState()
        val profileEventHandler = viewModel::handleEvent

        LaunchedEffect(viewModel) {
            profileEventHandler(ProfileTabEventHandler.FetchProfile)
            profileEventHandler(ProfileTabEventHandler.FetchUserProgress)
        }

        ProfileScreen(
            onNavigateToSignIn = { navigator?.push(OnBoardingVoyagerScreen) },
            profileState = state,
            profileTabEventHandler = profileEventHandler,
            onNavigateToBook = {
                navigator?.push(BookDetailsVoyagerScreen(it))
            }
        )
    }

}