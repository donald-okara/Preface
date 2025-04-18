package ke.don.feature_profile.tab

sealed class ProfileTabEventHandler {
    data object ShowBottomSheet : ProfileTabEventHandler()
    data object FetchProfile : ProfileTabEventHandler()
    data object FetchUserProgress : ProfileTabEventHandler()
    data class SignOut(val onSignOut: () -> Unit) : ProfileTabEventHandler()
    data class DeleteUser(val onSignOut: () -> Unit) : ProfileTabEventHandler()
}