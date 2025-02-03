package ke.don.common_datasource.data.di

import io.github.jan.supabase.auth.auth
import ke.don.shared_domain.data_models.Profile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserManager {
    private val supabase = SupabaseClient.supabase

    private val _userProfile = MutableStateFlow<Profile?>(null)
    val userProfile: StateFlow<Profile?> = _userProfile.asStateFlow()

    fun fetchUserProfile() {
//        val currentUser = supabase.auth.currentUserOrNull()
//        _userProfile.value = currentUser?.let {
//            Profile(
//                id = it.id,
//                name = it.userMetadata?.get("full_name").toString(),
//                avatarUrl = it.userMetadata?.get("avatar_url").toString()
//            )
//        }
    }

    fun onAuthStateChange(){
        fetchUserProfile()
    }
}