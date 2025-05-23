package ke.don.shared_navigation.bottom_navigation.tabs.profile

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import ke.don.common_datasource.local.datastore.user_settings.AppSettings
import ke.don.feature_profile.tab.ProfileScreen
import ke.don.feature_profile.tab.ProfileTabEventHandler
import ke.don.feature_profile.tab.ProfileViewModel
import ke.don.shared_domain.states.ResultState
import ke.don.shared_navigation.OnBoardingVoyagerScreen
import ke.don.shared_navigation.R
import ke.don.shared_navigation.app_scaffold.ConfigureAppBars
import ke.don.shared_navigation.bottom_navigation.tabs.search.BookDetailsVoyagerScreen

class ProfileVoyagerScreen(): AndroidScreen(){

    @Composable
    override fun Content() {

        val navigator = LocalNavigator.current
        val viewModel: ProfileViewModel = hiltViewModel()
        val settings by viewModel.settings.collectAsState(initial = AppSettings())
        val state by viewModel.profileState.collectAsState()
        val profileEventHandler = viewModel::handleEvent

        ConfigureAppBars(
            title = stringResource(R.string.profile),
            showBottomBar = true,
            actions = {
                if(state.profileResultState is ResultState.Success){
                    IconButton(
                        onClick = {
                            profileEventHandler(ProfileTabEventHandler.ShowBottomSheet)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(R.string.settings)
                        )
                    }
                }
            }
        )

        LaunchedEffect(viewModel) {
            profileEventHandler(ProfileTabEventHandler.FetchProfile)
            profileEventHandler(ProfileTabEventHandler.FetchUserProgress)
        }

        ProfileScreen(
            onNavigateToSignIn = { navigator?.push(OnBoardingVoyagerScreen) },
            profileState = state,
            profileTabEventHandler = profileEventHandler,
            settings = settings,
            onNavigateToBook = {
                navigator?.push(BookDetailsVoyagerScreen(it))
            }
        )
    }

}