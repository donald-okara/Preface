package ke.don.common_datasource.remote.data.profile.network

import android.content.Context
import android.util.Log
import android.widget.Toast
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.auth.user.UserInfo
import io.github.jan.supabase.postgrest.from
import ke.don.common_datasource.local.datastore.profile.profileDataStore
import ke.don.common_datasource.local.datastore.token.TokenData
import ke.don.common_datasource.local.datastore.token.TokenDatastoreManager
import ke.don.common_datasource.local.datastore.token.tokenDatastore
import ke.don.shared_domain.data_models.BookShelf
import ke.don.shared_domain.data_models.BookshelfCatalog
import ke.don.shared_domain.data_models.BookshelfRef
import ke.don.shared_domain.data_models.Profile
import ke.don.shared_domain.data_models.SupabaseBook
import ke.don.shared_domain.values.BOOKS
import ke.don.shared_domain.values.BOOKSHELFCATALOG
import ke.don.shared_domain.values.BOOKSHELFTABLE
import ke.don.shared_domain.values.MAX_RETRIES
import ke.don.shared_domain.values.PROFILESTABLE
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update


class ProfileNetworkClass(
    private val context: Context,
    private val supabase: SupabaseClient,
    private val tokenDatastore: TokenDatastoreManager
) {
    private val _userInfo = MutableStateFlow<UserInfo?>(null)
    private val userInfo:StateFlow<UserInfo?> = _userInfo

    /**
     * CREATE
     */
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

    suspend fun insertUserProfile(
        profile : Profile
    ){
        try {
            supabase.from(PROFILESTABLE).insert(profile)
            Log.d(TAG, "Profile inserted successfully")
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to create profile. Please try again later", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * READ
     */
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

        val signInStatus = context.reloadSession(supabase)
        Log.d(TAG, "User is logged in: $signInStatus")

        return signInStatus

    }
    // TODO: Do not deploy to playstore until we have revisited https://chatgpt.com/share/67ac894b-7928-8000-a399-13809feeb1f0

    /**
     * UPDATE
     */

    /**
     * DELETE
     */


    companion object {
        private const val TAG = "ProfileNetworkClass"
    }

}

suspend fun Context.reloadSession(
    supabase: SupabaseClient
): Boolean {
    val TAG = "Context"
    val maxRetries = MAX_RETRIES
    var attempt = 0

    while (attempt < maxRetries) {
        try {
            val token = tokenDatastore.data.first().token
            Log.d(TAG, "Attempt ${attempt + 1} - Token: $token")

            if (!token.isNullOrEmpty()) {
                supabase.auth.refreshCurrentSession()

                val newAccessToken = supabase.auth.currentAccessTokenOrNull()
                Log.d(TAG, "New token present : ${!newAccessToken.isNullOrEmpty()}")

                if (!newAccessToken.isNullOrEmpty()) {
                    tokenDatastore.updateData {
                        it.copy(token = newAccessToken)
                    }
                    return true
                }

                Log.d(TAG, "Failed to refresh token, retrying...")
            } else {
                Log.d(TAG, "Token is null or empty")
                return false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error reloading session on attempt ${attempt + 1}", e)
        }

        attempt++
        delay(2000) // Wait for 2 seconds before retrying
    }

    // If all retries fail, sign out the user
    Log.d(TAG, "Max retries reached, forcing re-authentication")
    supabase.auth.signOut()

    // Clear stored token
    tokenDatastore.updateData { TokenData() }
    profileDataStore.updateData { Profile() }

    return false
}
