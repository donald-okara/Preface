package ke.don.common_datasource.remote.data.network

import android.content.Context
import android.util.Log
import android.widget.Toast
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.auth.user.UserInfo
import io.github.jan.supabase.postgrest.from
import ke.don.common_datasource.local.datastore.TokenData
import ke.don.common_datasource.local.datastore.TokenDatastoreManager
import ke.don.common_datasource.local.datastore.profileDataStore
import ke.don.common_datasource.local.datastore.tokenDatastore
import ke.don.common_datasource.remote.data.di.SupabaseClient
import ke.don.shared_domain.data_models.Profile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update


class ProfileNetworkClass(
    private val context: Context,
    private val supabaseClient: SupabaseClient,
    private val tokenDatastore: TokenDatastoreManager
) {
    private val supabase = supabaseClient.supabase

    private val _userInfo = MutableStateFlow<UserInfo?>(null)
    private val userInfo:StateFlow<UserInfo?> = _userInfo

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

            tokenDatastore.setToken(
                supabase.auth.currentAccessTokenOrNull()
            )

            Log.d(TAG, "User Id is present: ${userInfo.value != null}")
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }

    }

    suspend fun checkIfProfileIsPresent(): Boolean{
        return try {
            if (userInfo.value == null) {
                Log.d(TAG, "User is not logged in")
                return false  // Return false if user is not logged in
            }

            val response = supabase.from("profiles")
                .select {
                filter {
                    Profile::authId eq userInfo.value!!.id
                    //or
                    eq("auth_id", userInfo.value!!.id)
                }
            }
                .decodeSingleOrNull<Profile>()

            response != null
        } catch (e: Exception) {

            e.printStackTrace()
            false
        }

    }

    suspend fun insertUserProfile(
        profile : Profile
    ){
        try {
            supabase.from("profiles").insert(profile)
            Log.d(TAG, "Profile inserted successfully")
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to create profile. Please try again later", Toast.LENGTH_SHORT).show()
        }
    }

    suspend fun fetchUserProfile(): Profile? {
        return if (userInfo.value == null) {
            Log.d("ProfileRepositoryImpl", "User is not logged in")
            null
        } else {
            val response = supabase.from("profiles")
                .select {
                    filter {
                        Profile::authId eq userInfo.value!!.id
                        //or
                        eq("auth_id", userInfo.value!!.id)
                    }
                }
                .decodeSingleOrNull<Profile>()

            response

        }
    }

    suspend fun checkSignedInStatus(): Boolean {

        val signInStatus = context.reloadSession(supabaseClient)
        Log.d("ProfileRepositoryImpl", "User is logged in: $signInStatus")

        return signInStatus

    }

    companion object {
        private const val TAG = "ProfileNetworkClass"
    }

}

suspend fun Context.reloadSession(
    supabaseClient: SupabaseClient
): Boolean {
    val TAG = "Context"

    return try {
        val token = tokenDatastore.data.first().token
        Log.d(TAG, "Token: $token")

        if (!token.isNullOrEmpty()) {
            supabaseClient.supabase.auth.refreshCurrentSession()

            val newAccessToken = supabaseClient.supabase.auth.currentAccessTokenOrNull()
            Log.d(TAG, "New token present : ${!newAccessToken.isNullOrEmpty()}")

            if (newAccessToken.isNullOrEmpty()) {
                Log.d(TAG, "Failed to refresh token, forcing re-authentication")
                // Sign out the user completely
                supabaseClient.supabase.auth.signOut()

                // Clear stored token
                tokenDatastore.updateData { TokenData() }
                profileDataStore.updateData { Profile() }
                return false
            }

            tokenDatastore.updateData {
                it.copy(token = newAccessToken)
            }

            true
        } else {
            Log.d(TAG, "Token is null or empty")
            false
        }
    } catch (e: Exception) {
        Log.e(TAG , "Error reloading session", e)
        false
    }
}
