package ke.don.feature_profile.tab

sealed class ProfileTabEventHandler {
    data object FetchProfile : ProfileTabEventHandler()
    data class SignOut(val onSignOut: () -> Unit) : ProfileTabEventHandler()
    data class DeleteUser(val onSignOut: () -> Unit) : ProfileTabEventHandler()
}