package ke.don.shared_domain.states

import ke.don.shared_domain.data_models.ProfileDetails

data class ProfileTabState(
    val profile: ProfileDetails = ProfileDetails(),
    val showBottomSheet: Boolean = false,
    val resultState: ResultState = ResultState.Empty
)
