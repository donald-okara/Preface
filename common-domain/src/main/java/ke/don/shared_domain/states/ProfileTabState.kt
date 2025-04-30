package ke.don.shared_domain.states

import ke.don.shared_domain.data_models.ProfileDetails
import ke.don.shared_domain.data_models.UserProgressBookView

data class ProfileTabState(
    val profile: ProfileDetails = ProfileDetails(),
    val showBottomSheet: Boolean = false,
    val isRefreshing: Boolean = false,
    val profileResultState: ResultState = ResultState.Loading,
    val userProgress: List<UserProgressBookView> = emptyList(),
    val progressResultState: ResultState = ResultState.Loading
)
