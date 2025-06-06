package ke.don.feature_profile.tab

import androidx.fragment.app.FragmentActivity

sealed class ProfileTabEventHandler {
    data object ShowBottomSheet : ProfileTabEventHandler()
    data object FetchProfile : ProfileTabEventHandler()
    data object FetchUserProgress : ProfileTabEventHandler()
    data class SignOut(val onSignOut: () -> Unit) : ProfileTabEventHandler()
    data class DeleteUser(val activity: FragmentActivity, val onSignOut: () -> Unit) : ProfileTabEventHandler()
    data object ToggleDarkTheme: ProfileTabEventHandler()
}