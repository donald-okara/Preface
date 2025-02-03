package ke.don.common_datasource.remote.data.network

import android.util.Log
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.auth.user.UserInfo
import io.github.jan.supabase.postgrest.from
import ke.don.common_datasource.remote.data.di.SupabaseClient
import ke.don.shared_domain.data_models.Profile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update


class ProfileNetworkClass(
    private val supabaseClient: SupabaseClient
) {
    private val  supabase = supabaseClient.supabase

    private val _userInfo = MutableStateFlow<UserInfo?>(null)
    val userInfo:StateFlow<UserInfo?> = _userInfo


    // = supabase.auth.currentUserOrNull()

    suspend fun signIn(
        idToken: String,
        rawNonce: String
    ): Boolean{
        return try {
            supabase.auth.signInWith(IDToken) {
                this.idToken = idToken
                provider = Google
                nonce = rawNonce
            }
            _userInfo.update {
                supabase.auth.currentUserOrNull()
            }

            Log.d("ProfileNetworkClass", "User Id: ${userInfo.value?.id}")
            true
        } catch (e: Exception) {
            false
        }

    }

    suspend fun checkIfProfileIsPresent(): Boolean{
        if (userInfo.value == null) {
            Log.d("ProfileRepositoryImpl", "User is not logged in")
            return false  // Return false if user is not logged in
        }

        val response = supabase.from("profiles")
            .select {
            filter {
                Profile::name eq userInfo.value!!.id
                //or
                eq("auth_id", userInfo.value!!.id)
            }
        }
            .decodeSingleOrNull<Profile>()

        return response != null

    }

    suspend fun insertUserProfile(
        profile : Profile
    ){
        supabase.from("profiles").insert(profile)
        Log.d("ProfileRepositoryImpl", "Profile inserted successfully")
    }

}